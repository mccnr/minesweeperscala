import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class FieldCellWSTest extends AnyWordSpec {

  "A Cell" should {

    "Check if isMine = true when value == 1" in {
      val c = Cell(1)
      c.isMine shouldBe true
    }

    "Check if isMine = false when value != 1" in {
      val c = Cell(0)
      c.isMine shouldBe false
    }

    "Display '*' for mines" in {
      Cell(1).display shouldBe "*"
    }

    "Display '.' for non-mines" in {
      Cell(0).display shouldBe "."
    }
  }

  "A Field" should {

    "Display correct structure and symbols for 1x1 field" in {
      val cells = Vector(
        Vector(Cell(1))
      )
      val field = Field(1, 1, cells)

      val out = field.show()

      out should include("| * |")
      out should startWith("-")
      out.trim should endWith("-")
    }

    "Display correct structure and symbols for 1x2 field" in {
      val cells = Vector(
        Vector(Cell(1), Cell(0))
      )
      val field = Field(1, 2, cells)

      val out = field.show()

      out should include("| * . |")
      out should startWith("-")
      out.trim should endWith("-")
    }

    "Display correct structure and symbols for 2x1 field" in {
      val cells = Vector(
        Vector(Cell(1)),
        Vector(Cell(0))
      )
      val field = Field(2, 1, cells)

      val out = field.show()

      out should include("| * |")
      out should include("| . |")
      out should startWith("-")
      out.trim should endWith("-")
    }
  }

  "randomField" should {

    "Create a 1x1 field with valid cell value" in {
      val f = randomField(1, 1)

      f.rows shouldBe 1
      f.cols shouldBe 1
      f.cells.length shouldBe 1
      f.cells.foreach(_.length shouldBe 1)

      // random Wert muss 0 oder 1 sein
      all(f.cells.flatten.map(_.value)) should (be(0) or be(1))
    }

    "Create a 1x2 field with valid cell values" in {
      val f = randomField(1, 2)

      f.rows shouldBe 1
      f.cols shouldBe 2
      f.cells.length shouldBe 1
      f.cells.foreach(_.length shouldBe 2)

      // random Werte müssen 0 oder 1 sein
      all(f.cells.flatten.map(_.value)) should (be(0) or be(1))
    }

    "Create a 2x1 field with valid cell values" in {
      val f = randomField(2, 1)

      f.rows shouldBe 2
      f.cols shouldBe 1
      f.cells.length shouldBe 2
      f.cells.foreach(_.length shouldBe 1)

      // random Werte müssen 0 oder 1 sein
      all(f.cells.flatten.map(_.value)) should (be(0) or be(1))
    }
  }

  "runGame" should {
    "Check Game Outputs" in {
      val out = runGame()
      out should include("Minesweeper")
      out should include("true") // weil cell1 = 1
      out should include("false") // weil cell2 = 0
    }
  }

  "runMain" should {
    "Run main" in {
      runMain() // Der rest von main wird abgearbeitet für die Coverage
    }
  }
}