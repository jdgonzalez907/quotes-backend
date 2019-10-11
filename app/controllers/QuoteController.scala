package controllers

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import models.Quote
import models.httpresponse.{ResultDataResponse, ResultMessageResponse}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import repositories.QuoteRepository
import services.{FamousQuoteService, PixabayService, QuoteService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class QuoteController @Inject()(cc: ControllerComponents,
                                quoteRepository: QuoteRepository,
                                famousQuoteApiService: FamousQuoteService,
                                pixabayService: PixabayService,
                                quoteServie: QuoteService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllQuotes() = Action.async { implicit request: Request[AnyContent] =>
    quoteRepository.getAllQuotes().fold(
      (error: String) => InternalServerError( Json.toJson( ResultMessageResponse( error ) ) ),
      (quoteList: Seq[Quote]) => Ok( Json.toJson( ResultDataResponse[Seq[Quote]](quoteList ) ) )
    )
  }

  def getQuote(id: Int) = Action.async { implicit request: Request[AnyContent] =>
    quoteRepository.getQuote(id).fold(
      (error: String) => InternalServerError( Json.toJson( ResultMessageResponse( error ) ) ),
      (quote: Option[Quote]) => quote match {
        case None => NoContent
        case Some(exist) => Ok( Json.toJson( ResultDataResponse[Quote]( exist  ) ) )
      }
    )
  }

  def deleteQuote(id: Int) = Action.async { implicit request: Request[AnyContent] =>
    quoteRepository.deleteQuote(id).fold(
      (error: String) => InternalServerError( Json.toJson( ResultMessageResponse(error) ) ),
      (quoteDeleted: Int) => quoteDeleted match {
        case 0 => NoContent
        case quantity if quantity > 0 => Ok( Json.toJson( ResultMessageResponse("La frase se eliminó correctamente.") ) )
      })
  }

  def generateRandomQuote() = Action.async { implicit request: Request[AnyContent] =>
    val quoteFuture = for {
      famousQuote <- famousQuoteApiService.getFamousQuote()
      words <- EitherT.rightT[Future, String](quoteServie.getKeyWord(famousQuote.quote))
      bingImage <- pixabayService.getPixabayImage(words)
      quote <- quoteRepository.createQuote(famousQuote.quote, bingImage.largeImageURL)
    } yield quote

    quoteFuture.fold(
      (error: String) => InternalServerError( Json.toJson( ResultMessageResponse(error) ) ),
      (quote: Quote) => Ok( Json.toJson( ResultDataResponse[Quote](quote) ) )
    )
  }

}
