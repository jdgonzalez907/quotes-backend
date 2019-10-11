package models.famousquote

import play.api.libs.json.Json

case class FamousQuote(quote: String, author: String, category: String)

object FamousQuote {
  implicit val famousQuoteFormat = Json.format[FamousQuote]
}
