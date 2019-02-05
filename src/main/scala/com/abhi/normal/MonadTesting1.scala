package com.abhi.normal

import cats.effect._
import scala.util.Try
import scala.io.{StdIn}

object MonadTesting1 extends IOApp {
    def print(msg: String) : IO[Unit] = IO(println(msg))

    def getRandomInt() : IO[Int] = {
        IO.pure(scala.util.Random.nextInt(100))
    }

    def readInput() : IO[Int] = {
        print("please enter a number").flatMap{_ => 
            Try(readLine().toInt).toOption match {
                case Some(x) if x <= 100 => IO.pure(x)
                case _ => print("Wrong Number").flatMap{ _ => readInput()}
            }
        }
    }

    def gameLogic(number: Int) : IO[Unit] = {
        readInput().flatMap{ input => 
            if (number == input) IO(println("you won.")).map(_ => IO(()))
            else if (input > number) {
                println(s"You guess too high")
                gameLogic(number)
            } else {
                println("You guess too low")
                gameLogic(number)
            }
        }
    }
    def run(params: List[String]) : IO[ExitCode] = {
        for {
            number <- getRandomInt()
            _ <- gameLogic(number)
        } yield (ExitCode.Success)
    }
}