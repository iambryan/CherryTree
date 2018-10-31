package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}
import repos.UserRepository
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class ApplicationController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  users: UserRepository
)(implicit assets: AssetsFinder, ex: ExecutionContext) extends AbstractController(components) with I18nSupport {

  def index = silhouette.SecuredAction.async { implicit request =>
    users.indexDocumentId(request.identity.userId).map {
      case Some(documentId) =>
        Redirect(controllers.routes.DocumentController.index(documentId))
      case None =>
        ??? /// create a document list page
    }
  }


  def signOut = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}