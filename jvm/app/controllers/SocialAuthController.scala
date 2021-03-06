package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.Inject
import models.User
import play.api.i18n.Messages.Message
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import repos.UserRepository
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class SocialAuthController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserRepository,
  authInfoRepository: AuthInfoRepository,
  socialProviderRegistry: SocialProviderRegistry
)(
  implicit
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with Logger {

  def createUser(profile: CommonSocialProfile) = {
    val name = profile.firstName -> profile.lastName match {
      case (Some(f), Some(l)) => f + " " + l
      case (Some(f), None) => f
      case (None, Some(l)) => l
      case _ => ""
    }
    User(UUID.randomUUID(), 0, name, profile.email.get, profile.avatarURL, true, profile.loginInfo)
  }

  def authenticate(provider: String) = Action.async { implicit request: Request[AnyContent] =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) =>
            p.retrieveProfile(authInfo).flatMap(profile => {
              userService.retrieve(profile.email.get).flatMap {
                case Some(exsitingUser) if exsitingUser.loginInfo != profile.loginInfo =>
                  Future.successful(Redirect(routes.SignInController.view()).flashing("error" -> Messages("email.taken")))
                case _ =>
                  for {
                    user <- {
                      val obj = createUser(profile)
                      userService.create(obj, authInfo, userService.newUserDocument())
                    }
                    authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)
                    value <- silhouette.env.authenticatorService.init(authenticator)
                    result <- silhouette.env.authenticatorService.embed(value, Redirect(routes.ApplicationController.default()))
                  } yield {
                    silhouette.env.eventBus.publish(LoginEvent(user, request))
                    result
                  }
              }
            })
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Redirect(routes.SignInController.view()).flashing("error" -> Messages("could.not.authenticate"))
    }
  }
}
