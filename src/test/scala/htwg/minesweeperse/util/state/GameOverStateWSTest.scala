package htwg.minesweeperse.util.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.controllerComponent.impl.implGC
import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.util.strategy.revealComponent.impl.StandardRevealStrategy
import htwg.minesweeperse.util.state.ControllerResult.*

import htwg.minesweeperse.model.fileIoComponent.IFileIO

class GameOverStateWSTest extends AnyWordSpec {
  
  private object DummyFileIO extends IFileIO {
    override def save(field: IField, seconds: Int): Unit = ()
    override def load(): (IField, Int) =
      (new implFieldAdvanced(1, 1, Vector(Vector(Cell(0)))), 0)
  }

  // Hilfsfeld
  def field2x2(): IField = {
    val cells = Vector(
      Vector(Cell(0), Cell(0)),
      Vector(Cell(0), Cell(0))
    )
    new implFieldAdvanced(2, 2, cells)
  }

  // Tests
  "The GameOverState" should {

    "return the correct name" in {
      val state = new GameOverState
      state.name shouldBe "GameOver"
    }

    "set lastResult = GameOver when processMove is called" in {
      val controller =
        new implGC(field2x2(), new StandardRevealStrategy, DummyFileIO)

      // Vorbedingung
      controller.lastResult should not be GameOver

      val state = new GameOverState
      state.processMove(0, 0, controller)

      controller.lastResult shouldBe GameOver
    }
  }
}
