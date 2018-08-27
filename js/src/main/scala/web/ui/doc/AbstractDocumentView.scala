package web.ui.doc

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import web.view._
import web.ui.dialog.{CommandMenuDialog, CoveringSourceEditDialog, InlineCodeDialog, UrlAttributeEditDialog}

abstract class AbstractDocumentView extends View {

  def hasSelection: Boolean

  def selection: org.scalajs.dom.Range


  def startSelection(range: org.scalajs.dom.Range): Unit

  def endSelection(): Unit

  val fakeSelections: HTMLElement


  var sourceEditor: CoveringSourceEditDialog = null
  var commandMenu: CommandMenuDialog = null
  var attributeEditor: UrlAttributeEditDialog = null
  var inlineEditor : InlineCodeDialog = null
}