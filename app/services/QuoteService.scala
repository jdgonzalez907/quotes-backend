package services

import constants.AppConstant
import javax.inject.Singleton

import scala.util.Random

@Singleton
class QuoteService {

  def cleanQuote(quote: String): String = {
    AppConstant.cleanRegex.replaceAllIn(quote, "")
  }

  def getKeyWord(quote: String): String = {
    val quoteCleaned = cleanQuote(quote.toLowerCase)
    val quoteWordsList: Array[String] = quoteCleaned.split(" ").filter(_.length > 3)
    val random: Int = new Random().nextInt(quoteWordsList.length)
    quoteWordsList(random)
  }

}
