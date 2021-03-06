package web.ui.panel

import client.Client
import command.{Command, Key}
import org.scalajs.dom.raw.{HTMLElement, MouseEvent}
import scalatags.JsDom.all._
import web.view.{UnselectableView, View}

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js

class CommandListPanel(val client: Client, doc: => View) extends UnselectableView  {


  dom = div(
    minWidth := "150px",
    width := "100%",
    height := "100%",
    whiteSpace := "pre-wrap",
    overflowY := "scroll",
    cls := "ct-scroll ct-panel",
    padding := "24px",
  ).render

  private val bf = new ArrayBuffer[(Command, HTMLElement)]


  private val onClick: js.Function1[MouseEvent, _] = (ev: MouseEvent) => {
    val cmd = ev.currentTarget.asInstanceOf[js.Dynamic].command.asInstanceOf[command.Command]
    client.commands.runTextualIfAvailable(cmd)
    doc.focus()
  }

  private val onPanelClick: js.Function1[MouseEvent, _] = (ev: MouseEvent) => {
    ev.currentTarget.asInstanceOf[HTMLElement].parentElement.classList.toggle("hide-children")
    doc.focus()
  }

  val res = div(
    client.commands.commandsByCategory.map {
      case (name, commands) =>
        if (commands.isEmpty) {
          div()
        } else {
          div(
            cls := "hide-children",
            marginBottom := "8px",
            {
              val h = h6(
                display := "flex",
                flexDirection := "row",
                alignItems := "center",
                cls := "ct-flat-selectable",
                marginLeft := "-6px",
                marginRight := "-6px",
                marginBottom := "0px",
                paddingLeft := "6px",
                paddingRight := "6px",
                paddingTop := "4px",
                paddingBottom := "4px",
                i(cls := "ct-general-hold"),
                div(marginLeft := "4px", name)
                ).render
              h.addEventListener("click", onPanelClick)
              h: Frag
            },
            commands.filter(_.description.nonEmpty).map(c => {
              val dom = div(
                p(marginLeft := "8px",
                  marginBottom := "0px",
                  paddingLeft := "4px",
                  paddingRight := "4px",
                  paddingTop := "5px",
                  paddingBottom := "5px",
                  Some(span(tag("kbd")(cls := "ct-kbd2", "N", title := "this command is repeatable when prefixed by a number"), " ")).filter(_ => client.enableModal && c.repeatable),
                  if (c.actDoubleClick) span(tag("kbd")(cls := "ct-kbd2", "\uD83D\uDDB1️double", title := "double click"), " ") else Seq.empty[Frag] : Frag,
                  if (c.actTripleClick) span(tag("kbd")(cls := "ct-kbd2", "\uD83D\uDDB1triple", title := "triple click"), " ") else Seq.empty[Frag] : Frag,
                  c.inputRule.map(a => span(tag("kbd")(cls := "ct-kbd", a.shortDesc, title := a.longDesc), " ")),
                  c.textCommand.map(a => span(tag("kbd")(cls := "ct-kbd", ":" + a, title := "use command menu to invoke this command"), " ")),
                  c.keysOn(client.enableModal).map(a => span(tag("kbd")(cls := "ct-kbd", Key.toString(a)), " ")),
                  if (c.needsChar) span(tag("kbd")(cls := "ct-kbd2", "char", title := "this command needs have a char as argument"), " ") else Seq.empty[Frag] : Frag,
                  if (c.needsMotion) span(tag("kbd")(cls := "ct-kbd2", "motion", title := "this command needs a motion as argument"), " ") else Seq.empty[Frag] : Frag,
                  c.description)
              ).render
              dom.asInstanceOf[js.Dynamic].command = c.asInstanceOf[scala.scalajs.js.Any]
              if (!c.needsStuff) {
                dom.className = "ct-flat-selectable"
                dom.addEventListener("click", onClick)
              }
              bf.append((c, dom))
              dom: Frag
            })
          )
        }
    }.toSeq
  ).render

  dom.appendChild(res)


  observe(client.stateUpdates.map(_ => 0).startWith(Seq(0)).doOnNext(pair => {
    for (p <- bf) {
      val av = p._1.available(client.state, client.commands)
      if (av) {
        p._2.classList.remove("ct-flat-disabled")
      } else {
        p._2.classList.add("ct-flat-disabled")
      }
    }
  }))

}
