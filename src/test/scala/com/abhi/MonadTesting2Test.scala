package com.abhi
import org.scalatest._
import cats.Id
import cats.effect.IO
import cats.implicits._

class TestRandom extends Random[Id] {
    def getRandomInt() : Id[Int] = 10
}

class TestConsole extends Console[Id] {
    var values : List[Int] = List(20, 15, 5, 2, 10)
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
    implicit val random = new TestRandom()
    implicit val console = new TestConsole()
    "Guess Game" should "output right values " in {
        val game = new GameImpl()
        val io = game.play()
        console.results(0) should equal ("you won!")
        console.results(1) should equal ("you guessed too low")
        console.results(2) should equal ("you guessed too low")
        console.results(3) should equal ("you guessed to high")
        console.results(4) should equal ("you guessed to high")    
    }
}