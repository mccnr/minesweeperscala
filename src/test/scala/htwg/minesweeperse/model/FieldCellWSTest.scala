package htwg.minesweeperse.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}

class FieldCellWSTest extends AnyWordSpec {

  // Hilfsfunktionen
  def emptyCell(): Cell = Cell(0)
  def mineCell(): Cell  = Cell(1)

  def fieldFromCells(cells: Vector[Vector[Cell]]): IField =
    new implFieldAdvanced(cells.length, cells.head.length, cells)

  // ======================
  // Cell Tests
  // ======================
  "A Cell" should {

    "have isMine = true when value == 1" in {
      mineCell().isMine shouldBe true
    }

    "have isMine = false when value != 1" in {
      emptyCell().isMine shouldBe false
    }

    "display '?' for hidden cells" in {
      emptyCell().display() shouldBe "?"
      mineCell().display()  shouldBe "?"
    }

    "display '*' for revealed mines" in {
      val cell = mineCell().reveal()
      cell.display() shouldBe "*"
    }

    "display blank or number for revealed non-mine cells" in {
      val cell = emptyCell().reveal()
      cell.display(Some(0)) shouldBe " "
    }
  }

  // ======================
  // Field Tests
  // ======================
  "A Field" should {

    "count mines around a cell correctly" in {
      val field = fieldFromCells(
        Vector(
          Vector(mineCell(), emptyCell()),
          Vector(emptyCell(), mineCell())
        )
      )

      field.countMinesAround(0, 1) shouldBe 2
      field.countMinesAround(1, 0) shouldBe 2
    }

    "reveal a non-mine cell" in {
      val field = fieldFromCells(Vector(Vector(emptyCell())))
      val revealed = field.reveal(0, 0)

      revealed.isRevealed(0, 0) shouldBe true
    }

    "reveal a mine and mark game over condition" in {
      val field = fieldFromCells(Vector(Vector(mineCell())))
      val revealed = field.reveal(0, 0)

      revealed.hasRevealedMine shouldBe true
    }

    "flood-fill empty regions" in {
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val revealed = field.reveal(0, 0)

      for {
        r <- 0 until revealed.rows
        c <- 0 until revealed.cols
      } revealed.isRevealed(r, c) shouldBe true
    }

    "detect win when all non-mine cells are revealed" in {
      val field = fieldFromCells(
        Vector(
          Vector(mineCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val revealed =
        field
          .reveal(0, 1)
          .reveal(1, 0)
          .reveal(1, 1)

      revealed.isWin shouldBe true
    }

    "have correct dimensions" in {
      val field = fieldFromCells(
        Vector.fill(5, 5)(emptyCell())
      )

      field.rows shouldBe 5
      field.cols shouldBe 5
    }

    "create a field that contains cells" in {
      val field = fieldFromCells(
        Vector.fill(4, 4)(emptyCell())
      )

      for {
        r <- 0 until field.rows
        c <- 0 until field.cols
      } {
        field.isRevealed(r, c) shouldBe false
      }
    }

    "use default '?' when revealed non-mine cell is displayed without context" in {
      val cell = emptyCell().reveal()
      cell.display() shouldBe "?"
    }

  }
}
