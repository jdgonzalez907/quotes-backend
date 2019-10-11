package models.httpresponse

import models.Quote
import play.api.libs.json.Json

case class ResultDataResponse[T](data: T)

object ResultDataResponse {
  implicit val resultDataResponseQuoteFormat = Json.format[ResultDataResponse[Quote]]
  implicit val resultDataResponseQuoteListFormat = Json.format[ResultDataResponse[Seq[Quote]]]
}

