package services

import models.pixabay.PixabayImage
import org.scalatestplus.play.PlaySpec
import play.core.server.Server
import play.api.routing.sird._
import play.api.mvc._
import play.api.libs.json._
import play.api.test._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class PixabayServiceSpec extends PlaySpec {

  val pixabayImage = PixabayImage("http://example.com")
  val word = "hello"

  "PixabayService" should {
    "consultar una imagen asociada" when {
      "devuelva la imagen" must {
        "retornar la imagen" in  {
          Server.withRouterFromComponents() { components =>
            import Results._
            import components.{ defaultActionBuilder => Action }
            {
              case GET(p"/api") =>
                Action {
                  Ok(Json.obj("hits" -> Json.toJson(Seq(pixabayImage))))
                }
            }
          } { implicit port =>
            WsTestClient.withClient { client =>
              val result = Await.result(new PixabayService(client, "/api").getPixabayImage(word).value, 10.seconds)
              result mustBe Right(pixabayImage)
            }
          }
        }
      }
      "no devuelva la imagen" must {
        "retornar error" in  {
          Server.withRouterFromComponents() { components =>
            import Results._
            import components.{ defaultActionBuilder => Action }
            {
              case GET(p"/api") =>
                Action {
                  Ok("[]")
                }
            }
          } { implicit port =>
            WsTestClient.withClient { client =>
              val result = Await.result(new PixabayService(client, "/api").getPixabayImage(word).value, 10.seconds)
              result mustBe Left("No se pudo obtener la imagen desde el servidor de Pixabay")
            }
          }
        }
      }
      "error en el servicio" must {
        "retornar error" in  {
          Server.withRouterFromComponents() { components =>
            import Results._
            import components.{ defaultActionBuilder => Action }
            {
              case GET(p"/api") =>
                Action {
                  InternalServerError("")
                }
            }
          } { implicit port =>
            WsTestClient.withClient { client =>
              val result = Await.result(new PixabayService(client, "/api").getPixabayImage(word).value, 10.seconds)
              result mustBe Left("Ocurrió un error al tratar descargar la imagen")
            }
          }
        }
      }
    }
  }

}
