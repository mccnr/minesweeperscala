package htwg.minesweeperse.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import java.io.File
import java.nio.file.Files

import htwg.minesweeperse.model.fileIoComponent.fileJsonImpl.implJSON
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced, implFieldBase}
import htwg.minesweeperse.model.cell.Cell

import play.api.libs.json.*

class ImplJsonWSTest extends AnyWordSpec {

  private val jsonFile = new File("minesweeper.json")

  private def deleteJsonFile(): Unit =
    if jsonFile.exists() then jsonFile.delete()

  "implJSON" should {

    "serialize and deserialize Cell including flagged + fallback when flagged is missing" in {
      val jsonIO = new implJSON

      // Writes[Cell] abdecken
      val cell = Cell(value = 0, revealed = true, flagged = true)
      val js = Json.toJson(cell)(using jsonIO.given_Writes_Cell)

      (js \ "value").as[Int] shouldBe 0
      (js \ "revealed").as[Boolean] shouldBe true
      (js \ "flagged").as[Boolean] shouldBe true

      // Reads[Cell] und flagged abdecken
      val parsed = js.as[Cell](using jsonIO.given_Reads_Cell)
      parsed.value shouldBe 0
      parsed.revealed shouldBe true
      parsed.flagged shouldBe true

      // Reads[Cell] flagged missing
      val jsMissingFlag = Json.obj(
        "value" -> 0,
        "revealed" -> false
        // flagged fehlt absichtlich
      )

      val parsed2 = jsMissingFlag.as[Cell](using jsonIO.given_Reads_Cell)
      parsed2.flagged shouldBe false
      parsed2.revealed shouldBe false
    }

    "serialize and deserialize FieldData including seconds + fallback seconds=0 when missing" in {
      val jsonIO = new implJSON

      val cells = Vector(
        Vector(Cell(0, revealed = false, flagged = true), Cell(1, revealed = true, flagged = false)),
        Vector(Cell(0, revealed = true, flagged = false), Cell(0, revealed = false, flagged = false))
      )

      val fieldData = jsonIO.FieldData(
        rows = 2,
        cols = 2,
        seconds = 42,
        cells = cells
      )

      // Writes[FieldData]
      val js = Json.toJson(fieldData)(using jsonIO.given_Writes_FieldData)

      (js \ "rows").as[Int] shouldBe 2
      (js \ "cols").as[Int] shouldBe 2
      (js \ "seconds").as[Int] shouldBe 42

      // Reads[FieldData]
      val parsed = js.as[jsonIO.FieldData](using jsonIO.given_Reads_FieldData)
      parsed.rows shouldBe 2
      parsed.cols shouldBe 2
      parsed.seconds shouldBe 42
      parsed.cells(0)(0).flagged shouldBe true

      // Reads[FieldData] seconds fehlt
      val jsMissingSeconds = Json.obj(
        "rows" -> 2,
        "cols" -> 2,
        "cells" -> Json.toJson(cells)(using Writes.seq(Writes.seq(jsonIO.given_Writes_Cell)))
      )

      val parsed2 = jsMissingSeconds.as[jsonIO.FieldData](using jsonIO.given_Reads_FieldData)
      parsed2.seconds shouldBe 0
    }

    "save and load an implFieldAdvanced including seconds (full roundtrip)" in {
      deleteJsonFile()

      val jsonIO = new implJSON

      val cells = Vector(
        Vector(Cell(0, revealed = true, flagged = false), Cell(1, revealed = false, flagged = true)),
        Vector(Cell(0, revealed = false, flagged = false), Cell(0, revealed = true, flagged = false))
      )

      val field: IField = new implFieldAdvanced(2, 2, cells)

      // Save Case f: implFieldAdvanced
      jsonIO.save(field, 99)
      jsonFile.exists() shouldBe true

      // Load
      val (loadedField, loadedSeconds) = jsonIO.load()

      loadedSeconds shouldBe 99
      loadedField.rows shouldBe 2
      loadedField.cols shouldBe 2

      loadedField.isRevealed(0, 0) shouldBe true
      loadedField.isFlagged(0, 1) shouldBe true
      loadedField.isMine(0, 1) shouldBe true

      deleteJsonFile()
    }

    "execute the else-branch when saving a non-implFieldAdvanced field" in {
      deleteJsonFile()

      val jsonIO = new implJSON
      val baseField: IField = new implFieldBase(2, 2)

      // Erzeuge absichtlich vorher eine Datei
      Files.writeString(jsonFile.toPath, "DONT CHANGE ME")

      val before = Files.readString(jsonFile.toPath)

      // else branch
      noException should be thrownBy {
        jsonIO.save(baseField, 5)
      }

      val after = Files.readString(jsonFile.toPath)

      // Datei darf NICHT überschrieben werden
      after shouldBe before

      deleteJsonFile()
    }

    "load should parse seconds=0 if the json file has no seconds field" in {
      deleteJsonFile()

      val jsonIO = new implJSON

      // JSON ohne "seconds" schreiben (um validateOpt[Int].getOrElse(0) zu testen)
      val customJson =
        Json.prettyPrint(
          Json.obj(
            "rows" -> 1,
            "cols" -> 1,
            "cells" -> Json.arr(
              Json.arr(
                Json.obj("value" -> 0, "revealed" -> false, "flagged" -> false)
              )
            )
          )
        )

      Files.writeString(jsonFile.toPath, customJson)

      val (loadedField, loadedSeconds) = jsonIO.load()

      loadedField.rows shouldBe 1
      loadedField.cols shouldBe 1
      loadedSeconds shouldBe 0

      deleteJsonFile()
    }
  }
}