package web.view

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import client._
import client.Client
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Reusability
import api.{Api, Authentication}
import sun.text.normalizer.ICUBinary.Authenticate
import model._
import model.data

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success}


object ClientView {


  private implicit val reusabilityClient = Reusability.always[Client]
  private implicit val reusabilityClientState = Reusability.by_==[ClientState]

  private val creator = ObserverView[Client, ClientState, ClientView](
    ScalaComponent.builder[Client]("ClientView"),
    s => new ClientView(s),
    client => client.doc,
    onStart = _.start(),
    onStop = _.stop()
  ).configure(Reusability.shouldComponentUpdate)
    .build

  def apply(c: Client): Unmounted[Client, ClientState, ClientView] = creator(c)
}

class ClientView(override val $: BackendScope[Client, ClientState]) extends ObserverView[Client, ClientState] {

  val random = new Random()
  def render(client: Client, state: ClientState): VdomElement = {
    div(
      div(s"client ${client.debug_authentication}, version ${client.debug_committedVersion}"),
      button("change content", onClick ==> (_ => Callback {
        client.change(operation.Node.randomTransaction(3, state.node, random))
      })),
      div(
        contentEditable := true,
        SimpleTreeView(state.node)
      )
    )
  }
}
