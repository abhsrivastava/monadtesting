package com.abhi.zio

import scalaz.zio.{App, IO}
import scalaz.zio.interop.catz._
import cats.Monad
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

    class GameImpl[E <: Error, F[E, ?] :Random : Monad] {

        def gameLoop() : F[E, Unit] = {
            ???
        }
    }

    def run(param: List[String]) : IO[Nothing, ExitStatus] = {
        ???
    }
}