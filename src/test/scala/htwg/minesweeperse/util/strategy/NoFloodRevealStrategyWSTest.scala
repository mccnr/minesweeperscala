package htwg.minesweeperse.util.strategy

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.implFieldAdvanced
import htwg.minesweeperse.util.strategy.revealComponent.impl.NoFloodRevealStrategy

class NoFloodRevealStrategyWSTest extends AnyWordSpec {

  // Hilfsfunktionen
  def emptyCell(): Cell = Cell(0)
  def mineCell(): Cell  = Cell(1)

  def fieldFromCells(cells: Vector[Vector[Cell]]) =
    new implFieldAdvanced(
      cells.length,
      cells.head.length,
      cells
    )

   // Tests
  "A NoFloodRevealStrategy" should {

    "reveal all mines if a mine is clicked" in {

      val field = fieldFromCells(
        Vector(
          Vector(mineCell(), emptyCell()),
          Vector(emptyCell(), mineCell())
        )
      )

      val strategy = new NoFloodRevealStrategy
      val newField = strategy.reveal(field, 0, 0)

      // Alle Minen revealed
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

      val strategy = new NoFloodRevealStrategy
      val newField = strategy.reveal(field, 0, 0)

      // Nur angeklickte Zelle
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

      val strategy = new NoFloodRevealStrategy
      val result = strategy.reveal(field, 5, 5)

      // Exakt dasselbe Objekt zurück
      result shouldBe field
    }
  }
}