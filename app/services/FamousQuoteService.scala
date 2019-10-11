package services

import cats.data.EitherT
import constants.FamousQuoteConstant
import javax.inject.{Inject, Singleton}
import models.famousquote.FamousQuote
import play.api.libs.json.{JsError, JsSuccess}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FamousQuoteService(wc: WSClient, baseUrl: String)(implicit ec: ExecutionContext) {
  @Inject def this(wc: WSClient, ec: ExecutionContext) = this(wc, FamousQuoteConstant.rapidApiUrl)(ec)

  def getFamousQuote(): EitherT[Future, String, FamousQuote] = {
    EitherT(wc.url(baseUrl)
      .addHttpHeaders((FamousQuoteConstant.rapidApiHostHeaderName, FamousQuoteConstant.rapidApiHostHeaderValue))
      .addHttpHeaders((FamousQuoteConstant.rapiApidKeyHeaderName, FamousQuoteConstant.rapidApiKeyHeaderValue))
      .get()
      .map( response => (response.json \ 0).validate[FamousQuote] match {
        case JsSuccess(famousQuote, _) => Right(famousQuote)
        case JsError(_) => Left("No se encontró una frase válida")
      })
      .recover({
        case _: Throwable => Left("Ocurrió un error en la petición consultando las frases")
      }))
  }

}
