package htwg.minesweeperse.util.strategy

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.model.cell.api.ICell

import htwg.minesweeperse.util.factory.cellFactory.CellCreator
import htwg.minesweeperse.util.factory.fieldFactory.RandomFieldCreator
import htwg.minesweeperse.util.strategy.reveal.impl.NoFloodRevealStrategy

class NoFloodRevealStrategyWSTest extends AnyWordSpec {

  // Factories
  val cellCreator  = CellCreator()
  val fieldCreator = RandomFieldCreator()

  def emptyCell(): ICell = cellCreator.create(0)
  def mineCell(): ICell  = cellCreator.create(1)

  def fieldFromCells(cells: Vector[Vector[ICell]]): IField =
    fieldCreator.fromCells(cells)

  "A NoFloodRevealStrategy" should {

    "reveal all mines if a mine is clicked" in {

      val field = fieldFromCells(
        Vector(
          Vector(mineCell(), emptyCell()),
          Vector(emptyCell(), mineCell())
        )
      )

      val strategy = NoFloodRevealStrategy()

      val newField = strategy.reveal(field, 0, 0)

      // Minen müssen revealed sein
      newField.isRevealed(0, 0) shouldBe true
      newField.isRevealed(1, 1) shouldBe true

      // Nicht-Minen bleiben verdeckt
      newField.isRevealed(0, 1) shouldBe false
      newField.isRevealed(1, 0) shouldBe false
    }

    "reveal only the selected cell when no mine is clicked (no flood fill)" in {

      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val strategy = NoFloodRevealStrategy()

      val newField = strategy.reveal(field, 0, 0)

      // Nur die angeklickte Zelle
      newField.isRevealed(0, 0) shouldBe true

      // Kein Flood-Fill
      newField.isRevealed(0, 1) shouldBe false
      newField.isRevealed(1, 0) shouldBe false
      newField.isRevealed(1, 1) shouldBe false
    }

    "return the unchanged field when coordinates are out of bounds" in {

      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val strategy = NoFloodRevealStrategy()

      val result = strategy.reveal(field, 5, 5)

      // Gleiches Objekt (keine Änderung)
      result shouldBe field
    }
  }
}