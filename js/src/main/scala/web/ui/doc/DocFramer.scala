package web.ui.doc

import java.util.UUID

import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.raw
import org.scalajs.dom.raw._
import scalatags.JsDom.all.{tag, _}
import web.view.View
import web.ui.content.{ContentView, ContentViewCreator}
import web.ui.content.ContentView.General

import scala.scalajs.js

trait DocFramer extends ContentViewCreator {

  val docFramerIsSmall: Int = 0
  val docFramerExtraClass: String = ""

  protected val parentHeadingLevel: Int = -1

  val latexMacroCache: LaTeXMacroCache

  val onClick: UUID => Unit = null
  val onDoubleClick: UUID => Unit = null

  val useFoldedIcon: Boolean = false

  def contentViewFromWithHold(a: raw.Node): ContentView.General = {
    View.fromDom[ContentView.General](a.childNodes(0).childNodes(0))
  }



  def insertExtraToContentView(a: HTMLElement, b: HTMLElement): Unit = {
    val box = a.childNodes(0)
    box.appendChild(b)
  }

  def extraViewInFrame(a: HTMLElement): HTMLElement = {
    a.childNodes(0).childNodes(1).asInstanceOf[HTMLElement]
  }

  def uuidOf(a: ContentView.General): UUID = {
    UUID.fromString(a.dom.asInstanceOf[js.Dynamic].ctUuid.asInstanceOf[String])
  }

  private val onClickListener: js.Function1[Event, _] = (ev: Event) => {
    val uuid = UUID.fromString(ev.currentTarget.asInstanceOf[js.Dynamic].ctUuid.asInstanceOf[String])
    onClick(uuid)
  }

  private val onDoubleClickListener: js.Function1[Event, _] = (ev: Event) => {
    val uuid = UUID.fromString(ev.currentTarget.asInstanceOf[js.Dynamic].ctUuid.asInstanceOf[String])
    onDoubleClick(uuid)
  }

  private def create(a: model.data.Node): General = {
    val cv = contentViewCreate(a.content, a.contentType)
    if (docFramerExtraClass != "") {
      cv.dom.classList.add(docFramerExtraClass)
    }
    if (onClick != null || onDoubleClick != null) {
      cv.dom.asInstanceOf[js.Dynamic].ctUuid = a.uuid.toString
    }
    if (onClick != null) {
      cv.dom.addEventListener("click", onClickListener)
    }
    if (onDoubleClick != null) {
      cv.dom.addEventListener("dblclick", onDoubleClickListener)
    }
    cv
  }

  def updateContentViewInsideFrame(a: HTMLElement, data: model.data.Node): Unit = {
    val oldView = contentViewFromWithHold(a)
    if (contentViewMatches(data.content, data.contentType, oldView)) {
      oldView.updateContent(data.content)
      updateContentViewAndHoldAttribute(a, data)
    } else {
      oldView.destroy()
      val box = a.childNodes(0)
      create(data).attachToNode(box, box.childNodes(0))
    }
  }

  def contentViewAndHold(node: model.data.Node): HTMLElement = {
    div(
      cls := "ct-d-folded",
      display := "flex",
      flexDirection := "row",
      div(
        cls := classesFromNodeAttribute(node),
        create(node)
      ),
      tag("span")(
        cls := (if (useFoldedIcon) "ct-d-hold ct-d-hold-folded " else "ct-d-hold "),
        if (docFramerIsSmall >= 2) {
          marginLeft := "-8px"
          marginTop := "6px"
        } else border := "none"
      )
    ).render
  }

  def updateContentViewAndHoldAttribute(a: HTMLElement, node: model.data.Node): Unit = {
    a.childNodes(0).asInstanceOf[HTMLElement].className = classesFromNodeAttribute(node)
  }

  def classesFromNodeAttribute(node: model.data.Node): String = {
    "ct-d-box " + (node.contentType.map {
      case model.data.Node.ContentType.Cite => "ct-d-cite"
      case model.data.Node.ContentType.Hr => "ct-d-hr"
      case model.data.Node.ContentType.Heading(j) =>
        val headingStr = if (parentHeadingLevel == -1 || j == parentHeadingLevel + 1) {
          "ct-d-heading"
        } else {
          "ct-d-heading-error"
        }
        if (docFramerIsSmall == 0) {
          s"$headingStr ct-d-h$j"
        } else if (docFramerIsSmall == 1) {
          s"$headingStr ct-d-hs${if (j >= 4) "s" else j.toString}"
        } else {
          headingStr
        }
      case _ => ""
    }.getOrElse("") + (if (node.isFolder) " ct-d-article " else " " )
        + (if (node.childIsLists) node.attribute(model.data.Node.ListType).getOrElse(model.data.Node.ListType.UnorderedList) match {
      case model.data.Node.ListType.OrderedList => "ct-d-ol"
      case model.data.Node.ListType.DashList => "ct-d-dl"
      case _ => "ct-d-ul"
    } else "ct-d-ps")   + " " + (node.priority.getOrElse(0) match {
      case a if a <= -3 => "ct-p-n3plus"
      case -2 => "ct-p-n2"
      case -1 => "ct-p-n1"
      case 0 => ""
      case 1 => "ct-p-1"
      case 2 => "ct-p-2"
      case 3 => "ct-p-3"
      case 4 => "ct-p-4"
      case _ => "ct-p-5plus"
    }))
  }
}
