package htwg.minesweeperse.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.model.cell.api.ICell
import htwg.minesweeperse.model.field.api.IField

import htwg.minesweeperse.util.factory.cellFactory.CellCreator
import htwg.minesweeperse.util.factory.fieldFactory.RandomFieldCreator

class FieldCellWSTest extends AnyWordSpec {

 // Factories
  val cellFactory  = CellCreator()
  val fieldFactory = RandomFieldCreator()

 // Hilfsfunktionen
  def emptyCell(): ICell = cellFactory.create(0)
  def mineCell(): ICell  = cellFactory.create(1)

  def fieldFromCells(cells: Vector[Vector[ICell]]): IField =
    fieldFactory.fromCells(cells)

// Cell Tests
  "A Cell component" should {

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
      val field = fieldFromCells(Vector(Vector(mineCell())))
      val revealed = field.reveal(0, 0)

      revealed.isMine(0, 0) shouldBe true
      revealed.isRevealed(0, 0) shouldBe true
    }

    "display numbers or blank for revealed non-mines" in {
      val field = fieldFromCells(Vector(Vector(emptyCell())))
      val revealed = field.reveal(0, 0)

      revealed.isRevealed(0, 0) shouldBe true
    }
  }

// Field Tests
  "A Field component" should {

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

    "reveal should reveal a non-mine cell" in {
      val field = fieldFromCells(Vector(Vector(emptyCell())))
      val revealed = field.reveal(0, 0)

      revealed.isRevealed(0, 0) shouldBe true
    }

    "reveal should reveal a mine and mark game over condition" in {
      val field = fieldFromCells(Vector(Vector(mineCell())))
      val revealed = field.reveal(0, 0)

      revealed.hasRevealedMine shouldBe true
    }

    "reveal should flood-fill empty regions" in {
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val revealed = field.reveal(0, 0)

      for
        r <- 0 until revealed.rows
        c <- 0 until revealed.cols
      do
        revealed.isRevealed(r, c) shouldBe true
    }

    "isWin should return true only when all non-mines are revealed" in {
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

    "create a field with correct dimensions" in {
      val field = fieldFactory.create(5, 5)

      field.rows shouldBe 5
      field.cols shouldBe 5
    }

    "create a field that contains cells" in {
      val field = fieldFactory.create(4, 4)

      var foundCell = false
      for
        r <- 0 until field.rows
        c <- 0 until field.cols
      do
        foundCell = true

      foundCell shouldBe true
    }
    "A Cell component" should {

      "use default None for revealed non-mine cell" in {
        val cell = cellFactory.create(0).reveal()
        cell.display() shouldBe "?"
      }
    }
  }
}
