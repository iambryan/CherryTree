package web.view

import controller.client.Client
import web.net.JsAutowireClient
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.BackendScope
import controller.client.ClientInitializer
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react._
import controller.api.{Api, Authentication}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


object ClientInitializerView {

  private val creator = ScalaComponent
    .builder[Authentication.Token]("ClientInitializerView")
    .initialState(None.asInstanceOf[Option[Client]])
    .renderBackend[ClientInitializerView]
    .componentDidMount(_.backend.start)
    .componentWillUnmount(_.backend.stop)
    .build
  def apply(token: Authentication.Token) = creator(token)
}

class ClientInitializerView($: BackendScope[Authentication.Token, Option[Client]]) {

  private val server = new JsAutowireClient()[Api]

  def stop = Callback {
  }

  def start: Callback = $.props.map { p =>
    ClientInitializer.init(server, p).onComplete {
      case Success(client) =>
        $.setState(Some(client)).runNow()
      case Failure(exception) =>
        exception.printStackTrace()
    }
  }

  def render(token: Authentication.Token, client: Option[Client]): VdomElement = {
    client match {
      case Some(c) =>
        ClientView(c)
      case None =>
        div(s"$token Connecting to server")
    }
  }
}
