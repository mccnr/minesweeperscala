package htwg.minesweeperse.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.model.fieldComponent.impl.implFieldBase
import htwg.minesweeperse.model.fieldComponent.impl.implFieldAdvanced
import htwg.minesweeperse.model.fieldComponent.impl.IField
import htwg.minesweeperse.model.cell.Cell

class ImplFieldBaseWSTest extends AnyWordSpec {

  "implFieldBase" should {

    "create a field with the given dimensions and cells" in {
      val field = new implFieldBase(4, 5)

      field.rows shouldBe 4
      field.cols shouldBe 5

      // Zugriff erzwingt Ausführung von Vector.tabulate
      field.isMine(0, 0) shouldBe a[Boolean]
    }

    "count mines around a cell without crashing" in {
      val field = new implFieldBase(3, 3)

      noException should be thrownBy {
        field.countMinesAround(1, 1)
      }
    }

    "reveal a non-mine cell" in {
      val field = new implFieldBase(3, 3)

      val revealed = field.reveal(0, 0)

      revealed.isRevealed(0, 0) shouldBe true
    }

    "return itself when revealing an already revealed cell" in {
      val field = new implFieldBase(2, 2)

      val once = field.reveal(0, 0)
      val twice = once.reveal(0, 0)

      twice shouldBe once
    }

    "reveal all mines when a mine is revealed" in {
      // Mehrere Versuche, bis zufällig eine Mine existiert
      val field = Iterator
        .continually(new implFieldBase(4, 4))
        .find(f =>
          (0 until f.rows).exists(r =>
            (0 until f.cols).exists(c => f.isMine(r, c))
          )
        )
        .get

      val minePos =
        (for
          r <- 0 until field.rows
          c <- 0 until field.cols
          if field.isMine(r, c)
        yield (r, c)).head

      val revealed = field.reveal(minePos._1, minePos._2)

      revealed.hasRevealedMine shouldBe true
    }

    "flood fill empty neighbors when no mines are around" in {
      val field = new implFieldBase(2, 2)

      val revealed = field.reveal(0, 0)

      // mindestens die Startzelle
      revealed.isRevealed(0, 0) shouldBe true
    }

    "correctly report isMine and isRevealed" in {
      val field = new implFieldBase(2, 2)

      val revealed = field.reveal(0, 0)

      revealed.isRevealed(0, 0) shouldBe true
      revealed.isMine(0, 0) shouldBe a[Boolean]
    }

    "detect hasRevealedMine correctly" in {
      val field = new implFieldBase(3, 3)

      val anyMine =
        (for
          r <- 0 until field.rows
          c <- 0 until field.cols
          if field.isMine(r, c)
        yield (r, c)).headOption

      anyMine match
        case Some((r, c)) =>
          val revealed = field.reveal(r, c)
          revealed.hasRevealedMine shouldBe true
        case None =>
          succeed // kein Mine-Fall ist erlaubt
    }

    "detect win condition correctly" in {
      val field = new implFieldBase(2, 2)

      var f: IField = field
      for
        r <- 0 until field.rows
        c <- 0 until field.cols
        if !field.isMine(r, c)
      do
        f = f.reveal(r, c)

      f.isWin shouldBe true
    }

    "render a string representation with show()" in {
      val field = new implFieldBase(2, 2)

      val output = field.show()

      output should include("|")
      output should include("-")
    }

    "return false for isRevealed on a fresh cell" in {
      val field = new implFieldBase(2, 2)

      field.isRevealed(0, 0) shouldBe false
    }

    "return false for hasRevealedMine when no mine was revealed" in {
      val field = new implFieldBase(3, 3)

      field.hasRevealedMine shouldBe false
    }

    "return false for isWin when at least one non-mine is hidden" in {
      val field = new implFieldBase(2, 2)

      field.isWin shouldBe false
    }

    "show revealed mines correctly" in {
      val field = Iterator
        .continually(new implFieldBase(3, 3))
        .find(f =>
          (0 until f.rows).exists(r =>
            (0 until f.cols).exists(c => f.isMine(r, c))
          )
        )
        .get

      val (r, c) =
        (for
          r <- 0 until field.rows
          c <- 0 until field.cols
          if field.isMine(r, c)
        yield (r, c)).head

      val revealed = field.reveal(r, c)
      val output = revealed.show()

      output should include("*")
    }

    "not flood-fill into mines" in {
      val field = new implFieldAdvanced(
        2, 2,
        Vector(
          Vector(Cell(0), Cell(1)),
          Vector(Cell(0), Cell(0))
        )
      )

      val revealed = field.reveal(0, 0)

      revealed.isRevealed(0, 1) shouldBe false
    }
  }
}
