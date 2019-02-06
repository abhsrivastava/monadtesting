package com.abhi.zio

import scalaz.zio.{App, IO}
import scalaz.zio.interop.catz._
import cats.Monad
import cats.syntax.flatMap._
import cats.implicits._
import scala.util.Try

object MonadTesting3 extends App {

    trait Error
    case class ErrorMsg(msg: List[String]) extends Error

    trait Console[F[_ <: Error, _]] {
        def readInput() : F[Error, Int]
        def print(msg: String) : F[Error, Unit]
    }

    class ConsoleImpl extends Console[IO] {
        def readInput() : IO[ErrorMsg, Int] = {
            IO.fromTry(Try(scala.io.StdIn.readLine().toInt)).leftMap(e => ErrorMsg(List(e.getMessage)))
        }
        def print(msg: String) : IO[ErrorMsg, Unit] = {
            IO.sync {println(msg)}
        }
    }

    trait Random[F[_ <: Error, _]] {
        def getRandomInt() : F[Error, Int]
    }

    class RandomImpl extends Random[IO] {
        def getRandomInt() : IO[Nothing, Int] = {
            IO.point(scala.util.Random.nextInt(100))
        }
    }

    class GameImpl {
        def gameLoop[E <: Error, F[+_, +_]](number: Int)(implicit C: Console[F], M : Monad[F[E, ?]]) : F[E, Unit] = {
            for {
                input <- C.readInput()
            } yield {
                if (number == input) C.print("you won").map(_ => ())
                else if (number > input) C.print("you guessed too high").flatMap(_ => gameLoop(number))
                else C.print("you guessed too low").flatMap(_ => gameLoop(number))
            }
        }
    }

    def run(param: List[String]) : IO[Nothing, ExitStatus] = {
        ???
    }
}