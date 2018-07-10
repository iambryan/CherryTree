package client


import java.nio.ByteBuffer

import autowire._
import com.softwaremill.quicklens._
import monix.execution.Cancelable
import monix.reactive.Observable
import monix.execution.rstreams.Subscription
import monix.reactive._

import concurrent.duration._
import monix.execution.Scheduler.Implicits.global
import api._
import model.ot.Rebased
import util._
import model._

import scala.concurrent.Future
import scala.util.{Failure, Success}


object Client {
  type Proxy = ClientProxy[Api, ByteBuffer, boopickle.Pickler, boopickle.Pickler]
}

class Client(
  private val server: Client.Proxy,
  private val initial: ClientInit,
  private val authentication: Authentication.Token
)  { self =>

  protected def lockObject: AnyRef  = self
  def debug_authentication: Authentication.Token = authentication

  /**
    * connection state
    */
  val connected = ObservableProperty(true)


  private var subscription: Cancelable = null

  def start(): Unit = {
    subscription = Observable.interval(300.millis).doOnNext(_ => sync()).subscribe()
  }

  def stop(): Unit = {
    if (subscription != null) {
      subscription.cancel()
      subscription = null
    }
  }


  private var committedVersion: Int = initial.version
  def debug_committedVersion = committedVersion
  private var committed: data.Node = initial.node
  def debug_committed = committed
  private var uncommitted = Seq.empty[transaction.Node]

  /**
    * document observable
    */
  val doc = ObservableProperty(ClientState(committed, Some(initial.mode)))

  /**
    * request queue
    */
  private var requesting = false
  private var requests: Seq[Int] = Seq.empty[Int]

  private def putBackAndMarkNotConnected(head: Int): Unit = {
    requests = head +: requests
    connected.update(false)
  }

  private def request[T](head: Int, a: Future[ErrorT[T]], onSuccess: T => Unit): Unit = {
    requesting = true
    transform(a).onComplete {
      case Success(r) =>
        self.synchronized {
          requesting = false
          onSuccess(r)
          tryTopRequest()
        }
      case Failure(t) =>
        self.synchronized {
          requesting = false
          t.printStackTrace()
          t match {
            case ApiError.ClientVersionIsOlderThanServerCache =>
              // LATER properly handle this!
              putBackAndMarkNotConnected(head)
            case ApiError.InvalidToken =>
              // LATER properly handle this!
              putBackAndMarkNotConnected(head)
            case _ =>
              putBackAndMarkNotConnected(head)
          }
        }
    }
  }


  private def tryTopRequest(): Unit = {
    if (!requesting) {
      connected.update(true)
      requests match {
        case head :: tail =>
          requests = tail
          val submit = uncommitted
          request[ClientUpdate](head, server.change(authentication, committedVersion, submit, doc.get.mode, if (debugModel) committed else data.Node.empty).call(), succsss => {
            updateFromServer(succsss)
          })
        case _ =>
      }
    }
  }

  def updating: Boolean = requesting

  def hasUncommited: Boolean = uncommitted.nonEmpty

  /**
    * sync with remote server
    */
  def sync(): Boolean = self.synchronized {
    if (requests.isEmpty) {
      requests = requests :+ 0
    }
    if (!requesting) {
      tryTopRequest()
      true
    } else {
      false
    }
  }


  /**
    * update committed, committed version, uncommited, state
    */
  private def updateFromServer(success: ClientUpdate): Unit = {
    try {
      val take = success.acceptedLosersCount
      val winners = success.winners
      val (loser, remaining) = uncommitted.splitAt(take)
      // LATER handle conflict, modal handling of winner deletes loser
      val Rebased(cs0, (wp, lp)) = ot.Node.rebaseT(winners.flatten, loser)
      committed = operation.Node.applyT(lp, operation.Node.applyT(winners, committed))
      val altVersion = committedVersion + winners.size + lp.size
      committedVersion = success.finalVersion
      assert(altVersion == committedVersion, s"Version wrong! $committedVersion $altVersion ${winners.size} $take")
      val Rebased(cs1, (wp0, uc)) = ot.Node.rebaseT(wp, remaining)
      uncommitted = uc
      doc.modify(it =>
        ClientState(operation.Node.apply(wp0, it.node),
          it.mode.flatMap(a => operation.Node.transform(wp0, a))
          ))
    } catch {
      case e: Exception =>
        throw new Exception(s"Apply update from server failed $success #### $committed", e)
    }
  }


  /**
    * submit a change to local state, a sync might follow
    *
    * if mode is defined, it is explicitly set **after** the change is applied
    * if not, the mode is transformed by the change
    */
  def change(changes: transaction.Node,
    mode: Option[model.mode.Node] = None,
    sync: Boolean = true): Unit = self.synchronized {
    var changed = false
    val d = if (changes.nonEmpty) {
      changed = true
      operation.Node.apply(changes, doc.get.node)
    } else {
      doc.get.node
    }
    val m = mode match {
      case Some(_) =>
        changed = true
        mode
      case None =>
        if (changed) {
          doc.get.mode.flatMap(a => operation.Node.transform(changes, a))
        } else {
          doc.get.mode
        }
    }
    if (changed) {
      doc.update(ClientState(d, m))
      uncommitted = uncommitted :+ changes
      if (sync) self.sync()
    }
  }
}