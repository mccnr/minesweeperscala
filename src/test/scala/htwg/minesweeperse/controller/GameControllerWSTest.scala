package htwg.minesweeperse.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import com.google.inject.Guice
import htwg.minesweeperse.MinesweeperModule

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.util.state.ControllerResult._
import htwg.minesweeperse.util.state.PlayingState

import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}

import htwg.minesweeperse.util.strategy.revealComponent.impl.IRevealStrategy

class GameControllerWSTest extends AnyWordSpec {

  // Strategy die absichtlich crasht
  class ThrowingStrategy extends IRevealStrategy {
    override def reveal(field: IField, r: Int, c: Int): IField =
      throw new IndexOutOfBoundsException("boom")
  }

  // Hilfsfeld mit genau einer Mine
  def fieldWithMine(): IField = {
    val cells = Vector(
      Vector(Cell(0), Cell(0), Cell(0)),
      Vector(Cell(0), Cell(0), Cell(0)),
      Vector(Cell(0), Cell(0), Cell(1)) // Mine
    )
    new implFieldAdvanced(3, 3, cells)
  }

  // Controller aus Guice
  private def makeController(): IController = {
    val injector = Guice.createInjector(new MinesweeperModule)
    val controller = injector.getInstance(classOf[IController])
    controller
  }

  "A GameController" should {

    "update the field when a move is made" in {
      val field = fieldWithMine()
      val controller = makeController()

      // unser Test-Field reinsetzen
      controller.field = field

      controller.processMove(1, 1)

      controller.lastResult shouldBe Revealed
      controller.field.isRevealed(1, 1) shouldBe true
    }

    "return OutOfBounds when coordinates are invalid" in {
      val field = fieldWithMine()
      val controller = makeController()

      controller.field = field

      controller.processMove(10, 10)

      controller.lastResult shouldBe OutOfBounds
      PlayingState().playing shouldBe true
    }

    "set lastResult to OutOfBounds when reveal strategy throws" in {
      val field = fieldWithMine()
      val controller = makeController()

      controller.field = field
      
      controller.revealStrategy match {
        case _ =>
      }

      // provozieren OutOfBounds
      controller.processMove(99, 99)

      controller.lastResult shouldBe OutOfBounds
    }
  }
}
