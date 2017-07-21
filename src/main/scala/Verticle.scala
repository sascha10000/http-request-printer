import io.vertx.core.http.HttpMethod
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.CorsHandler
import scala.collection.JavaConverters._

/**
  * Created by Sascha on 14.07.2017.
  */
class Verticle extends ScalaVerticle {

  override def start(): Unit = {
    val router = Router.router(vertx)

    // handles all CORS requests
    router.route().handler(CorsHandler.create("*")
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.PUT)
      .allowedMethod(HttpMethod.DELETE)
      .allowedMethod(HttpMethod.OPTIONS)
      .allowedHeader("token")
      .allowedHeader("content-type"))


    router.route("/*").handler((ctx) => {
      val headers = ctx.request().headers()
      val params = ctx.request().params();
      ctx.request().bodyHandler((buff) => {
        val resp:String = "Headers:\n" + headers.names().foldLeft("")((a,b) => (if(a=="") "" else a + ",\n" )+ b + ":"+ headers.get(b).get )+ "\nParams:\n"+params.names().foldLeft("")((a,b) => (if(a=="") "" else a + ",\n" )+ b + ":"+ params.get(b).get ) + "\nBody:\n" + buff.toString()
        println(resp)
        ctx.response().end(resp)
      })
    })

    println("[RUNNING] localhost:80")

    vertx.createHttpServer().requestHandler(router.accept _).listen(80)
  }
}

object Verticle {
  def main(args: Array[String]): Unit = {
    //System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, classOf[SLF4JLogDelegateFactory].getName)
    Vertx.vertx.deployVerticle(ScalaVerticle.nameForVerticle[Verticle])
  }
}
