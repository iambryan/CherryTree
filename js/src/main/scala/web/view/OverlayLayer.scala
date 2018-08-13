package web.view

import client.Client
import command.Key
import org.scalajs.dom.raw._
import org.scalajs.dom.window
import scalatags.JsDom.all._
import util.Rect

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js


trait CoveringOverlay extends Overlay {

  protected val covering: () => HTMLElement

  override def show(): Unit = {
    val f = covering().getBoundingClientRect()
    val g = layer.parent.getBoundingClientRect()
    dom.style.position = "absolute"
    val paddingLeft = (((f.left - g.left) max 0.0) + 48) + "px"
    val paddingBottom = (((g.bottom - f.bottom) max 0.0) + 48) + "px"
    val paddingTop = (((f.top - g.top) max 0.0) + 48) + "px"
    val paddingRight = (((g.right - f.right) max 0.0) + 48) + "px"
    dom.style.width = s"calc(100% - $paddingLeft - $paddingRight)"
    dom.style.height = s"calc(100% - $paddingTop - $paddingBottom)"
    dom.style.top = paddingTop
    dom.style.left = paddingLeft
    super.show()
  }

  override protected def onDismiss(): Unit = {
    super.onDismiss()
  }
}

trait OverlayAnchor {
  def rect: Rect
  def onDismiss()
}

trait MountedOverlay[ANCHOR <: OverlayAnchor] extends Overlay {


  protected var anchor: ANCHOR = null.asInstanceOf[ANCHOR]

  def show(anchor: ANCHOR): Unit = {
    this.anchor = anchor
    setDomAttributeBy(anchor.rect)
    show()
  }

  override protected def onDismiss(): Unit = {
    anchor.onDismiss()
    anchor = null.asInstanceOf[ANCHOR]
    super.onDismiss()
  }

  def refresh(): Unit = {
    if (anchor != null) setDomAttributeBy(anchor.rect)
  }
}
trait Overlay extends View {

  protected def layer: OverlayLayer

  private var attached = false

  def show(): Unit = {
    if (!attached) {
      attached = true
      attachTo(layer)
    }
    if (dismissed) {
      dismissed = false
      dom.style.display = "block"
      layer.onShowOverlay(this)
      focus()
      window.setTimeout(() => {
        if (!dismissed) focus()
      }, 0)
    }
  }

  def dismiss(): Unit = {
    if (!dismissed) {
      dismissed = true
      onDismiss()
    }
  }

  protected def onDismiss(): Unit = {
    dom.style.display = "none"
    layer.onDismissOverlay(this)
  }

  protected var dismissed = true


  /**
    * will also remove from parent
    * ALSO make sure you destroy child dom attachments!!!
    */
  override def destroy(): Unit = {
    dismiss()
    super.destroy()
  }

  def setDomAttributeBy(rect: Rect): Unit = {
    val bounding = toRect(layer.dom.getBoundingClientRect())
    val rec = rect.moveBy(-bounding.left, -bounding.top)
    //whereToShow(bounding, rec)
    dom.style.left = rec.left.toString + "px"
    dom.style.top = rec.bottom.toString + "px"
  }
}
class OverlayLayer(val parent: HTMLElement, base: View) extends View {

  private val showingOverlay = ArrayBuffer[Overlay]()

  def onShowOverlay(overlay: Overlay): Unit = {
    if (!showingOverlay.contains(overlay)) {
      if (showingOverlay.isEmpty) show()
      showingOverlay.append(overlay)
    }
  }

  def onDismissOverlay(overlay: Overlay): Unit = {
    if (showingOverlay.contains(overlay)) {
      val index = showingOverlay.indexOf(overlay)
      showingOverlay.remove(index)
      if (showingOverlay.isEmpty) {
        dismiss()
      } else if (index == showingOverlay.size) {
        showingOverlay.last.focus()
      }
    }
  }


  private val clicker = div(
    position := "absolute",
    width := "100%",
    height := "100%"
  ).render

  dom = div(
    position := "absolute",
    left := "0px",
    top := "0px",
    width := "100%",
    height := "100%",
    zIndex := "100",
    display := "none",
    clicker
  ).render

  private def show(): Unit = {
    dom.style.display = "block"
    clicker.addEventListener("click", clickEvent)
    clicker.addEventListener("dbclick", clickEvent)
  }

  private val clickEvent: js.Function1[MouseEvent, Unit] = (e: MouseEvent) => {
    dismissAllOverlay()
  }


  def dismissAllOverlay(): Unit = {
    while (showingOverlay.nonEmpty) {
      showingOverlay.head.dismiss()
    }
  }



  private def dismiss(): Unit = {
    clicker.removeEventListener("click", clickEvent)
    clicker.removeEventListener("dbclick", clickEvent)
    dom.style.display = "none"
    base.focus()
  }

}
