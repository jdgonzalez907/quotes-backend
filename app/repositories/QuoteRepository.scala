package repositories

import cats.data.EitherT
import constants.AppConstant
import javax.inject.{Inject, Singleton}
import models.Quote
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.PostgresProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class QuoteRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[PostgresProfile]

  import dbConfig._
  import PostgresProfile.api._

  private class QuoteTable(tag: Tag) extends Table[Quote](tag, "QUOTE") {
    def ID = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def QUOTE = column[String]("QUOTE")
    def IMAGE = column[String]("IMAGE")

    def * = (ID, QUOTE, IMAGE) <> ((Quote.apply _).tupled, Quote.unapply)
  }

  private val quoteTable = TableQuery[QuoteTable]

  def createQuote(description: String, url: String): EitherT[Future, String, Quote] = {
    val insert = (quoteTable.map( quote => (quote.QUOTE, quote.IMAGE) )
      returning quoteTable.map( _.ID )
      into ((propsInsert, id) => Quote(id, propsInsert._1, propsInsert._2))
      ) += (description, url)
    EitherT(db.run(insert.asTry.map({
        case Success(quote) => Right(quote)
        case Failure(_: Throwable) => Left(s"Ocurrio un error al guardar la frase: ${description} con la imagen: ${url}")
    })).recover({
      case _: Throwable => Left(AppConstant.messageErrorBD)
    }))
  }

  def getAllQuotes(): EitherT[Future, String, Seq[Quote]] = {
    EitherT(db.run(quoteTable.sortBy(_.ID.desc).result.asTry.map({
      case Success(quoteList) => Right(quoteList)
      case Failure(_: Throwable) => Left("Ocurrio un error al listar las frases")
    })).recover({
      case _: Throwable => Left(AppConstant.messageErrorBD)
    }))
  }

  def getQuote(id: Int): EitherT[Future, String, Option[Quote]] = {
    EitherT(db.run(quoteTable.filter(_.ID === id).result.asTry.map({
      case Success(quoteList) => Right(quoteList.headOption)
      case Failure(_: Throwable) => Left(s"Ocurrio un error al buscar la frase con id: ${id.toString}")
    })).recover({
      case _: Throwable => Left(AppConstant.messageErrorBD)
    }))
  }

  def deleteQuote(id: Int): EitherT[Future, String, Int] ={
    EitherT( db.run(quoteTable.filter(_.ID === id).delete.asTry.map({
      case Success(quotesDeleted) => Right(quotesDeleted)
      case Failure(_: Throwable) => Left(s"Ocurrio un error al eliminar la frase con id: ${id.toString}")
    })).recover({
      case _: Throwable => Left(AppConstant.messageErrorBD)
    }))
  }

}
