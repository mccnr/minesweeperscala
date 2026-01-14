package htwg.minesweeperse.model.fileIoComponent.fileJsonImpl

import htwg.minesweeperse.model.fileIoComponent.IFileIO
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.model.cell.Cell

import play.api.libs.json.*
import scala.io.Source
import java.io.PrintWriter

class implJSON extends IFileIO {


  //Cell JSON (inkl. flagged)
  given Writes[Cell] = Writes { cell =>
    Json.obj(
      "value"    -> cell.value,
      "revealed" -> cell.revealed,
      "flagged"  -> cell.flagged
    )
  }

  given Reads[Cell] = Reads { json =>
    for
      value    <- (json \ "value").validate[Int]
      revealed <- (json \ "revealed").validate[Boolean]
      flagged  <- (json \ "flagged").validate[Boolean]
    yield Cell(value, revealed, flagged)
  }

  // FieldData Json
  case class FieldData(
   rows: Int,
   cols: Int,
   cells: Vector[Vector[Cell]]
                      )

  given Writes[FieldData] = Writes { f =>
    Json.obj(
      "rows"  -> f.rows,
      "cols"  -> f.cols,
      "cells" -> f.cells
    )
  }

  given Reads[FieldData] = Reads { json =>
    for
      rows  <- (json \ "rows").validate[Int]
      cols  <- (json \ "cols").validate[Int]
      cells <- (json \ "cells").validate[Vector[Vector[Cell]]]
    yield FieldData(rows, cols, cells)
  }

  // Save
  override def save(field: IField): Unit = {
    field match
      case f: implFieldAdvanced =>
        val data = FieldData(f.rows, f.cols, f.cells)

        val jsonString =
          Json.prettyPrint(
            Json.toJson(data)(using summon[Writes[FieldData]])
          )

        val pw = new PrintWriter("minesweeper.json")
        pw.write(jsonString)
        pw.close()

      case _ =>
        System.out.println("JSON save only supported for implFieldAdvanced")
  }

  // Load
  override def load(): IField = {
    val source = Source.fromFile("minesweeper.json").getLines().mkString
    val json = Json.parse(source)

    val data =
      json.as[FieldData](using summon[Reads[FieldData]])

    new implFieldAdvanced(data.rows, data.cols, data.cells)
  }
}