package shared.client

import autowire._
import com.softwaremill.quicklens._
import shared.data._
import boopickle.Default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class Client(server: Server, initial: ClientState) { self =>

  /**
    * connection state
    */
  val connected = ObservableProperty(true)

  /**
    * out facing state
    */
  val root = ObservableProperty(initial.document.root)

  private var committed: ClientState = initial
  private var uncommitted = Seq.empty[Transaction]
  def debugCommited = committed

  /**
    * request queue
    */
  private var requesting = false
  private var requests: Seq[Unit] = Seq.empty

  private def putBackAndMarkNotConnected(head: Unit) = {
    requests = head +: requests
    connected.update(false)
  }

  private def request[T](head: Unit, a: Future[ErrorT[T]], onSuccess: T => Unit): Unit = {
    requesting = true
    a.onComplete {
      case Success(Right(r)) =>
        self.synchronized {
          requesting = false
          onSuccess(r)
          tryTopRequest()
        }
      case Success(Left(error)) =>
        self.synchronized {
          requesting = false
          new IllegalStateException(error.toString).printStackTrace()
          error match {
            case ApiError.ClientVersionIsOlderThanServerCache =>
              // ignore this
              tryTopRequest()
            case ApiError.InvalidToken =>
              putBackAndMarkNotConnected(head)
          }
        }
      case Failure(t) =>
        self.synchronized {
          t.printStackTrace()
          requesting = false
          putBackAndMarkNotConnected(head)
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
          request[ClientStateUpdate](head, server.change(ClientStateSnapshot(committed), submit).call(), succsss => {
            updateFromServer(succsss)
          })
        case _ =>
      }
    }
  }

  def updating: Boolean = requesting
  def hasUncommited: Boolean = uncommitted.nonEmpty

  def sync(): Boolean = self.synchronized {
    if (requests.isEmpty) {
      requests = requests ++ Seq[Unit](Unit)
      tryTopRequest()
      true
    } else {
      if (!requesting) {
        throw new IllegalStateException("Not possible")
      }
      false
    }
  }


  private def updateFromServer(success: ClientStateUpdate) = {
    val take = success.document.acceptedLosersCount
    val winners = success.document.winners
    val loser = uncommitted.take(take)
    // TODO modal handling of winner deletes loser
    val (wp, lp) = Transaction.rebase(winners, loser, RebaseConflict.all)
    val doc = committed.document.modify(_.root).using { a => Transaction.apply(Transaction.apply(a, winners), lp)}
        .modify(_.version).using(_ + winners.size + take)
    committed = committed.copy(document = doc)
    val (wp0, uc) = Transaction.rebase(Seq(Transaction(wp)), uncommitted.drop(take), RebaseConflict.all)
    uncommitted = uc
    root.update(Transaction.apply(root.get, Seq(Transaction(wp0))))
  }


  def change(changes: Transaction): Unit = self.synchronized {
    root.update(Transaction.apply(root.get, Seq(changes)))
    uncommitted = uncommitted :+ changes
    sync()
  }
}
