/* import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class FieldCellWSTest extends AnyWordSpec {

  // Test für einzelne Zellen
  "A Cell" should {

    // Prüft ob isMine korrekt funktioniert
    "Check if isMine = true when value == 1" in {
      val c = Cell(1)
      c.isMine shouldBe true
    }

    "Check if isMine = false when value != 1" in {
      val c = Cell(0)
      c.isMine shouldBe false
    }

    // Anzeige einer Zelle abhängig vom Zustand
    "Display '?' for hidden cells" in {
      Cell(1).display() shouldBe "?"
      Cell(0).display() shouldBe "?"
    }

    "Display '*' for revealed mines" in {
      Cell(1, revealed = true).display() shouldBe "*"
    }

    "Display numbers or blank for revealed non-mines" in {
      val c = Cell(0, revealed = true)
      c.display(Some(0)) shouldBe " "
      c.display(Some(2)) shouldBe "2"
    }
  }

  // Tests für das Spielfeld
  "A Field" should {

    // Struktur für ein 1x1 Feld prüfen (verdeckt)
    "Display correct structure and symbols for 1x1 field" in {
      val cells = Vector(Vector(Cell(1)))
      val field = Field(1, 1, cells)
      val out = field.show()

      out should include("| ? |")
      out should startWith("-")
      out.trim should endWith("-")
    }

    // 1x2 Feld prüfen (verdeckt)
    "Display correct structure and symbols for 1x2 field" in {
      val cells = Vector(Vector(Cell(1), Cell(0)))
      val field = Field(1, 2, cells)
      val out = field.show()

      out should include("| ? ? |")
      out should startWith("-")
      out.trim should endWith("-")
    }

    // 2x1 Feld prüfen (verdeckt)
    "Display correct structure and symbols for 2x1 field" in {
      val cells = Vector(Vector(Cell(1)), Vector(Cell(0)))
      val field = Field(2, 1, cells)
      val out = field.show()

      out should include("| ? |")
      out should include("| ? |")
      out should startWith("-")
      out.trim should endWith("-")
    }

  }

  // Test für randomField
  "randomField" should {

    "Create a 1x1 field with valid cell value" in {
      val f = randomField(1, 1)
      f.rows shouldBe 1
      f.cols shouldBe 1
      f.cells.length shouldBe 1
      f.cells.foreach(_.length shouldBe 1)
      all(f.cells.flatten.map(_.value)) should (be(0) or be(1))
    }

    "Create a 1x2 field with valid cell values" in {
      val f = randomField(1, 2)
      f.rows shouldBe 1
      f.cols shouldBe 2
      f.cells.length shouldBe 1
      f.cells.foreach(_.length shouldBe 2)
      all(f.cells.flatten.map(_.value)) should (be(0) or be(1))
    }

    "Create a 2x1 field with valid cell values" in {
      val f = randomField(2, 1)
      f.rows shouldBe 2
      f.cols shouldBe 1
      f.cells.length shouldBe 2
      f.cells.foreach(_.length shouldBe 1)
      all(f.cells.flatten.map(_.value)) should (be(0) or be(1))
    }
  }

  // verhindert interaktiven Start während Tests
  "runMain" should {
    "Run main" in {
      System.setProperty("test.env", "true")
      runMain()
    }
  }
}
*/
package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

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