package htwg.minesweeperse.model.fieldCell

import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class FieldCellWSTest extends AnyWordSpec {

  // Hilfsfunktionen
  def emptyCell(): Cell = Cell(0)
  def mineCell(): Cell  = Cell(1)

  def fieldFromCells(cells: Vector[Vector[Cell]]): IField =
    new implFieldAdvanced(cells.length, cells.head.length, cells)

  // Cell Tests
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

    "display 'F' for hidden flagged cells (flag has priority over '?')" in {
      val cell = emptyCell().toggleFlag()
      cell.display() shouldBe "F"
    }

    "not reveal a flagged cell (reveal returns same instance)" in {
      val flaggedCell = emptyCell().toggleFlag()
      val revealedTry = flaggedCell.reveal()

      revealedTry shouldBe flaggedCell
      revealedTry.isRevealed shouldBe false
      revealedTry.isFlagged shouldBe true
    }

    "not toggleFlag on an already revealed cell (toggleFlag returns same instance)" in {
      val revealedCell = emptyCell().reveal()
      val toggledTry   = revealedCell.toggleFlag()

      toggledTry shouldBe revealedCell
      toggledTry.isFlagged shouldBe false
      toggledTry.isRevealed shouldBe true
    }

    "display '*' for revealed mines" in {
      val cell = mineCell().reveal()
      cell.display() shouldBe "*"
    }

    "display blank for revealed non-mine cells with minesAround=0" in {
      val cell = emptyCell().reveal()
      cell.display(Some(0)) shouldBe " "
    }

    "display number for revealed non-mine cells when minesAround is defined" in {
      val cell = emptyCell().reveal()
      cell.display(Some(3)) shouldBe "3"
    }

    "use default '?' when revealed non-mine cell is displayed without context" in {
      val cell = emptyCell().reveal()
      cell.display() shouldBe "?"
    }
  }

  // Field Tests
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

    "return itself when revealing a flagged cell (field.reveal should return this)" in {
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell().toggleFlag())
        )
      )

      val revealed = field.reveal(0, 0)

      // reveal on flagged must do nothing
      revealed shouldBe field
      revealed.isRevealed(0, 0) shouldBe false
      revealed.isFlagged(0, 0) shouldBe true
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

    "flood-fill should unflag flagged empty neighbors and reveal them" in {
      // Start (0,0) reveals, floodfill touches (0,1)
      // (0,1) ist flagged, muss unflag + revealed werden
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell().toggleFlag()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val revealed = field.reveal(0, 0)

      revealed.isRevealed(0, 1) shouldBe true
      revealed.isFlagged(0, 1) shouldBe false
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

    "toggleFlag should return itself when coords are out of bounds" in {
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val after = field.toggleFlag(-1, 0)

      after shouldBe field
    }

    "unflagAndRevealOne should remove flag and reveal that cell" in {
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell().toggleFlag())
        )
      )

      val updated = field.unflagAndRevealOne(0, 0)

      updated.isFlagged(0, 0) shouldBe false
      updated.isRevealed(0, 0) shouldBe true
    }

    "totalMines should count all mines" in {
      val field = fieldFromCells(
        Vector(
          Vector(mineCell(), emptyCell()),
          Vector(mineCell(), mineCell())
        )
      )

      field.totalMines shouldBe 3
    }

    "totalFlags should count all flagged cells" in {
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell().toggleFlag(), emptyCell()),
          Vector(emptyCell().toggleFlag(), mineCell())
        )
      )

      field.totalFlags shouldBe 2
    }
  }
}
