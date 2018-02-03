package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import shared.server.CherryTreeServer


class HttpServer() {


  val api = new CherryTreeServer()
  val router = new HttpRouter(api)
  def run() {
    implicit val system: ActorSystem = ActorSystem("server-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val config = ConfigFactory.load()
    val interface = config.getString("http.interface")
    val port = config.getInt("http.port")

    import system.dispatcher
    Http().bindAndHandle(router.apply(), interface, port)
    println(s"Server online at http://$interface:$port")
  }
}
