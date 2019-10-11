package models.pixabay

import play.api.libs.json.Json

case class PixabayImage(largeImageURL: String)

object PixabayImage {
  implicit val bingImageFormat = Json.format[PixabayImage]
}
