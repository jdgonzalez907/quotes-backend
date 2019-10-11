package controllers

import cats.data.EitherT
import cats.implicits._
import models.Quote
import models.famousquote.FamousQuote
import models.httpresponse.{ResultDataResponse, ResultMessageResponse}
import models.pixabay.PixabayImage
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import repositories.QuoteRepository
import services.{FamousQuoteService, PixabayService, QuoteService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class QuoteControllerSpec extends PlaySpec with MockitoSugar {
    val mockQuoteRepository = mock[QuoteRepository]
    val mockFamousQuoteService = mock[FamousQuoteService]
    val mockPixabayService = mock[PixabayService]
    val mockQuoteService = mock[QuoteService]

    val controller = new QuoteController(stubControllerComponents(), mockQuoteRepository, mockFamousQuoteService, mockPixabayService, mockQuoteService)

    val quote = Quote(1, "Ejemplo", "http://ejemplo.com")
    val genericError = "Error"
    val quantityZero = 0
    val quantityOne = 1
    val quoteList = Seq(quote)
    val famousQuote = FamousQuote("Quote", "author", "category")
    val quoteCleaned = "Quote cleaned"
    val pixabayImage = PixabayImage("http://example.com")

  "QuoteController" should {
    "consultar todas las frases registradas" when {
      "existan errores" must {
        "retornar el error" in {
          val expectResult = Json.toJson(ResultMessageResponse(genericError))

          when(mockQuoteRepository.getAllQuotes()) thenReturn EitherT.leftT[Future, Seq[Quote]](genericError)

          val result = controller.getAllQuotes().apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe INTERNAL_SERVER_ERROR
          realResult mustBe expectResult
        }
      }
      "no existan errores" must {
        "retornar las frases registradas" in {
          val expectResult = Json.toJson(ResultDataResponse[Seq[Quote]](quoteList))

          when(mockQuoteRepository.getAllQuotes()) thenReturn EitherT.rightT[Future, String](quoteList)

          val result = controller.getAllQuotes().apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe OK
          realResult mustBe expectResult
        }
      }
    }
    "consultar una frase por id" when {
      "existan errores" must {
        "retornar el error" in {
          val expectResult = Json.toJson(ResultMessageResponse(genericError))

          when(mockQuoteRepository.getQuote(anyInt)) thenReturn EitherT.leftT[Future, Option[Quote]](genericError)

          val result = controller.getQuote(anyInt).apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe INTERNAL_SERVER_ERROR
          realResult mustBe expectResult
        }
      }
      "no existan errores pero no encuentre la frase" must {
        "retornar error" in {
          val expectResult = ""

          when(mockQuoteRepository.getQuote(anyInt)) thenReturn EitherT.rightT[Future, String](None)

          val result = controller.getQuote(anyInt).apply(FakeRequest())
          val realResult = contentAsString(result)

          status(result) mustBe NO_CONTENT
          realResult mustBe expectResult
        }
      }
      "no existan errores y encuentre la frase" must {
        "retornar la frase" in {
          val expectResult = Json.toJson(ResultDataResponse[Quote]( quote))

          when(mockQuoteRepository.getQuote(anyInt)) thenReturn EitherT.rightT[Future, String](Some(quote))

          val result = controller.getQuote(anyInt).apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe OK
          realResult mustBe expectResult
        }
      }
    }
    "eliminar una frase por id" when {
      "existan errores" must {
        "retornar el error" in {
          val expectResult = Json.toJson(ResultMessageResponse(genericError))

          when(mockQuoteRepository.deleteQuote(anyInt)) thenReturn EitherT.leftT[Future, Int](genericError)

          val result = controller.deleteQuote(anyInt).apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe INTERNAL_SERVER_ERROR
          realResult mustBe expectResult
        }
      }
      "no existan errores pero no encuentre la frase" must {
        "retornar error" in {
          val expectResult = ""

          when(mockQuoteRepository.deleteQuote(anyInt)) thenReturn EitherT.rightT[Future, String](quantityZero)

          val result = controller.deleteQuote(anyInt).apply(FakeRequest())
          val realResult = contentAsString(result)

          status(result) mustBe NO_CONTENT
          realResult mustBe expectResult
        }
      }
      "no existan errores y encuentre la frase" must {
        "retornar mensaje de éxito" in {
          val expectResult = Json.toJson(ResultMessageResponse("La frase se eliminó correctamente."))

          when(mockQuoteRepository.deleteQuote(anyInt)) thenReturn EitherT.rightT[Future, String](quantityOne)

          val result = controller.deleteQuote(anyInt).apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe OK
          realResult mustBe expectResult
        }
      }
    }
    "crear una frase generateRandomQuote" when {
      "falle el servicio famousquote" must {
        "retornar el error" in {
          val expectResult = Json.toJson(ResultMessageResponse(genericError))

          when(mockFamousQuoteService.getFamousQuote()) thenReturn EitherT.leftT[Future, FamousQuote](genericError)

          val result = controller.generateRandomQuote().apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe INTERNAL_SERVER_ERROR
          realResult mustBe expectResult
        }
      }
      "falle el servicio pixabay" must {
        "retornar el error" in {
          val expectResult = Json.toJson(ResultMessageResponse(genericError))

          when(mockFamousQuoteService.getFamousQuote()) thenReturn EitherT.rightT[Future, String](famousQuote)
          when(mockQuoteService.getKeyWord(anyString)) thenReturn quoteCleaned
          when(mockPixabayService.getPixabayImage(anyString)) thenReturn EitherT.leftT[Future, PixabayImage](genericError)

          val result = controller.generateRandomQuote().apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe INTERNAL_SERVER_ERROR
          realResult mustBe expectResult
        }
      }
      "falle la creación" must {
        "retornar el error" in {
          val expectResult = Json.toJson(ResultMessageResponse(genericError))

          when(mockFamousQuoteService.getFamousQuote()) thenReturn EitherT.rightT[Future, String](famousQuote)
          when(mockQuoteService.getKeyWord(anyString)) thenReturn quoteCleaned
          when(mockPixabayService.getPixabayImage(anyString)) thenReturn EitherT.rightT[Future, String](pixabayImage)
          when(mockQuoteRepository.createQuote(anyString, anyString)) thenReturn EitherT.leftT[Future, Quote](genericError)

          val result = controller.generateRandomQuote().apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe INTERNAL_SERVER_ERROR
          realResult mustBe expectResult
        }
      }
      "no existan errores" must {
        "retornar la frase creada" in {
          val expectResult = Json.toJson(ResultDataResponse[Quote]( quote))

          when(mockFamousQuoteService.getFamousQuote()) thenReturn EitherT.rightT[Future, String](famousQuote)
          when(mockQuoteService.getKeyWord(anyString)) thenReturn quoteCleaned
          when(mockPixabayService.getPixabayImage(anyString)) thenReturn EitherT.rightT[Future, String](pixabayImage)
          when(mockQuoteRepository.createQuote(anyString, anyString)) thenReturn EitherT.rightT[Future, String](quote)

          val result = controller.generateRandomQuote().apply(FakeRequest())
          val realResult = contentAsJson(result)

          status(result) mustBe OK
          realResult mustBe expectResult
        }
      }
    }

  }

}
