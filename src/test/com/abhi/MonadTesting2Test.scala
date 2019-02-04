package com.abhi
import org.scalatest._
import cats.Id
import cats.effect.IO
import cats.implicits._

class TestConsole(number: Int) extends Cosole[Id] {
    var msgState: String = ""
    def readInput() : Id[Int] = number
    def print(msg: String) = {
        msgState = msg
        ()
    }
}

class TestSpec extends FlatSpec {
    val game = new GameLoopImpl()
    "Game Loop" should "say value is high" in {
        implicit val higherConsole = new TestConsole(15)
        game.gameLoop(10)
        higherConsole.msgState shouldEqual("you guessed to high")
    }
    "Game Loop" should "say value is low" in {
        implicit val lowerConsole = new TestConsole(5)
        game.gameLoop(10)
        lowerConsole.msgState shouldEqual("you guessed to low")
    }
    "Game Loop" should "say you won" in {
        implicit val lowerConsole = new TestConsole(5)
        game.gameLoop(5)
        lowerConsole.msgState shouldEqual("you won!")
    }
}