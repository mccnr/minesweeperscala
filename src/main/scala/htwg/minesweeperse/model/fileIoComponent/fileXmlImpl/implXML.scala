package htwg.minesweeperse.model.fileIoComponent.fileXmlImpl

import htwg.minesweeperse.model.fileIoComponent.IFileIO
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.model.cell.Cell
import scala.xml.{Elem, PrettyPrinter}
import java.io.PrintWriter

class implXML extends IFileIO {

  override def save(field: IField, seconds: Int): Unit =
    val xml: Elem = // Als Scala Xml speichern
      <minesweeper>
        <rows>{field.rows}</rows>
        <cols>{field.cols}</cols>
        <seconds>{seconds}</seconds>
        <cells>
          {
          for // Iterieren über Cell Koordinaten
            r <- 0 until field.rows
            c <- 0 until field.cols
          yield // Für jede Koordinate bauen
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

    val pretty = new PrettyPrinter(120, 2).format(xml) // Formatieren
    val pw = new PrintWriter("minesweeper.xml") // In Datei schreiben
    pw.write(pretty)
    pw.close()

  override def load(): (IField, Int) =
    val file = scala.xml.XML.loadFile("minesweeper.xml")

    val rows = (file \ "rows").text.trim.toInt
    val cols = (file \ "cols").text.trim.toInt
    val seconds = (file \ "seconds").text.trim.toInt

    val empty = Vector.fill(rows, cols)(Cell(0)) // erstellt leeres grid

    val filled = (file \\ "cell").foldLeft(empty) { (grid, node) => // findet alle <cell> im xml, baut fiilled grid
      val r = (node \ "row").text.trim.toInt
      val c = (node \ "col").text.trim.toInt
      val value = (node \ "value").text.trim.toInt
      val revealed = (node \ "revealed").text.trim.toBoolean
      val flagged = (node \ "flagged").text.trim.toBoolean

      grid.updated(r, grid(r).updated(c, Cell(value, revealed, flagged)))
    }

    (new implFieldAdvanced(rows, cols, filled), seconds) // Neues Field
}