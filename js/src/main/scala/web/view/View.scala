package web.view


import monix.execution.Cancelable
import monix.execution.rstreams.Subscription
import monix.reactive.Observable

import scala.collection.mutable.ArrayBuffer
import org.scalajs.dom._
import org.scalajs.dom.raw.{EventTarget, HTMLElement}

import scala.scalajs.js

object View {

  def fromDom[T <: View](a: Node): T = a.asInstanceOf[js.Dynamic].ctview.asInstanceOf[T]

  def maybeDom[View](a: Node): Option[View] = {
    val tt = a.asInstanceOf[js.Dynamic].ctview
    //noinspection ComparingUnrelatedTypes
    if (tt != js.undefined && tt != null) {
      Some(tt.asInstanceOf[View])
    } else {
      None
    }
  }

  private val views = new ArrayBuffer[View]()

  if (model.debug_view) {
    window.setInterval(() => {
      var i = 0
      while (i < views.size) {
        val v = views(i)
        if (v.dom_ != null && v.attached && !document.body.contains(v.dom_) && !v.destroyed) {
          if (v.needsDestroy) {
            window.console.log(v.dom)
            throw new IllegalStateException("View detached but not destroyed")
          }
          views.remove(i)
        } else {
          i += 1
        }
      }
    }, 3000)
  }
}

abstract class View {

  def fpsStart(): Unit = _root_.util.fpsStart()
  def fpsEnd(): Unit = _root_.util.fpsEnd()

  private var dom_ : HTMLElement = null
  private var attached = false
  private var des = new ArrayBuffer[Unit => Unit]()
  private val views = ArrayBuffer[View]()

  def removeDefer(a: Unit => Unit) = {
    val index = des.indexOf(a)
    if (index >= 0) des.remove(index)
  }

  if (model.debug_view) {
    View.views.append(this)
  }

  def dom: HTMLElement = {
    if (destroyed) throw new IllegalArgumentException("Already destroyed")
    dom_
  }

  def dom_=(a: HTMLElement): Unit = {
    if (dom_ == null) {
      dom_ = a
      dom_.asInstanceOf[js.Dynamic].ctview = this.asInstanceOf[scala.scalajs.js.Any]
    } else {
      throw new IllegalArgumentException("Only set once!!!")
    }
  }


  def attachToNode(a: Node, before: Node = null) : View = {
    if (destroyed) throw new IllegalStateException("Not supported")
    attached = true
    a.insertBefore(dom, before)
    onAttach()
    this
  }
  def attachTo(a: View, before: Node = null): View = {
    if (destroyed) throw new IllegalStateException("Not supported")
    attached = true
    a.dom.insertBefore(dom, before)
    onAttach()
    this
  }

  def onAttach(): Unit = {

  }


  def destroyed: Boolean = des == null

  def needsDestroy: Boolean = (des != null && des.nonEmpty) || views.nonEmpty

  /**
    * will also remove from parent
    * ALSO make sure you destroy child dom attachments!!!
    */
  def destroy(): Unit = {
    if (!attached) {
      window.console.log(dom_)
      throw new IllegalStateException("destroying not attached node!")
    }
    des.reverse.foreach(_.apply())
    views.reverse.foreach(a => if (a.attached) a.destroy())
    dom_.parentNode.removeChild(dom_)
    des = null
  }

  def defer(a: Unit => Unit): Unit = {
    des.append(a)
  }

  def defer[T <: View](a: T):  T = {
    views.append(a)
    a
  }

  /**
    *
    * the cancelable returned is THE SAME as the argument, and you are not being able to cancel a defer!!!
    * // LATER cancel this
    */
  def observe[T](a: Observable[T]): Cancelable = {
    import monix.execution.Scheduler.Implicits.global
    val cancelable = a.subscribe()
    des.append(_ => cancelable.cancel())
    cancelable
  }

  def event[T <: Event](ty: String,
    listener: T => Any): Unit = {
    event(dom, ty, listener)
  }

  def event[T <: Event](node: EventTarget, ty: String,
    listener: T => Any): Unit = {
    val li: js.Function1[T, _] =  (t: T) => {
      fpsStart()
      val res = listener(t)
      fpsEnd()
      res
    }
    if (des == null) throw new IllegalAccessException("Destroyed!")
    node.addEventListener(ty, li)
    defer(_ => node.removeEventListener(ty, li))
  }

  def focus(): Unit = {
    dom.focus()
  }


  def scrollToTop(): Unit =
    dom.scrollTop = 0

  def scrollToBottom(): Unit =
  dom.scrollTop = dom.scrollHeight - dom.clientHeight

  def preventDefault(a: Event,notCancelable: () => Unit = () => console.log("Not cancelable event not handled")): Unit  = {
    if (a.cancelable) {
      a.preventDefault()
    } else {
      notCancelable()
    }
  }
}
