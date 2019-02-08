package com.abhi.zio

import scalaz.zio.{App, IO}
import scalaz.zio.interop.catz._
import cats.FlatMap
import cats.implicits._
import scala.util.Try

object MonadTesting3 extends App {

    trait Error
    case class ErrorMsg(msg: List[String]) extends Error

    trait Console[E <: Error, F[E, _]] {
        def readInput() : F[E, Int]
        def print(msg: String) : F[E, Unit]
    }

    class ConsoleImpl extends Console[ErrorMsg, IO] {
        def readInput() : IO[ErrorMsg, Int] = {
            print("Please enter a number") *> IO.fromTry(Try(scala.io.StdIn.readLine().toInt)).leftMap(e => ErrorMsg(List(e.toString)))
        }
        def print(msg: String) : IO[ErrorMsg, Unit] = {
            IO.sync {println(msg)}
        }
    }

    trait Random[E <: Error, F[E, _]] {
        def getRandomInt() : F[E, Int]
    }

    class RandomImpl extends Random[ErrorMsg, IO] {
        def getRandomInt() : IO[ErrorMsg, Int] = {
            IO.point(scala.util.Random.nextInt(100))
        }
    }

    class GameLoopImpl[E <: Error, F[+_, +_]] {
        def gameLoop(number: Int)(implicit C: Console[E, F], M: FlatMap[F[E, ?]]) : F[E, Unit] = {
            C.readInput().flatMap { input => 
                if (number == input) C.print("you won!")
                else if (input > number) C.print("you guessed too high").flatMap(_ => gameLoop(number))
                else C.print("you guessed too low").flatMap(_ => gameLoop(number))
            }
        }
    }

    class GameImpl[E <: Error, F[+_, +_]] {
        val game = new GameLoopImpl[E, F]()
        def play()(implicit C: Console[E, F], R: Random[E, F], M: FlatMap[F[E, ?]]) : F[E, Unit] = {
            for {
                number <- R.getRandomInt()
                retVal <- game.gameLoop(number)
            } yield retVal
        }
    }

    def run(param: List[String]) : IO[Nothing, ExitStatus] = {
        implicit val console = new ConsoleImpl()
        implicit val random = new RandomImpl()
        val game = new GameImpl[ErrorMsg, IO]()
        game.play().redeemPure(
            e => {e.msg.foreach(println); ExitStatus.ExitNow(1)}, 
            _ => ExitStatus.ExitNow(0)
        )
    }
}