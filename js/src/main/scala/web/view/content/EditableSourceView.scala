package web.view.content

import model._
import model.data._
import model.operation.Content
import model.range.IntRange
import monix.execution.Cancelable
import org.scalajs.dom.raw.{CompositionEvent, Element, Event, HTMLElement, HTMLSpanElement, Node, Range}
import org.scalajs.dom.{document, raw, window}
import scalatags.JsDom.all._
import util.Rect
import view.EditorInterface
import web.view.doc.DocumentView
import web.view.{EmptyStr, removeAllChild, theme}
import web.view._

import scala.scalajs.js

class EditableSourceView(
  documentView: DocumentView,
  controller: EditorInterface,
  c0: model.data.Content.Code
) extends SourceView(c0)
  with EditableContentView[model.data.Content.Code, model.operation.Content.Code, model.mode.Content.Code] {

  private var editing: CoveringSourceEditDialog = null

  def removeEditor(): Unit = {
    if (editing != null) {
      editing.dismiss()
      editing = null
    }
  }


  override def updateContent(c: model.data.Content.Code, trans: model.operation.Content.Code, viewUpdated: Boolean): Unit = {
    this.c = c
    if (editing == null) {
      super.updateContent(c, trans, viewUpdated)
    } else {
      if (!viewUpdated) {
        trans match {
          case model.operation.Content.CodeLang(lang) =>
          case model.operation.Content.CodeContent(c) =>
            editing.sync(c)
        }
      }
    }
  }

  override def updateMode(aa: model.mode.Content.Code, viewUpdated: Boolean, fromUser: Boolean): Unit = {
    if (fromUser) {
      web.view.scrollInToViewIfNotVisible(dom, documentView.dom)
    }
    if (aa == model.mode.Content.CodeNormal) {
      removeEditor()
    } else {
      if (editing == null) {
        editing = documentView.sourceEditor
        editing.show(new SourceEditOption(c.unicode, false, c0.asSourceMime) {

          override def onTransaction(unicode: Seq[operation.Unicode]): Unit = {
            controller.codeEdit(unicode)
          }

          override def onDismiss(): Unit = {
            editing = null
            controller.exitCodeEdit()
            updateContent()
          }
        })
      }
    }
  }

  override def clearMode(): Unit = {
    removeEditor()
    dom.classList.remove("ct-selection")
  }


  /**
    * will also remove from parent
    * ALSO make sure you destroy child dom attachments!!!
    */
  override def destroy(): Unit = {
    clearMode()
    super.destroy()
  }

  override def initMode(): Unit = {
    dom.classList.add("ct-selection")
  }

  override def selectionRect: Rect = {
    toRect(dom.getBoundingClientRect())
  }
}
