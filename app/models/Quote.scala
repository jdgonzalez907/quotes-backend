package models

import play.api.libs.json.{Format, Json}

case class Quote(id: Int, quote: String, image: String)

object Quote {

  implicit val quoteFormat = Json.format[Quote]

}

