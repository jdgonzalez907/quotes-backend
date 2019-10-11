package services

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class QuoteServiceSpec extends PlaySpec with MockitoSugar {

  "QuoteService" should {

    val quoteService = new QuoteService()
    val mockQuoteService = mock[QuoteService]
    val quote = "Hello! I'm good good"
    val quoteCleaned = "Hello Im good good"

    "limpiar la frase" when {
      "la frase tenga caracteres especiales" must {
        "retornar la frase limpia" in {

          val result = quoteService.cleanQuote(quote)

          result mustBe quoteCleaned
        }
      }
    }

    "obtener una palabra aleatoria de la frase" when {
      "se envie la frase" must {
        "retornar la palabra" in {

          when(mockQuoteService.cleanQuote(anyString)) thenReturn quoteCleaned

          val result = quoteService.getKeyWord(quote)

          quoteCleaned.split(" ").find(_ === result).toList.length mustBe 1
        }
      }
    }
  }

}
