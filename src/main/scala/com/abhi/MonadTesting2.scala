package com.abhi

import cats.Monad
import cats.effect.{IO, IOApp, ExitCode}
import cats.implicits._
import scala.util.Try
import cats.effect.syntax._

trait Console[F[_]] {
    def readInput(): F[Int]
    def print(msg: String): F[Unit]
}

trait Random[F[_]] {
    def getRandomInt: F[Int]
}

class GameLoopImpl {
    def gameLoop[F[_] : Monad](number: Int)(implicit C: Console[F]) : F[Unit] = {
        C.readInput().flatMap{input => 
            if (number == input) C.print("you won!").map(_ => ())
            else if (input > number) {
                C.print("you guessed to high").flatMap(_ => gameLoop(number))
            } else {
                C.print("you guessed too low").flatMap(_ => gameLoop(number))
            }
        }
    }
}

class ConsoleImpl extends Console[IO] {
    private def readInt() : IO[Int] = {
        Try(readLine().toInt).toOption match {
            case Some(x) if x <= 100 => IO.pure(x)
            case _ => print("Wrong Number").flatMap{ _ => readInput()}
        }
    }
    def readInput(): IO[Int] = print("please enter a number") *> readInt()
    def print(msg: String) : IO[Unit] = IO(println(msg))
}

class RandomImpl extends Random[IO] {
    def getRandomInt() : IO[Int] = {
        IO.pure(scala.util.Random.nextInt(100))
    }
}

object MonadTesting2 extends IOApp {
    val random = new RandomImpl()
    implicit val console = new ConsoleImpl()
    val game = new GameLoopImpl()
    for {
        number <- random.getRandomInt()
        _ <- game.gameLoop[IO](number)
    } yield (ExitCode.Success)
}