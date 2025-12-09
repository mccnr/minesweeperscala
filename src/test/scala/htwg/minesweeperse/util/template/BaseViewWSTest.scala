package htwg.minesweeperse.util.template

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import htwg.minesweeperse.controller._
import htwg.minesweeperse.util.state._
import htwg.minesweeperse.util.command._
import htwg.minesweeperse.model._
import htwg.minesweeperse.util.strategy._

class BaseViewWSTest extends AnyWordSpec {

  // Hilfsfunktion
  def dummyController(): GameController =
    val f = Field(2, 2, Vector.fill(2)(Vector.fill(2)(Cell(0))))
    GameController(f, StandardRevealStrategy())

  // "Mock View"
  class MockView(controller: GameController, inputs: List[String]) extends BaseView(controller):

    private var inputQueue = inputs
    var outputLog: List[String] = Nil

    override def showWelcome(): Unit =
      outputLog ::= "welcome"

    override def showField(): Unit =
      outputLog ::= "showField"

    override def readInput(): String =
      inputQueue match
        case Nil => ""
        case h :: t =>
          inputQueue = t
          h

    override def parseInput(s: String): Option[InputCommand] =
      s match
        case "undo" => Some(UndoCmd)
        case "redo" => Some(RedoCmd)
        case "m" => Some(Move(0, 0))
        case "invalid" => None
        case _ => None

    override def handleInvalidInput(s: String): Unit =
      outputLog ::= "invalid"

    override def handleResult(result: ControllerResult): Unit =
      outputLog ::= s"result:$result"

  "BaseView" should {

    "block moves when in GameOverState" in {
      val controller = dummyController()
      controller.state = GameOverState()

      val view = MockView(controller, List("m", ""))

      view.startGameLoop()

      // Kein Result (weil Move blockiert)
      view.outputLog.exists(_.startsWith("result")) shouldBe false
    }

    "block moves when in WinState" in {
      val controller = dummyController()
      controller.state = WinState()

      val view = MockView(controller, List("m", ""))

      view.startGameLoop()

      view.outputLog.exists(_.startsWith("result")) shouldBe false
    }

    "execute UndoCmd" in {
      val controller = dummyController()

      // Erst einen Spielzug ausführen, damit ein Undo möglich ist
      controller.processMove(0,0)

      val before = controller.field

      val view = MockView(controller, List("undo", ""))

      view.startGameLoop()

      controller.field should not equal before  // Undo hat Zustand verändert
    }

    "execute RedoCmd" in {
      val controller = dummyController()

      // undo/redo Sequenz vorbereiten
      controller.processMove(0,0)
      controller.undo()

      val beforeRedo = controller.field

      val view = MockView(controller, List("redo", ""))

      view.startGameLoop()

      controller.field should not equal beforeRedo // Redo hat Zustand verändert
    }

    "handle invalid input" in {
      val controller = dummyController()

      val view = MockView(controller, List("invalid", ""))

      view.startGameLoop()

      view.outputLog.contains("invalid") shouldBe true
    }
  }
}