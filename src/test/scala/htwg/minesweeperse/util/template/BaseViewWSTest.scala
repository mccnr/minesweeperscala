package htwg.minesweeperse.util.template

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import htwg.minesweeperse.controller._
import htwg.minesweeperse.util.state._
import htwg.minesweeperse.model._
import htwg.minesweeperse.util.strategy._

class BaseViewWSTest extends AnyWordSpec {

  // Hilfsfunktion
  def dummyController(): GameController =
    val f = Field(2, 2, Vector.fill(2)(Vector.fill(2)(Cell(0))))
    GameController(f, StandardRevealStrategy())

  // MockView ohne echten InputThread
  class MockView(controller: GameController, inputs: List[String])
    extends BaseView(controller):

    private var inputQueue = inputs
    var outputLog: List[String] = Nil

    override def showWelcome(): Unit =
      outputLog ::= "welcome"

    override def showField(): Unit =
      outputLog ::= "field"

    override def readInput(): String =
      inputQueue match
        case Nil     => ""
        case h :: t =>
          inputQueue = t
          h

    override def parseInput(s: String): Option[InputCommand] =
      s match
        case "undo"    => Some(UndoCmd)
        case "redo"    => Some(RedoCmd)
        case "m"       => Some(Move(0,0))
        case _         => None

    override def handleInvalidInput(s: String): Unit =
      outputLog ::= "invalid"

    override def handleResult(result: ControllerResult): Unit =
      outputLog ::= s"result:$result"

    // update() notwendig, sonst wird der Test nie benachrichtigt
    override def update(): Unit =
      outputLog ::= "update"
  end MockView


  "BaseView" should {

    "block moves when in GameOverState" in {
      val controller = dummyController()
      controller.state = GameOverState()

      val view = MockView(controller, List("m", ""))

      // Nur einen Input manuell verarbeiten:
      view.start()       // welcome + field
      Thread.sleep(50)   // Thread startet
      Thread.sleep(50)

      view.outputLog.exists(_.startsWith("result")) shouldBe false
    }

    "block moves when in WinState" in {
      val controller = dummyController()
      controller.state = WinState()

      val view = MockView(controller, List("m", ""))

      view.start()
      Thread.sleep(50)

      view.outputLog.exists(_.startsWith("result")) shouldBe false
    }

    "execute UndoCmd" in {
      val controller = dummyController()

      controller.processMove(0,0)
      val before = controller.field

      val view = MockView(controller, List("undo", ""))

      view.start()
      Thread.sleep(50)

      controller.field should not equal before
    }

    "execute RedoCmd" in {
      val controller = dummyController()

      controller.processMove(0,0)
      controller.undo()
      val beforeRedo = controller.field

      val view = MockView(controller, List("redo", ""))

      view.start()
      Thread.sleep(50)

      controller.field should not equal beforeRedo
    }

    "handle invalid input" in {
      val controller = dummyController()

      val view = MockView(controller, List("invalid", ""))

      view.start()
      Thread.sleep(50)

      view.outputLog.contains("invalid") shouldBe true
    }

    "execute a Move command and call processMove" in {
      val controller = dummyController()
      val view = MockView(controller, List("m", ""))

      view.start()
      Thread.sleep(50)

      // Move(0,0) wurde ausgeführt, Zelle muss revealed sein
      controller.field.cells(0)(0).revealed shouldBe true
    }

    "handle invalid input via BaseView when parseInput returns None" in {
      val controller = dummyController()
      val view = MockView(controller, List("xyz", ""))

      view.start()
      Thread.sleep(50)

      view.outputLog.contains("invalid") shouldBe true
    }
  }
}
