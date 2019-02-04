package com.abhi
import org.scalatest._
import cats.Id
import cats.effect.IO
import cats.implicits._

class TestConsole extends Console[Id] {
    var values : List[Int] = List(20, 5, 10)
    var results : Array[String] = Array.empty[String]
    def readInput() : Id[Int] = {
        val retVal = values.head
        values = values.tail
        retVal
    }
    def print(msg: String) = {
        results = msg +: results
        ()
    }
}

class TestSpec extends FlatSpec with Matchers {
    val game = new GameLoopImpl()
    implicit val console = new TestConsole()
    game.gameLoop(10)
    console.results(0) should equal ("you won!")
    console.results(1) should equal ("you guessed too low")
    console.results(2) should equal ("you guessed to high")
}