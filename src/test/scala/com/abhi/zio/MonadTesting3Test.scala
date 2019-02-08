package com.abhi.zio

import org.scalatest._
import cats.implicits._
import MonadTesting3._

class TestRandom extends Random[ErrorMsg, Either] {
    def getRandomInt() : Either[ErrorMsg, Int] = 10.asRight[ErrorMsg]
}

class TestConsole extends Console[ErrorMsg, Either] {
    var values : List[Int] = List(20, 15, 5, 2, 10)
    var results : Array[String] = Array.empty[String]
    def readInput() : Either[ErrorMsg, Int] = {
        val retVal = values.head
        values = values.tail
        retVal.asRight[ErrorMsg]
    }
    def print(msg: String) : Either[ErrorMsg, Unit] = {
        results = msg +: results
        ().asRight[ErrorMsg]
    }
}

class TestSpec2 extends FlatSpec with Matchers {
    "Guess Game" should "output right values " in {
        implicit val random = new TestRandom()
        implicit val console = new TestConsole()    
        val game = new GameImpl[ErrorMsg, Either]()
        val io : Either[ErrorMsg, Unit] = game.play()
        console.results(0) should equal ("you won!")
        console.results(1) should equal ("you guessed too low")
        console.results(2) should equal ("you guessed too low")
        console.results(3) should equal ("you guessed too high")
        console.results(4) should equal ("you guessed too high")    
    }
}