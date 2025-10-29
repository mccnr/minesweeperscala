import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class WordSpecTest extends AnyWordSpec {

  "A Cell" should {

    "report isMine = true when value == 1" in {
      val c = Cell(1)
      c.isMine shouldBe true
    }

    "report isMine = false when value != 1" in {
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
      out.trim should endWith ("-") // trim wichtig, falls newline am Ende
    }
  }
}
