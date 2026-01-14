package htwg.minesweeperse.model.fileIoComponent.fileXmlImpl
import htwg.minesweeperse.model.fileIoComponent.IFileIO
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.model.cell.Cell

import scala.xml.{Elem, PrettyPrinter}
import java.io.PrintWriter

class implXML extends IFileIO {

  override def save(field: IField): Unit = {

    val xml: Elem =
      <minesweeper>
        <rows>{field.rows}</rows>
        <cols>{field.cols}</cols>

        <cells>
          {
          for
            r <- 0 until field.rows
            c <- 0 until field.cols
          yield
            <cell>
              <row>{r}</row>
              <col>{c}</col>

              <value>{if field.isMine(r, c) then 1 else 0}</value>
              <revealed>{field.isRevealed(r, c)}</revealed>
              <flagged>{field.isFlagged(r, c)}</flagged>
            </cell>
          }
        </cells>
      </minesweeper>

    val pretty = new PrettyPrinter(120, 2).format(xml)

    val pw = new PrintWriter("minesweeper.xml")
    pw.write(pretty)
    pw.close()
  }

  override def load(): IField = {

    val file = scala.xml.XML.loadFile("minesweeper.xml")

    val rows = (file \ "rows").text.trim.toInt
    val cols = (file \ "cols").text.trim.toInt

    val empty: Vector[Vector[Cell]] =
      Vector.fill(rows, cols)(Cell(0, revealed = false, flagged = false))

    val filled = (file \\ "cell").foldLeft(empty) { (grid, node) =>
      val r = (node \ "row").text.trim.toInt
      val c = (node \ "col").text.trim.toInt

      val value    = (node \ "value").text.trim.toInt
      val revealed = (node \ "revealed").text.trim.toBoolean
      val flagged  = (node \ "flagged").text.trim.toBoolean

      grid.updated(r, grid(r).updated(c, Cell(value, revealed, flagged)))
    }

    new implFieldAdvanced(rows, cols, filled)
  }
}