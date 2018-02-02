package client.view

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import shared.client._
import shared.data.ClientState
import shared.client.Client
import client.net.JsAutowireAdapter
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.BackendScope
import shared.Api
import shared.data._
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Reusability

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


object ClientView {


  private implicit val reusabilityClient = Reusability.always[Client]
  private implicit val reusabilityClientState = Reusability.by_==[ClientState]

  private val creator = ObservingView[Client, ClientState, ClientView](
    ScalaComponent.builder[Client]("ClientView"),
    s => new ClientView(s),
    client => client.state,
    onStart = _.start(),
    onStop = _.stop()
  ).configure(Reusability.shouldComponentUpdate)
    .build

  def apply(c: Client): Unmounted[Client, ClientState, ClientView] = creator(c)
}

class ClientView(override val $: BackendScope[Client, ClientState]) extends ObservingView[Client, ClientState] {


  def render(client: Client, state: ClientState): VdomElement = {
    div(
      div(s"client ${state.authentication}, version ${state.document.version}"),
      button("change content", onClick ==> (_ => Callback {
        client.change(shared.test.randomTwoChangeTransaction(client.state.get.document.root))
      })),
      SimpleTreeView(state.document.root)
    )
  }
}
