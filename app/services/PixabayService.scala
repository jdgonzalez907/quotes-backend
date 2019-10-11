package services

import cats.data.EitherT
import constants.PixabayConstant
import javax.inject.{Inject, Singleton}
import models.pixabay.PixabayImage
import play.api.libs.json.{JsError, JsSuccess}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PixabayService(wc: WSClient, baseUrl: String)(implicit ec: ExecutionContext) {
  @Inject def this(wc: WSClient, ec: ExecutionContext) = this(wc, PixabayConstant.pixabayApiUrl)(ec)

  def getPixabayImage(search: String): EitherT[Future, String, PixabayImage] = {
    EitherT(wc.url(baseUrl)
      .addQueryStringParameters((PixabayConstant.pixabayApiKeyName, PixabayConstant.pixabayApiKeyValue))
      .addQueryStringParameters((PixabayConstant.pixabayApiTypeImageName, search))
      .get()
      .map( response => (response.json \ "hits" \ 0).validate[PixabayImage] match {
        case JsSuccess(pixabaygImage, _) => Right(pixabaygImage)
        case JsError(_) => Left("No se pudo obtener la imagen desde el servidor de Pixabay")
      })
      .recover({
        case e: Throwable => Left("Ocurrió un error al tratar descargar la imagen")
      }))
  }

}
