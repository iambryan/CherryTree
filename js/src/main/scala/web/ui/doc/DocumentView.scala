package web.ui.doc

import java.util.UUID

import api.PermissionLevel
import doc.{DocInterface, DocState}
import model.data.Content
import model.data.Node.ContentType
import model.mode.Node
import model.operation
import model.range.IntRange
import org.scalajs.dom.html.Div
import org.scalajs.dom.{CompositionEvent, DragEvent, Event, FocusEvent, MouseEvent, document, raw, window}
import org.scalajs.dom.raw.HTMLElement
import util.Rect
import scalatags.JsDom.all.{s, _}
import search.Search
import settings.Settings
import web.{Implicits, ui}
import web.ui.content.{ContentView, ContentViewEditor, RichView}
import web.view._
import web.ui.dialog._

import scala.scalajs.js
import scala.util.Success

object DocumentView {
  val DisableReasonMouse = "mouse"
  val DisableReasonComposition = "composition"
}

/**
  * NOTE: this class only use the doc framer for class attributes, nothing more!
  */
abstract class DocumentView extends View with EditorView with Implicits with DocFramer
{
  import DocumentView._

  override val contentViewCreatedInDocument: Boolean = true

  def settings: Settings

  def clearNodeVisual(): Unit
  def updateNodeVisual(v: Node.Visual, fromUser: Boolean): Unit

  // this can temp be null during state update
  protected var currentDoc: DocState = null
  protected def currentZoom: model.cursor.Node = currentDoc.zoom
  protected def currentZoomId: UUID = currentDoc.zoomId

  private val noEditable = div(
    cls := "unselectable",
    position := "absolute",
    top := "-30px",
    width := "1000px",
    ui.EvilChar,
    height := "0px").render



  val fakeSelections: Div = div(
    position := "absolute",
    width := "0px",
    height := "0px"
  ).render

  val motionXView = div(
    position := "absolute",
    background := "#000000",
    width := "1px",
    height := "100%"
  ).render

  dom = div(
    position := "relative",
    cls := "ct-scroll ct-document-view-root",
    flex := "1 1 auto",
    paddingLeft := "36px",
    paddingRight := "36px",
    contenteditable := "true",
    overflowY := "scroll",
    fakeSelections,
    noEditable,
  ).render

  if (model.debug_selection) {
    dom.appendChild(motionXView)
  }

  /**
    * TODO no protected var
    */
  private var duringStateUpdate: Boolean = false
  private var allowCompositionInput = false


  protected val client: DocInterface

  protected def cursorOf[T <: model.data.Content, O <: model.operation.Content](a: ContentView[T, O]): model.cursor.Node
  protected def contentAt(a: model.cursor.Node): ContentView.General
  protected def contentOfHold(a: raw.Node): ContentView.General

  private val nonEditableSelection = document.createRange()

  private def cancelNoEditableInput(): Any = {
    noEditable.textContent = ui.EvilChar
    val sel = window.getSelection()
    sel.removeAllRanges()
    sel.addRange(nonEditableSelection)
  }

  private var duringValidComposition = false

  def canEditActiveEditor() = activeContentEditor != null && client.canEdit(currentDoc.node(cursorOf(activeContent)).uuid)

  event("compositionstart", (a: CompositionEvent) => {
    flushBeforeKeyDown()
    if (a.target == noEditable) {
    } else {
      if (allowCompositionInput) {
        if (canEditActiveEditor()) {
          editor.onDeleteCurrentSelectionAndStartInsert()
          editor.disableRemoteStateUpdate(true, DisableReasonComposition)
          duringValidComposition = true
        }
      } else {
        a.preventDefault()
        duringValidComposition = false
      }
    }
  })

  event("compositionupdate", (a: CompositionEvent) => {
    flushBeforeKeyDown()
    if (duringValidComposition) editor.disableRemoteStateUpdate(true, DisableReasonComposition)
  })

  event("compositionend", (a: CompositionEvent) => {
    flushBeforeKeyDown()
    if (duringValidComposition) editor.disableRemoteStateUpdate(false, DisableReasonComposition)
    duringValidComposition = false
    if (activeContent != null) {
      activeContentEditor.compositionEndEvent()
    }
  })

  event("beforeinput", (a: Event) => {
    flushBeforeKeyDown()
    if (a.target == noEditable) {
    } else {
      //window.console.log("before input ", a)
      if (activeContent != null) {
        activeContentEditor.beforeInputEvent(a)
      }
    }
  })

  event("input", (a: Event) => {
    flushBeforeKeyDown()
    if (a.target == noEditable) {
      cancelNoEditableInput()
    } else {
      //window.console.log("input ", a)
      if (activeContent != null) {
        activeContentEditor.inputEvent(a)
      }
    }
  })

  /**
    *
    *
    *
    * state
    *
    *
    */
  private var focusedOut_ : Boolean = true
  private def focusedOut_=(b: Boolean) : Unit = {
    focusedOut_ = b
  }
  private def focusedOut: Boolean = focusedOut_

  private var currentSelection: raw.Range = nonEditableSelection




  event("blur", (a: FocusEvent) => {
    focusedOut = true
    dom.classList.add("ct-window-inactive")
  })


  override def focus(): Unit = {
    if (focusedOut) {
      focusedOut = false
      dom.classList.remove("ct-window-inactive")
      if (model.debug_scroll) {
        println("focusing on document")
      }
      // setting the selection is enough to get focus
      flushSelection(true)
    }
  }


  event("focus", (a: FocusEvent) => {
    if (!duringStateUpdate && focusedOut) {
      focusedOut = false
      dom.classList.remove("ct-window-inactive")
      flushSelection()
    }
  })


  private var duringVisualUpDown = false
  private var visualMotionX = -1
  private val tempSelection: raw.Range = document.createRange()

  private var forceFlushSelection = false

  protected def flushSelection(force0: Boolean = false, userModeUpdate: Boolean = false): Unit = {
    val force = if (forceFlushSelection) {
      forceFlushSelection = false
      true
    } else {
      force0
    }
    if (!duringVisualUpDown && userModeUpdate && currentSelection != nonEditableSelection) {
      val sel = currentDoc.mode match {
        case Some(model.mode.Node.Content(_, v: model.mode.Content.RichRange)) =>
          val r = tempSelection
          r.setStart(currentSelection.startContainer, currentSelection.startOffset)
          r.setEnd(currentSelection.endContainer, currentSelection.endOffset)
          r.collapse(!v.leftIsAnchor)
          r
        case _ => currentSelection
      }
      visualMotionX = (toRect(sel.getBoundingClientRect()).middleX - dom.offsetLeft).toInt
      if (model.debug_selection) {
        motionXView.style.left = s"${visualMotionX}px"
      }
    }
    if (!focusedOut) {
      val sel = window.getSelection()
      if (!force) {
        if (sel.rangeCount == 1) {
          val ran = sel.getRangeAt(0)
          if (ran == currentSelection) {
            return
          } else if (ran.startContainer == currentSelection.startContainer &&
            ran.startOffset == currentSelection.startOffset &&
            ran.endContainer == currentSelection.endContainer &&
            ran.endOffset == currentSelection.endOffset) {
            return
          }
        }
      }
      if (model.debug_selection) {
        window.console.log("flushing selection", sel)
      }
      val oldTop = dom.scrollTop
      sel.removeAllRanges()
      sel.addRange(currentSelection)
      if (dom.scrollTop != oldTop) {
        dom.scrollTop = oldTop
        if (model.debug_scroll) {
          window.console.log("resetting scroll by selection change", dom.scrollTop, oldTop)
        }
      }
    }
  }


  def canEditNode(root: model.cursor.Node): Boolean = canEditNode(currentDoc.node(root))
  def canEditNode(root: model.data.Node): Boolean = client.canEdit(root.uuid)

  private def updateMode(m: Option[model.mode.Node], viewUpdated: Boolean = false, editorUpdated: Boolean = false, fromUser: Boolean = false): Unit = {
    duringStateUpdate = true
    m match {
      case None =>
        allowCompositionInput = false
        removeActiveContentEditor()
        endSelection()
        clearNodeVisual()
      case Some(mk) => mk match {
        case model.mode.Node.Content(at, aa) =>
          allowCompositionInput = aa match {
            case _: model.mode.Content.RichInsert => true
            case _: model.mode.Content.RichRange => true
            case _ => false
          }
          clearNodeVisual()
          val current = contentAt(at)
          if (current != activeContent) {
            removeActiveContentEditor()
            activeContentEditor = current.createEditor(this, editor)
          }
          activeContentEditor.updateMode(aa, viewUpdated, editorUpdated, fromUser)
        case v@model.mode.Node.Visual(_, _) =>
          allowCompositionInput = false
          removeActiveContentEditor()
          endSelection()
          updateNodeVisual(v, fromUser)
      }
    }
    duringStateUpdate = false
    flushSelection(userModeUpdate = fromUser)
    updateSearchingHighlight()
  }


  def removeAllNodes(): Unit

  def renderAll(): Unit

  def toggleHold(a: model.cursor.Node, visible: Boolean)


  def renderTransaction(s: DocState, t: operation.Node, to: DocState, viewUpdated: Boolean, editorUpdated: Boolean): Unit

  def refreshAllLaTeX(): Unit

  // we use onAttach because we access window.setSelection
  override def onAttach(): Unit = {
    super.onAttach()

    nonEditableSelection.setStart(noEditable.childNodes(0), 0)
    nonEditableSelection.setEnd(noEditable.childNodes(0), 0)

    currentDoc = client.state
    renderAll()
    updateMode(client.state.mode)

    observe(client.stateUpdates.doOnNext(update => {
      update.foldsBefore.foreach(f => {
        if (currentDoc.visible(f._1)) {
          if (model.debug_view) {
            println(s"unfolding ${f._1} ${f._2}")
          }
          toggleHold(f._1, f._2)
        }
      })
      duringStateUpdate = true
      if (update.to.zoomId != currentZoomId) {
        //          if (cursor.Node.contains(currentZoom, a)) {
        //          } else {
        //            cleanFrame(rootFrame)
        //            insertNodeRec(update.root(a), rootFrame)
        //          }
        updateMode(None)
        removeAllNodes()
        currentDoc = update.to
        renderAll()
        scrollToTop()
      } else {
        for ((s, t, to) <- update.from) {
          currentDoc = s
          //              if (model.debug_view) {
          //                println(s"current zoom is $currentZoom")
          //                println(s"current trans is ${t._1}")
          //              }
          renderTransaction(s, t, to, update.viewUpdated, update.editorUpdated)
        }
      }
      if (_postRefreshAllLaTeX) {
        _postRefreshAllLaTeX = false
        refreshAllLaTeX()
      }
      currentDoc = update.to
      duringStateUpdate = false
      updateMode(update.to.mode, update.viewUpdated, update.editorUpdated, update.fromUser)
      refreshMounted()
    }))

    observe(client.searchState.doOnNext(search => {
      val haveBefore = searching != null
      searching = search.searching.orNull
      updateSearchingHighlight()
      if (haveBefore && searching == null && activeContent != null) {
        scrollInToViewIfNotVisible(activeContent.dom, dom, 5, 5, 30, 30)
      }
    }))

    event(window, "resize", (a: MouseEvent) => {
      refreshMounted()
      if (activeContent != null) activeContentEditor.refreshRangeSelection()
      updateSearchingHighlight()
    })


    observe(editor.flushes.doOnNext(_ => if (activeContent != null) activeContentEditor.flush()))
  }

  private var searching: Search = null

  private def clearSearchHighlight(): Unit = {
    fakeSelections.innerHTML = ""
  }

  private def updateSearchingHighlight(): Unit = {
    clearSearchHighlight()
    if (searching == null) {
    } else {
      currentDoc.searchInShown(searching, settings.enableModal).headOption match {
        case Some(a) =>
          contentAt(a.node).asInstanceOf[Any] match {
            case richView: RichView =>
              scrollInToViewIfNotVisible(richView.dom, dom, 5, 5, 30, 30)
              RichView.renderRangeInto(richView.nonEmptySelectionToDomRange(a.range)._1, fakeSelections, "ct-search-highlight")
            case _ =>
          }
        case None =>
      }
      // render search ocs
    }
  }




  def startSelection(range: raw.Range): Unit = {
    currentSelection = range
  }

  def endSelection(): Unit = {
    currentSelection = nonEditableSelection
  }

  def hasSelection: Boolean = {
    nonEditableSelection != currentSelection
  }

  def selection: raw.Range = currentSelection

  protected var activeContentEditor: ContentViewEditor.General = null

  protected def activeContent =
    if (activeContentEditor == null || activeContentEditor.contentView.destroyed) null else activeContentEditor.contentView

  protected def removeActiveContentEditor(): Unit = {
    if (activeContentEditor != null && !activeContentEditor.contentView.destroyed) {
      activeContentEditor.clearMode()
    }
    activeContentEditor = null
  }


  override def destroy(): Unit = {
    if (activeContent != null) activeContent.destroy()
    super.destroy()
  }

  protected val handleHoverEvent: js.Function1[MouseEvent, Unit] = (e: MouseEvent) => {
    val hold = e.target.asInstanceOf[HTMLElement]
    val ee = contentOfHold(hold)
    val cur = cursorOf(ee)
    val node = client.state.node(cur)
    client.getNodeInfo(node.uuid).onComplete(res => {
      val info = res match {
        case Success(Some(i)) =>
          Seq(s"created time: ${platform.formatDate(i.createdTime)}",
            s"updated time: ${platform.formatDate(i.updatedTime)}",
            s"created by: ${i.createdBy.name} (${i.createdBy.email})")
        case _ =>
          Seq.empty
      }

      hold.title = (info ++ Seq(
          node.contentType.map("content type: " + _.toString).getOrElse(""),
          node.attribute(model.data.Node.ListType).map("list type: " + _.toString).getOrElse(""),
          s"items: ${node.count}",
          s"text size: ${node.content.size}",
          s"total text size: ${node.size}"
       ) ++
        (if (node.ignoreInSearch) Seq("ignored in search") else Seq.empty)
      ).filter(_.nonEmpty).mkString("\n")
    })
  }

  var _postRefreshAllLaTeX = false
  def postRefreshAllLaTeX(): Unit = {
    _postRefreshAllLaTeX = true
  }


  var sourceEditor: CoveringSourceEditDialog = null
  var commandMenu: CommandMenuDialog = null
  var registersDialog: RegistersDialog = null
  var attributeEditor: AttributeEditDialog = null
  var inlineEditor : InlineCodeDialog = null

  def selectionRect: Rect

  protected def refreshMounted(): Unit = {
    attributeEditor.refresh()
    inlineEditor.refresh()
    commandMenu.refresh()
    registersDialog.refresh()
  }



  private val commandMenuAnchor = new OverlayAnchor {
    override def rect: Rect = selectionRect
  }

  def showCommandMenu(): Unit = {
    commandMenu.show(commandMenuAnchor)
  }

  def showRegisters(): Unit = {
    registersDialog.show(commandMenuAnchor)
  }


  /**
    *
    *
    *
    *
    *
    *
    *
    *
    *
    * gaint mess of mouse, keyboard arrow handling
    *
    *
    *
    *
    *
    *
    *
    */


  private var isRightMouseButton: Boolean = false
  private var mouseDown = false
  protected def duringMouseMovement: Boolean = mouseDown
  private case class Click(t: Long, x: Double, y: Double) {
    def near(ano: Click, limit: Long): Boolean = {
      val dx = x - ano.x
      val dy = y - ano.y
      dx * dx + dy * dy < 100 && ano.t - t < limit
    }
  }
  private val noMouseDown = Click(-1L, 0, 0)
  private var lastMouseDown = noMouseDown
  private var oneButLastMouseDown = noMouseDown
  private var mouseFirstContent: model.cursor.Node = null
  private var mouseFirstContentRich: RichView = null
  private var mouseSecondContent: model.cursor.Node = null
  private var clickCount = 0

  private var mouseDisableWrongSelectionHandler = -1

  private def setFirstContent(other: model.cursor.Node) = {
    mouseFirstContent = other
    contentAt(mouseFirstContent).asInstanceOf[Any] match {
      case r: RichView =>
        mouseFirstContentRich = r
        r.tempEditableTempDuringSelectionChange(true)
      case _ =>
        editor.onMouseFocusOn(other, None, true, false)
    }
  }

  protected override def flushBeforeKeyDown(): Unit = {
    if (focusFinder != null) {
      readSelectionAfterMouseUpWithDelay(0, focusFinder._2, focusFinder._3, focusFinder._4)
    }
  }

  protected override def postFlushSelectionOnSpellCheckerKey(): Unit = {
    forceFlushSelection = true
    editor.disableRemoteStateUpdate(true, DisableReasonMouse)
    clearAllPreviousReading()
    readSelectionAfterMouseUpWithDelay(10, null, null, true)
  }

  event("mousedown", (a: MouseEvent) => {
    editor.flushBeforeMouseDown()
    clearAllPreviousReading()
    val now = System.currentTimeMillis()
    if (!hasShift && ((a.metaKey && platform.isMac) || (a.ctrlKey && !platform.isMac))) {
      lastMouseDown = noMouseDown
      oneButLastMouseDown = noMouseDown
      val pc = findParentContent(a.target.asInstanceOf[raw.Node])
      if (pc != null) {
        editor.onVisualMode(pc, pc)
      }
      a.preventDefault()
    } else {
      var down = Click(now, a.clientX, a.clientY)
      if (!lastMouseDown.near(down, 500)) { // single click
        clickCount = 1
      } else if (!oneButLastMouseDown.near(down, 600)) { // double click
        clickCount = 2
      } else { // triple click
        val pc = findParentContent(a.target.asInstanceOf[raw.Node])
        if (pc != null) {
          val cot = client.state.node(pc).content
          val ran = if (cot.isRich) {
            val size = cot.asInstanceOf[Content.Rich].size
            Some(IntRange(0, size))
          } else {
            None
          }
          editor.onMouseFocusOn(pc, ran, true, false)
          down = null
        }
        clickCount = 3
      }
      if (hasShift) {
        client.state.mode match {
          case None =>
            down = null
          case Some(m) =>
            setFirstContent(m.other)
        }
      }
      if (down != null) {
        clearAllPreviousReading()
        mouseDisableWrongSelectionHandler = window.setInterval(() => {
          if (mouseSecondContent != null || (mouseFirstContent != null && mouseFirstContentRich == null)) {
            val sel = window.getSelection()
            if (sel.rangeCount > 0) sel.removeAllRanges()
          }
        }, 8)
        editor.disableRemoteStateUpdate(true, DisableReasonMouse)
        mouseDown = true
        oneButLastMouseDown = lastMouseDown
        lastMouseDown = down
        isRightMouseButton = a.button != 0
        if (mouseFirstContent == null) {
          getFirstContentView(a)
        } else {
          handleMaybeSecondContentView(a)
        }
      } else {
        a.preventDefault()
      }
    }
  })


  private def endMouseDown(a: MouseEvent, isRight: Boolean, readSelection: Boolean): Unit = {
    if (mouseDown) {
      window.clearInterval(mouseDisableWrongSelectionHandler)
      if (!readSelection) {
        editor.disableRemoteStateUpdate(false, DisableReasonMouse)
      } else if (mouseSecondContent == null) {
        isRightMouseButton = isRight || a.button != 0
        var waitTime = 0
        if (clickCount == 1) {
          waitTime = (200 - (System.currentTimeMillis() - lastMouseDown.t)).toInt
          if (isRightMouseButton && waitTime < 100) waitTime = 100
          if (waitTime < 0) waitTime = 0
        }
        if (model.debug_selection) {
          println("read selection at " + waitTime)
        }
        val isDoubleClick = clickCount == 2 && !isRightMouseButton
        readSelectionAfterMouseUpWithDelay(waitTime, mouseFirstContentRich, a, isDoubleClick)
        if (isDoubleClick) {
          editor.onDoubleClick()
        }
      } else {
        editor.disableRemoteStateUpdate(false, DisableReasonMouse)
        flushSelection()
      }

      mouseDown = false
      mouseFirstContent = null
      if (mouseFirstContentRich != null) {
        mouseFirstContentRich = null
      }

      mouseSecondContent = null
    }
  }

  event(org.scalajs.dom.window, "mouseup", (a: MouseEvent) => {
    endMouseDown(a, false, true)
  })

  event("contextmenu", (a: MouseEvent) => {
    endMouseDown(a, true, true)
  })

  private def getFirstContentView(a: MouseEvent): Unit = {
    val pc = findParentContent(a.target.asInstanceOf[raw.Node])
    if (pc != null) {
      setFirstContent(pc)
    }
  }


  private def handleMaybeSecondContentView(a: MouseEvent) = {
    val node = a.target.asInstanceOf[raw.Node]
    val ctt = {
      val p = findParentContent(node)
      if (p != null) p else null
    }
    val secondContent =
      if (mouseSecondContent == null) {
        if (ctt != mouseFirstContent) ctt else null
      } else if (ctt != null) {
        ctt
      } else {
        mouseSecondContent
      }
    if (secondContent != null) {
      if (secondContent != mouseSecondContent) {
        mouseSecondContent = secondContent
        editor.onVisualMode(mouseFirstContent, mouseSecondContent)
        val sel = window.getSelection()
        if (sel.rangeCount > 0) sel.removeAllRanges()
      }
    } else if (mouseFirstContent != null && mouseFirstContentRich == null) {
      val sel = window.getSelection()
      if (sel.rangeCount > 0) sel.removeAllRanges()
    }
  }

  event("mouseover", (a: MouseEvent) => {
    if (mouseDown) {
      if (mouseFirstContent == null) {
        getFirstContentView(a)
      } else {
        handleMaybeSecondContentView(a)
      }
    }
  })



  private def findParentContent(t0: raw.Node): model.cursor.Node = {
    val ct = findParentContentView(t0, dom)
    if (ct != null) {
      cursorOf(ct)
    } else {
      null
    }
  }

  private var focusFinder: (Int, RichView, MouseEvent, Boolean) = null

  private def clearAllPreviousReading(): Unit = {
    if (focusFinder != null) {
      window.clearTimeout(focusFinder._1)
      focusFinder = null
    }
  }


  event("click", (a: MouseEvent) => {
    a.target match {
      case element: HTMLElement if element.className.contains("ct-d-hold") =>
        clearAllPreviousReading() // if mouseup is before us
      val ct = contentOfHold(element)
        endMouseDown(a, false, false)
        editor.onMouseFocusOn(cursorOf(ct), None, true, false)
        showCommandMenu()
      case _ =>
    }
  })


  def exitVisual(): Unit = {
    val cur = currentDoc.mode.get.focus
    val content = contentAt(cur)
    content.constructVisualLineBuff()
    val range = content.rangeAroundLine(0, (visualMotionX + dom.offsetLeft).toInt, !settings.enableModal)
    content.clearVisualLineBuff()
    editor.onMouseFocusOn(currentDoc.mode.get.focus, range, true, false, maybeNormal = true)
  }

  def visualUpDownMotion(isUp: Boolean, blockWiseCount: Int, enterVisual: Boolean): Unit = {
    val isBlockwise = blockWiseCount >= 0
    val count = 1 // we don't accept multiple lines now
    duringVisualUpDown = true
    currentDoc.mode match {
      case Some(model.mode.Node.Content(node, a)) =>
        var i = 0
        var cur = node
        var content = contentAt(cur)
        if (model.debug_view) {
          if (currentSelection != nonEditableSelection) {
            assert(findParentContent(currentSelection.startContainer) == cur)
            assert(activeContentEditor.contentView == content)
          }
        }
        content.constructVisualLineBuff()

        val sel = a match {
          case v: model.mode.Content.RichRange =>
            val r = tempSelection
            r.setStart(currentSelection.startContainer, currentSelection.startOffset)
            r.setEnd(currentSelection.endContainer, currentSelection.endOffset)
            r.collapse(!v.leftIsAnchor)
            r
          case _ => currentSelection
        }
        var line = content.readVisualSelectionLine(sel, isUp)
        var lineCount = content.visualLineCount()
        var goToExteme = false
        val mover = currentDoc.mover()
        if (model.debug_selection) {
          window.console.log("line count", lineCount, line)
        }
        def resetContentAt(c: model.cursor.Node): Unit = {
          assert(cur != c)
          content.clearVisualLineBuff()
          cur = c
          content = contentAt(c)
          content.constructVisualLineBuff()
          lineCount = content.visualLineCount()
          if (isUp) {
            line = lineCount - 1
          } else {
            line = 0
          }
        }

        while (i < count) {
          if (isUp) {
            if (!isBlockwise && line > 0) {
              line -= 1
            } else {
              mover.visualUp(cur) match {
                case Some(j) =>
                  resetContentAt(j)
                case None =>
                  goToExteme = true
              }
            }
          } else {
            if (!isBlockwise && line < lineCount - 1) {
              line += 1
            } else {
              mover.visualDown(cur) match {
                case Some(j) =>
                  resetContentAt(j)
                case None =>
                  goToExteme = true
              }
            }
          }
          i += 1
        }
        val range = if (goToExteme) {
          content.clearVisualLineBuff()
          if (isUp) {
            Some(IntRange(0, 0))
          } else {
            val size = currentDoc.node(cur).content.size
            Some(IntRange(size, size))
          }
        } else {
          val insert = !settings.enableModal || currentDoc.isInsertal
          val ret = content.rangeAroundLine(line, (visualMotionX + dom.offsetLeft).toInt, insert)
          content.clearVisualLineBuff()
          ret
        }
        if (enterVisual || (settings.enableModal && a.isInstanceOf[model.mode.Content.RichVisual])) {
          if (node != cur || !a.isInstanceOf[model.mode.Content.Rich]) {
            editor.onVisualMode(node, cur)
          } else {
            val rm = a.asInstanceOf[model.mode.Content.Rich]
            val rang = range.getOrElse(if (isUp) currentDoc.node(cur).rich.rangeBeginning else currentDoc.node(cur).rich.rangeEnd)
            val leftIsAnchor = rm.fixed.start < rang.start
            editor.onMouseFocusOn(cur, Some(rm.fixed.merge(rang)), leftIsAnchor, false)
          }
        } else {
          editor.onMouseFocusOn(cur, range, true, false, maybeNormal = true)
        }
      case _ => throw new IllegalStateException("Not possible")
    }
    duringVisualUpDown = false
  }

  private def readSelectionAfterMouseUpWithDelay(delay: Int, richView: RichView, mouseEvent: MouseEvent, isDouble: Boolean): Unit = {
    def work() = {
      editor.disableRemoteStateUpdate(false, DisableReasonMouse)
      focus()
      val sel = window.getSelection()
      if (sel != null && sel.rangeCount > 0) {

        val mt = if (mouseEvent != null) findParentContentView(mouseEvent.target.asInstanceOf[raw.Node], dom) else null
        var ct = findParentContentView(sel.focusNode, dom)
        var noUseSelection = false
        if (mt != null && mt != ct) {
          noUseSelection = true
          ct = mt
        }
        if (ct != null) {
          val pc = cursorOf(ct)
          val cc = findParentContent(sel.anchorNode)
          if (cc == null || cc == pc || noUseSelection) {
            val cur = pc
            ct.asInstanceOf[Any] match {
              case r: RichView =>
                val atomic = if ((noUseSelection || sel.isCollapsed) && mouseEvent != null) {
                  r.atomicParentOf(mouseEvent.target.asInstanceOf[raw.Node])
                } else {
                  null
                }
                if (atomic != null) {
                  if (isDouble) {
                    editor.onMouseFocusOn(cur, Some(IntRange(atomic._2, atomic._3)), true, false)
                  } else {
                    val rect = atomic._1.getBoundingClientRect()
                    if (Math.abs(mouseEvent.clientX - rect.left) < Math.abs(mouseEvent.clientX - rect.right)) {
                      editor.onMouseFocusOn(cur, Some(IntRange(atomic._2, atomic._2)), true, false)
                    } else {
                      editor.onMouseFocusOn(cur, Some(IntRange(atomic._3, atomic._3)), true, false)
                    }
                  }
                } else {
                  r.readSelectionFromDom() match {
                    case Some(res) =>
                      editor.onMouseFocusOn(cur, Some(res._1), res._2, false)
                    case _ => editor.onMouseFocusOn(cur, None, true, false)
                  }
                }
              case w =>
                editor.onMouseFocusOn(cur, None, true, false)
            }
          } else {
            editor.onVisualMode(cc, pc)
          }
        } else {
          editor.onRefreshMode()
        }
      } else {
        editor.onRefreshMode()
      }
      if (richView != null) richView.tempEditableTempDuringSelectionChange(false)
      focusFinder = null
    }
    if (delay == 0) {
      work()
    } else {
      focusFinder = (window.setTimeout(() => {
        work()
      }, delay), richView, mouseEvent, isDouble)
    }
  }




  /**
    *
    *
    * drag drop currently disabled for entire document
    *
    *
    */

  event("dragstart", (a: DragEvent) => {
    preventDefault(a)
  })

  event("dragend", (a: DragEvent) => {
    preventDefault(a)
  })

  event("dragover", (a: DragEvent) => {
    preventDefault(a)
  })

  event("dragenter", (a: DragEvent) => {
    preventDefault(a)
  })

  event("drop", (a: DragEvent) => {
    preventDefault(a)
  })

}
