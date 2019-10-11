package services

import models.famousquote.FamousQuote
import org.scalatestplus.play.PlaySpec
import play.core.server.Server
import play.api.routing.sird._
import play.api.mvc._
import play.api.libs.json._
import play.api.test._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class FamousQuoteServiceSpec extends PlaySpec {

  val famousQuote = FamousQuote("Quote", "Author", "Category")

  "FamousQuoteServe" should {
    "consultar frases famosas" when {
      "devuelva una frase" must {
        "retornar la frase" in  {
          Server.withRouterFromComponents() { components =>
            import Results._
            import components.{ defaultActionBuilder => Action }
            {
              case GET(p"/") =>
                Action {
                  Ok(Json.toJson(Seq(famousQuote)))
                }
            }
          } { implicit port =>
            WsTestClient.withClient { client =>
              val result = Await.result(new FamousQuoteService(client, "/").getFamousQuote().value, 10.seconds)
              result mustBe Right(famousQuote)
            }
          }
        }
      }
      "no devuelva una frase" must {
        "retornar error" in  {
          Server.withRouterFromComponents() { components =>
            import Results._
            import components.{ defaultActionBuilder => Action }
            {
              case GET(p"/") =>
                Action {
                  Ok("[]")
                }
            }
          } { implicit port =>
            WsTestClient.withClient { client =>
              val result = Await.result(new FamousQuoteService(client, "/").getFamousQuote().value, 10.seconds)
              result mustBe Left("No se encontró una frase válida")
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
              case GET(p"/") =>
                Action {
                  InternalServerError("")
                }
            }
          } { implicit port =>
            WsTestClient.withClient { client =>
              val result = Await.result(new FamousQuoteService(client, "/").getFamousQuote().value, 10.seconds)
              result mustBe Left("Ocurrió un error en la petición consultando las frases")
            }
          }
        }
      }
    }
  }

}
