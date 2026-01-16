package htwg.minesweeperse.util.template

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.controllerComponent.impl.implGC

import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.model.fileIoComponent.IFileIO

import htwg.minesweeperse.util.command.*
import htwg.minesweeperse.util.state.*
import htwg.minesweeperse.util.strategy.revealComponent.impl.StandardRevealStrategy

class BaseViewWSTest extends AnyWordSpec {

  private object DummyFileIO extends IFileIO {
    override def save(field: IField, seconds: Int): Unit = ()
    override def load(): (IField, Int) =
      (new implFieldAdvanced(1, 1, Vector(Vector(Cell(0)))), 0)
  }

  // Hilfsfunktion: Dummy Controller erzeugen
  def dummyController(): IController = {
    val cells = Vector.fill(2, 2)(Cell(0))
    val field = new implFieldAdvanced(2, 2, cells)
    new implGC(field, new StandardRevealStrategy, DummyFileIO)
  }

  // Controller Spy (um save/load/restart zu prüfen)
  class SpyController(inner: IController) extends IController {

    // flags für Tests
    var saveCalled: Boolean = false
    var loadCalled: Boolean = false
    var restartCalled: Boolean = false

    // Delegation (alles weiterreichen)
    override def field: IField = inner.field
    override def field_=(f: IField): Unit = inner.field = f

    override def revealStrategy = inner.revealStrategy

    override def lastResult: ControllerResult = inner.lastResult
    override def lastResult_=(r: ControllerResult): Unit = inner.lastResult = r

    override def state: GameState = inner.state
    override def state_=(s: GameState): Unit = inner.state = s

    override def timerSeconds: Int = inner.timerSeconds
    override def timerSeconds_=(s: Int): Unit = inner.timerSeconds = s

    override def processMove(r: Int, c: Int): Unit = inner.processMove(r, c)
    override def undo(): Unit = inner.undo()
    override def redo(): Unit = inner.redo()

    override def toggleFlag(r: Int, c: Int): Unit = inner.toggleFlag(r, c)

    override def syncStateWithField(): Unit = inner.syncStateWithField()
    override def changeState(state: GameState): Unit = inner.changeState(state)
    override def autoFlagAllMines(): Unit = inner.autoFlagAllMines()

    override def save(): Unit = {
      saveCalled = true
      inner.save()
    }

    override def load(): Unit = {
      loadCalled = true
      inner.load()
    }

    override def restart(): Unit = {
      restartCalled = true
      inner.restart()
    }

    // Observer Pattern
    override def addObserver(o: htwg.minesweeperse.util.observer.Observer): Unit =
      inner.addObserver(o)

    override def removeObserver(o: htwg.minesweeperse.util.observer.Observer): Unit =
      inner.removeObserver(o)

    override def notifyObservers(): Unit =
      inner.notifyObservers()
  }

  // Mock View
  class MockView(controller: IController, inputs: List[String])
    extends BaseView(controller) {

    private var inputQueue = inputs
    var outputLog: List[String] = Nil

    // Exit in Tests NICHT wirklich Platform.exit() callen!
    var exitCalled: Boolean = false

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
        case "undo"    => Some(UndoCmd)
        case "redo"    => Some(RedoCmd)
        case "m"       => Some(Move(0, 0))
        case "save"    => Some(SaveCmd)
        case "load"    => Some(LoadCmd)
        case "restart" => Some(RestartCmd)
        case "exit"    => Some(ExitCmd)
        case _         => None

    override def handleInvalidInput(s: String): Unit =
      outputLog ::= "invalid"

    override def handleResult(result: ControllerResult): Unit =
      outputLog ::= s"result:$result"

    override def update(): Unit =
      outputLog ::= "update"

  }

   // Tests
  "BaseView" should {

    "block moves when in GameOverState" in {
      val controller = dummyController()
      controller.state = GameOverState()

      val view = new MockView(controller, List("m", ""))
      view.start()
      Thread.sleep(50)

      view.outputLog.exists(_.startsWith("result")) shouldBe false
    }

    "block moves when in WinState" in {
      val controller = dummyController()
      controller.state = WinState()

      val view = new MockView(controller, List("m", ""))
      view.start()
      Thread.sleep(50)

      view.outputLog.exists(_.startsWith("result")) shouldBe false
    }

    "execute UndoCmd" in {
      val controller = dummyController()
      controller.processMove(0, 0)
      val before = controller.field

      val view = new MockView(controller, List("undo", ""))
      view.start()
      Thread.sleep(50)

      controller.field should not equal before
    }

    "execute RedoCmd" in {
      val controller = dummyController()
      controller.processMove(0, 0)
      controller.undo()
      val before = controller.field

      val view = new MockView(controller, List("redo", ""))
      view.start()
      Thread.sleep(50)

      controller.field should not equal before
    }

    "handle invalid input" in {
      val controller = dummyController()
      val view = new MockView(controller, List("invalid", ""))

      view.start()
      Thread.sleep(50)

      view.outputLog.contains("invalid") shouldBe true
    }

    "execute a Move command" in {
      val controller = dummyController()
      val view = new MockView(controller, List("m", ""))

      view.start()
      Thread.sleep(50)

      controller.field.isRevealed(0, 0) shouldBe true
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

    "execute SaveCmd (controller.save) from BaseView loop" in {
      val spy = new SpyController(dummyController())

      val view = new MockView(spy, List("save", ""))
      view.start()
      Thread.sleep(50)

      spy.saveCalled shouldBe true
    }

    "execute LoadCmd (controller.load) from BaseView loop" in {
      val spy = new SpyController(dummyController())

      val view = new MockView(spy, List("load", ""))
      view.start()
      Thread.sleep(50)

      spy.loadCalled shouldBe true
    }

    "execute RestartCmd (controller.restart) from BaseView loop" in {
      val spy = new SpyController(dummyController())

      val view = new MockView(spy, List("restart", ""))
      view.start()
      Thread.sleep(50)

      spy.restartCalled shouldBe true
    }
  }
}
