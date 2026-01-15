package htwg.minesweeperse.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import java.io.File
import scala.util.Try

import htwg.minesweeperse.model.fileIoComponent.fileXmlImpl.implXML
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.model.cell.Cell

class ImplXmlWSTest extends AnyWordSpec {

  private val xmlFile = new File("minesweeper.xml")

  private def deleteXmlFile(): Unit =
    if xmlFile.exists() then xmlFile.delete()

  "implXML" should {

    "save a field with rows/cols/seconds/cells into minesweeper.xml" in {
      deleteXmlFile()

      val xmlIO = new implXML

      val cells = Vector(
        Vector(
          Cell(0, revealed = true, flagged = false),
          Cell(1, revealed = false, flagged = true)
        ),
        Vector(
          Cell(0, revealed = false, flagged = false),
          Cell(0, revealed = true, flagged = false)
        )
      )

      val field: IField = new implFieldAdvanced(2, 2, cells)

      // Deckt save(...) ab und for/yield + PrettyPrinter + PrintWriter.write/close
      xmlIO.save(field, seconds = 33)

      xmlFile.exists() shouldBe true

      val content = java.nio.file.Files.readString(xmlFile.toPath)

      // Einfache Checks ob die wichtigsten Teile drin sind
      content should include("<minesweeper>")
      content should include("<rows>2</rows>")
      content should include("<cols>2</cols>")
      content should include("<seconds>33</seconds>")

      // Cell tags existieren
      content should include("<cell>")
      content should include("<value>1</value>")
      content should include("<revealed>")
      content should include("<flagged>")

      deleteXmlFile()
    }

    "load a saved minesweeper.xml and restore field + seconds correctly" in {
      deleteXmlFile()

      val xmlIO = new implXML

      // Wir speichern erst, dann laden wir. Dies deckt beide Methoden sauber ab
      val cells = Vector(
        Vector(
          Cell(0, revealed = true, flagged = false),
          Cell(1, revealed = false, flagged = true)
        ),
        Vector(
          Cell(0, revealed = false, flagged = false),
          Cell(0, revealed = true, flagged = false)
        )
      )
      val field: IField = new implFieldAdvanced(2, 2, cells)

      xmlIO.save(field, seconds = 77)

      // Deckt load() ab und XML.loadFile + foldLeft + grid.updated(...) + implFieldAdvanced(...) + seconds
      val (loadedField, loadedSeconds) = xmlIO.load()

      loadedSeconds shouldBe 77
      loadedField.rows shouldBe 2
      loadedField.cols shouldBe 2

      // Werte prüfen
      loadedField.isRevealed(0, 0) shouldBe true
      loadedField.isMine(0, 1) shouldBe true
      loadedField.isFlagged(0, 1) shouldBe true

      loadedField.isRevealed(1, 1) shouldBe true
      loadedField.isMine(1, 1) shouldBe false
      loadedField.isFlagged(1, 1) shouldBe false

      deleteXmlFile()
    }
  }
}