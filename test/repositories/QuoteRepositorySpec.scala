package repositories

import com.typesafe.config.ConfigFactory
import models.Quote
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.{Configuration, Mode}
import play.api.inject.guice.GuiceApplicationBuilder
import org.mockito._

import scala.concurrent.Await
import scala.concurrent.duration._

class QuoteRepositorySpec extends PlaySpec with MockitoSugar {

  val app = new GuiceApplicationBuilder()
    .loadConfig(new Configuration(ConfigFactory.load("conf/application.conf")))
    .in(Mode.Test)
    .build()

  val quoteRepository = app.injector.instanceOf[QuoteRepository]
  val mockQuoteRepository = mock

  var idQuote = 0
  val quoteModel = Quote(1, "Hola", "Image")

  "QuoteRepositorySpec" should {
    "crear una frase" when {
      "la información esté correcta y no existan problemas de base de datos" must {
        "devolver la frase creada" in {


          val result = quoteRepository.createQuote(quoteModel.quote, quoteModel.image)
          val realResult = Await.result(result.value, 5.seconds)

          realResult.map({
            case Quote(id, quote, image) => {
              idQuote = id
              quote mustBe quoteModel.quote
              image mustBe quoteModel.image
            }
          })
        }
      }
    }
    "obtener una frase" when {
      "la información esté correcta y no existan problemas de base de datos" must {
        "devolver la frase consultada" in {

          val result = quoteRepository.getQuote(idQuote)
          val realResult = Await.result(result.value, 5.seconds)

          realResult.map({
            case Some(Quote(_, quote, image)) => {
              quote mustBe quoteModel.quote
              image mustBe quoteModel.image
            }
          })
        }
      }
    }
    "obtener todas las frases" when {
      "la información esté correcta y no existan problemas de base de datos" must {
        "devolver la frases consultadas" in {

          val result = quoteRepository.getAllQuotes()
          val realResult = Await.result(result.value, 5.seconds)

          realResult.map({
            case lista: Seq[Quote] => {
              lista.length > 1
            }
          })
        }
      }
    }
    "eliminar una frase" when {
      "la información esté correcta y no existan problemas de base de datos" must {
        "devolver una frase eliminada" in {

          val result = quoteRepository.deleteQuote(idQuote)
          val realResult = Await.result(result.value, 5.seconds)

          realResult.map({
            case cantidad: Int => {
              cantidad mustBe 1
            }
          })
        }
      }
    }
  }

}
