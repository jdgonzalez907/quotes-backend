package models.httpresponse

import play.api.libs.json.Json

case class ResultMessageResponse(message: String)

object ResultMessageResponse {
  implicit val resultResponseQuoteFormat = Json.format[ResultMessageResponse]
}