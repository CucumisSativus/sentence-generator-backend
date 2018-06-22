package net.cucumbersome.sentenceGenerator.persistence

import java.io.{BufferedReader, File, FileReader}
import java.nio.file.{Files, StandardOpenOption}

import cats.effect._
import cats.syntax.all._
import io.circe
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import net.cucumbersome.sentenceGenerator.domain.{Haiku, HaikuId, Word}

import scala.collection.mutable.ListBuffer

trait HaikuRepository {
  def save(haiku: Haiku): IO[Unit]

  def all: IO[List[Haiku]]
}

class InMemoryHaikuRepostiory extends HaikuRepository {
  var haikus: List[Haiku] = List.empty

  override def save(haiku: Haiku): IO[Unit] = {
    haikus = haikus :+ haiku
    IO.unit
  }

  override def all: IO[List[Haiku]] = {
    IO.pure(haikus)
  }
}

class InFileHaikuRepository(file: File) extends HaikuRepository {

  import net.cucumbersome.sentenceGenerator.persistence.InFileHaikuRepository._

  override def save(haiku: Haiku): IO[Unit] =
    IO {
      val databaseHaiku = toDatabaseHaiku(haiku).asJson.noSpaces + "\n"
      Files.write(file.toPath, databaseHaiku.getBytes("UTF-8"), StandardOpenOption.APPEND, StandardOpenOption.CREATE)
      ()
    }

  //    IO(new java.io.PrintWriter(file)).flatMap { p =>
  //      val databaseHaiku = toDatabaseHaiku(haiku)
  //
  //      IO(p.append(databaseHaiku.asJson.noSpaces))
  //        .flatMap(_ => IO(p.close()))
  //        .onError {
  //          case _ => IO(p.close())
  //        }
  //    }


  override def all: IO[List[Haiku]] =
    IO(new BufferedReader(new FileReader(file))).flatMap { in =>
      IO {
        val content = ListBuffer.empty[String]
        var line: String = null
        do {
          line = in.readLine()
          if (line != null) content.append(line)
        } while (line != null)
        content.map(parseStringToHaiku).map(_.right.get).toList
      }.flatMap(result =>
        IO(in.close()).map(_ => result)
      ).onError {
        case _ => IO(in.close())
      }
    }

}

object InFileHaikuRepository {

  case class DatabaseHaiku(
                            id: String,
                            firstLine: String,
                            middleLine: String,
                            lastLine: String
                          )

  private def haikuLineToDbLine(s: Seq[Word]): String =
    s.map(_.value).mkString(" ")

  private def dbHaikuLineToHaikuLine(s: String): Seq[Word] =
    s.split(" ").map(Word.apply)

  def toDatabaseHaiku(haiku: Haiku): DatabaseHaiku = DatabaseHaiku(
    id = haiku.id.value,
    firstLine = haikuLineToDbLine(haiku.firstLine),
    middleLine = haikuLineToDbLine(haiku.middleLine),
    lastLine = haikuLineToDbLine(haiku.lastLine)
  )

  def fromDatabaseHaiku(dbHaiku: DatabaseHaiku): Haiku = Haiku(
    id = HaikuId(dbHaiku.id),
    firstLine = dbHaikuLineToHaikuLine(dbHaiku.firstLine),
    middleLine = dbHaikuLineToHaikuLine(dbHaiku.middleLine),
    lastLine = dbHaikuLineToHaikuLine(dbHaiku.lastLine)
  )

  def parseStringToHaiku(line: String): Either[circe.Error, Haiku] = {
    for {
      json <- parse(line)
      parsed <- json.as[DatabaseHaiku]
      haiku = fromDatabaseHaiku(parsed)
    } yield haiku
  }
}
