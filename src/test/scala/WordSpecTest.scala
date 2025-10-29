import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class WordSpecTest extends AnyWordSpec {

  "A Cell" should {

    "isMine = true when value == 1" in {
      val c = Cell(1)
      c.isMine shouldBe true
    }

    "isMine = false when value != 1" in {
      val c = Cell(0)
      c.isMine shouldBe false
    }

    "display '*' for mines" in {
      Cell(1).display shouldBe "*"
    }

    "display '.' for non-mines" in {
      Cell(0).display shouldBe "."
    }
  }

  "A Field" should {

    "render with correct structure and symbols" in {
      val cells = Vector(
        Vector(Cell(1), Cell(0)),
        Vector(Cell(0), Cell(1))
      )
      val field = Field(2, 2, cells)

      val out = field.show()

      out should include ("| * . |")
      out should include ("| . * |")
      out should startWith ("-")
      out.trim should endWith ("-")
    }
  }

  "randomField" should {

    "create a field with the correct size and valid cell values" in {
      val f = randomField(3, 4)

      f.rows shouldBe 3
      f.cols shouldBe 4
      f.cells.length shouldBe 3
      f.cells.foreach(_.length shouldBe 4)

      // random werte, müssen 0 oder 1 sein
      all (f.cells.flatten.map(_.value)) should (be (0) or be (1))
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

  "Hello main" should {
    "Run main" in {
      Hello() // Der rest von main wird auch abgearbeitet für die Coverage
    }
  }
}