package server

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, RouteResult}
import akka.util.ByteString
import boopickle.Default._
import java.nio.ByteBuffer

import scala.concurrent.{ExecutionContext, Future}
import shared._
import shared.api.Api
import shared.data._
import shared.server.CherryTreeServer

class ApiRouter(val service: Api) extends autowire.Server[ByteBuffer, Pickler, Pickler] {


  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)
  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)

  def dispatch(url: Seq[String])(implicit ec: ExecutionContext): RequestContext => Future[RouteResult] =
    entity(as[ByteString]) { entity =>
      val body = Unpickle[Map[String, ByteBuffer]].fromBytes(entity.asByteBuffer)
      val request: Future[ByteBuffer] = route[Api](service)(autowire.Core.Request(url, body))
      onSuccess(request)(buffer => complete(ByteString(buffer)))
    }
}
