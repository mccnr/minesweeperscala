import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class MineSelectWSTest extends AnyWordSpec {

  // Tests für Spiellogik für die Minen und das Zählen
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

    "reveal should fill empty regions" in {
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
  }
}
