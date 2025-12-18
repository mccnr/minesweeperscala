package htwg.minesweeperse.util.template

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.controller.api.IController
import htwg.minesweeperse.controller.ControllerResult
import htwg.minesweeperse.util.state.*
import htwg.minesweeperse.util.command.*

import htwg.minesweeperse.util.factory.controllerFactory.ControllerCreator
import htwg.minesweeperse.util.factory.fieldFactory.RandomFieldCreator
import htwg.minesweeperse.util.factory.cellFactory.CellCreator
import htwg.minesweeperse.util.factory.revealFactory.StandardRevealCreator
import htwg.minesweeperse.controller.InputCommand
import htwg.minesweeperse.controller.{Move, UndoCmd, RedoCmd, InvalidCmd}

class BaseViewWSTest extends AnyWordSpec {

  /* --------------------------------------------------
   * Factories (Client kennt nur Creator)
   * -------------------------------------------------- */
  val cellCreator   = CellCreator()
  val fieldCreator  = RandomFieldCreator()
  val revealCreator = StandardRevealCreator()
  val controllerCreator = ControllerCreator()

  /* --------------------------------------------------
   * Hilfsfunktion
   * -------------------------------------------------- */
  def dummyController(): IController =
    val field = fieldCreator.fromCells(
      Vector.fill(2, 2)(cellCreator.create(0))
    )
    controllerCreator.create(field, revealCreator.create())

  /* --------------------------------------------------
   * MockView
   * -------------------------------------------------- */
  class MockView(controller: IController, inputs: List[String])
    extends BaseView(controller):

    private var inputQueue = inputs
    var outputLog: List[String] = Nil

    override def showWelcome(): Unit =
      outputLog ::= "welcome"

    override def showField(): Unit =
      outputLog ::= "field"

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
        case "m"    => Some(Move(0,0))
        case _      => None

    override def handleInvalidInput(s: String): Unit =
      outputLog ::= "invalid"

    override def handleResult(result: ControllerResult): Unit =
      outputLog ::= s"result:$result"

    override def update(): Unit =
      outputLog ::= "update"
  end MockView

  /* ==================================================
   * TESTS
   * ================================================== */
  "BaseView" should {

    "block moves when in GameOverState" in {
      val controller = dummyController()
      controller.state = GameOverState()

      val view = MockView(controller, List("m", ""))
      view.start()
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
      val before = controller.field

      val view = MockView(controller, List("redo", ""))
      view.start()
      Thread.sleep(50)

      controller.field should not equal before
    }

    "handle invalid input" in {
      val controller = dummyController()
      val view = MockView(controller, List("invalid", ""))

      view.start()
      Thread.sleep(50)

      view.outputLog.contains("invalid") shouldBe true
    }

    "execute a Move command" in {
      val controller = dummyController()
      val view = MockView(controller, List("m", ""))

      view.start()
      Thread.sleep(50)

      controller.field.isRevealed(0,0) shouldBe true
    }

    "handle InvalidCmd explicitly" in {
      val controller = dummyController()

      val view = new MockView(controller, List("bad", "")) {
        override def parseInput(s: String): Option[InputCommand] =
          Some(InvalidCmd)
      }

      view.start()
      Thread.sleep(50)

      view.outputLog.contains("invalid") shouldBe true
    }
  }
}
