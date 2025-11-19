package model

import htwg.minesweeperse.model.{Cell, Field}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class FieldCellWSTest extends AnyWordSpec {

  "A Cell" should {
    "have isMine = true when value == 1" in {
      val c = Cell(1)
      c.isMine shouldBe true
    }

    "have isMine = false when value != 1" in {
      val c = Cell(0)
      c.isMine shouldBe false
    }

    "display '?' for hidden cells" in {
      Cell(1).display() shouldBe "?"
      Cell(0).display() shouldBe "?"
    }

    "display '*' for revealed mines" in {
      Cell(1, revealed = true).display() shouldBe "*"
    }

    "display numbers or blank for revealed non-mines" in {
      val c = Cell(0, revealed = true)
      c.display(Some(0)) shouldBe " "
      c.display(Some(2)) shouldBe "2"
    }
  }

  "A Field" should {
    "count mines around a cell correctly" in {
      val cells = Vector(
        Vector(Cell(1), Cell(0)),
        Vector(Cell(0), Cell(1))
      )
      val field = Field(2, 2, cells)

      field.countMinesAround(0, 1) shouldBe 2
      field.countMinesAround(1, 0) shouldBe 2
    }

    "reveal should reveal a non-mine cell" in {
      val f = Field(1, 1, Vector(Vector(Cell(0))))
      val f2 = f.reveal(0, 0)
      f2.cells(0)(0).revealed shouldBe true
    }

    "reveal should reveal a mine cell" in {
      val f = Field(1, 1, Vector(Vector(Cell(1))))
      val f2 = f.reveal(0, 0)
      f2.cells(0)(0).revealed shouldBe true
      f2.cells(0)(0).isMine shouldBe true
    }

    "reveal should flood-fill empty regions" in {
      val cells = Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0))
      )
      val f = Field(2, 2, cells)
      val f2 = f.reveal(0, 0)
      all(f2.cells.flatten.map(_.revealed)) shouldBe true
    }

    "isWin should return true only when all non-mines are revealed" in {
      val cells = Vector(
        Vector(Cell(1), Cell(0)),
        Vector(Cell(0), Cell(0))
      )
      val f = Field(2, 2, cells)
      val f2 = f.reveal(0, 1).reveal(1, 0).reveal(1, 1)
      f2.isWin shouldBe true
    }

    "return the same Field object when revealing an already revealed cell" in {
      val cell = Cell(0, revealed = true)
      val field = Field(2, 2, Vector(
        Vector(cell, Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val result = field.reveal(0, 0)

      // Es sollte genau dasselbe Objekt zurückgegeben werden:
      assert(result eq field)
    }
  }
}