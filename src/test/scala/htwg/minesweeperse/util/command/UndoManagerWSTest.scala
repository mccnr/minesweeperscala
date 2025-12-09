package htwg.minesweeperse.util.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class UndoManagerWSTest extends AnyWordSpec {

  // Dummy command für tests
  class TestCommand extends Command:
    var didUndo = false
    var didRedo = false

    override def doStep(): Unit = {}
    override def undoStep(): Unit = didUndo = true
    override def redoStep(): Unit = didRedo = true

  "UndoManager" should {

    "do nothing when undo() is called but undoStack is empty" in {
      val m = new UndoManager()

      noException shouldBe thrownBy {
        m.undo()
      }

      // test prüft, ob kein crash (für case Nil)
      succeed
    }

    "do nothing when redo() is called but redoStack is empty" in {
      val m = new UndoManager()

      noException shouldBe thrownBy {
        m.redo()
      }

      // test prüft, ob kein crash (für case Nil)
      succeed
    }
  }
}