package com.abhi.cats

import cats.FlatMap
import cats.effect.{IO, IOApp, ExitCode}
import cats.implicits._
import scala.util.Try
import cats.effect.syntax._

trait Console[F[_]] {
    def readInput(): F[Int]
    def print(msg: String): F[Unit]
}

trait Random[F[_]] {
    def getRandomInt() : F[Int]
}

class GameLoopImpl {
    def gameLoop[F[_]](number: Int)(implicit C: Console[F], M: FlatMap[F]) : F[Unit] = {
        for {
            input <- C.readInput
        } yield {
            if (number == input) C.print("you won!")
            else if (input > number) {
                C.print("you guessed too high").flatMap(_ => gameLoop(number))
            } else {
                C.print("you guessed too low").flatMap(_ => gameLoop(number))
            }
        }
    }
}

class GameImpl {
    val game = new GameLoopImpl()
    def play[F[_]: Random: Console: FlatMap]() : F[Unit] = {
        val random = implicitly[Random[F]]
        for {
            number <- random.getRandomInt()
            _ <- game.gameLoop(number)
        } yield()
    }
}

class ConsoleImpl extends Console[IO] {
    def readInt() : IO[Int] = {
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
    def run(params: List[String]) : IO[ExitCode] = {
        implicit val console = new ConsoleImpl()
        implicit val random = new RandomImpl()
        val game = new GameImpl()
        game.play().map(_ => ExitCode.Success)
    }
}