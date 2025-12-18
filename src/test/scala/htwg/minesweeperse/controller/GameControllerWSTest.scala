package htwg.minesweeperse.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.controller.ControllerResult.*
import htwg.minesweeperse.controller.api.IController

import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.model.cell.api.ICell

import htwg.minesweeperse.util.factory.controllerFactory.ControllerCreator
import htwg.minesweeperse.util.factory.fieldFactory.RandomFieldCreator
import htwg.minesweeperse.util.factory.cellFactory.CellCreator
import htwg.minesweeperse.util.factory.revealFactory.StandardRevealCreator

import htwg.minesweeperse.util.strategy.reveal.api.IRevealStrategy
import htwg.minesweeperse.util.state.PlayingState

class GameControllerWSTest extends AnyWordSpec {

  // Test strat, welche crashed
  class ThrowingStrategy extends IRevealStrategy:
    override def reveal(field: IField, r: Int, c: Int): IField =
      throw new IndexOutOfBoundsException("boom")

  // Factory Creator
  val cellCreator = new CellCreator
  val fieldCreator = new RandomFieldCreator
  val revealCreator = new StandardRevealCreator
  val controllerCreator = new ControllerCreator

  // Tests
  "A GameController" should {

    "update the field when a move is made" in {
      val field = fieldCreator.create(3, 3)
      val reveal = revealCreator.create()
      val controller = controllerCreator.create(field, reveal)

      controller.processMove(1, 1)

      controller.lastResult shouldBe Revealed
      controller.field.isRevealed(1, 1) shouldBe true
    }

    /* "end the game when a mine is revealed" in {
      val field = fieldCreator.create(2, 2)
      val reveal = revealCreator.create()
      val controller = controllerCreator.create(field, reveal)

      controller.processMove(0, 0)

      controller.lastResult shouldBe GameOver
      controller.field.hasRevealedMine shouldBe true
    } */

    "return OutOfBounds when coordinates are invalid" in {
      val field = fieldCreator.create(3, 3)
      val reveal = revealCreator.create()
      val controller = controllerCreator.create(field, reveal)

      controller.processMove(10, 10)

      controller.lastResult shouldBe OutOfBounds
      PlayingState().playing shouldBe true
    }

    "set lastResult to OutOfBounds when reveal strategy throws" in {
      val field = fieldCreator.create(2, 2)
      val controller = controllerCreator.create(field, new ThrowingStrategy)

      controller.processMove(99, 99)

      controller.lastResult shouldBe OutOfBounds
    }
  }
}
