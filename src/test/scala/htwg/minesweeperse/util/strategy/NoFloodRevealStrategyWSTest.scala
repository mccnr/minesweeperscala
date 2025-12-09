package htwg.minesweeperse.util.strategy

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import htwg.minesweeperse.model.*

class NoFloodRevealStrategyWSTest extends AnyWordSpec {

  "A NoFloodRevealStrategy" should {

    "reveal all mines if a mine is clicked" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(1), Cell(0)),
        Vector(Cell(0), Cell(1))
      ))

      val strategy = NoFloodRevealStrategy()

      val newField = strategy.reveal(field, 0, 0)

      // sollten beide revealed werden
      newField.cells(0)(0).revealed shouldBe true
      newField.cells(1)(1).revealed shouldBe true

      // felder, welche keine minen sind bleiben versteckt
      newField.cells(0)(1).revealed shouldBe false
      newField.cells(1)(0).revealed shouldBe false
    }

    "reveal only the selected cell when no mine is clicked, no flood fill" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val strategy = NoFloodRevealStrategy()

      val newField = strategy.reveal(field, 0, 0)

      // nur die gedrückte zelle wird revealed
      newField.cells(0)(0).revealed shouldBe true

      // der rest versteckt
      newField.cells(0)(1).revealed shouldBe false
      newField.cells(1)(0).revealed shouldBe false
      newField.cells(1)(1).revealed shouldBe false
    }

    "return the unchanged field when coordinates are out of bounds" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val strat = NoFloodRevealStrategy()

      // Out Of Bounds Koordinaten: r = 5, c = 5
      val result = strat.reveal(field, 5, 5)

      // Exakt dasselbe Field zurückgeben, da es invalide Koordinaten sind
      result shouldBe field
    }
  }
}