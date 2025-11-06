import scala.util.Random

case class Cell(value: Int, revealed: Boolean = false):
  def isMine: Boolean = value == 1
  def display(minesAround: Option[Int] = None): String =
    if !revealed then "?"                        // verdeckt
    else if isMine then "*"                      // Mine
    else if minesAround.contains(0) then " "     // keine Minen um sich, d.h. leer
    else minesAround.map(_.toString).getOrElse("?") // Zahl anzeigen

// Feld aus mehreren Zeilen und Spalten
case class Field(rows: Int, cols: Int, cells: Vector[Vector[Cell]]):

  // Zähle Minen in der Umgebung einer Zelle
  def countMinesAround(r: Int, c: Int): Int =
    val neighbors = for
      dr <- -1 to 1
      dc <- -1 to 1
      if !(dr == 0 && dc == 0)
      nr = r + dr
      nc = c + dc
      if nr >= 0 && nr < rows && nc >= 0 && nc < cols
    yield cells(nr)(nc)
    neighbors.count(_.isMine)

  // Eine Zelle klicken
  def reveal(r: Int, c: Int): Field =
    if cells(r)(c).revealed then this // schon offen, nichts tun
    else if cells(r)(c).isMine then
      println("Du hast eine Mine getroffen.")
      revealAllMines()
    else
      val count = countMinesAround(r, c)
      val updated = updateCell(r, c, cells(r)(c).copy(revealed = true))
      if count == 0 then
        // Wenn keine Minen daneben, den Nachbarn automatisch öffnen
        revealEmptyNeighbors(updated, r, c)
      else updated

  // Markiert alle Minen als aufgedeckt
  def revealAllMines(): Field =
    val newCells = cells.map(_.map(cell =>
      if cell.isMine then cell.copy(revealed = true) else cell
    ))
    Field(rows, cols, newCells)

  // Hilfsfunktion, die Zelle im Raster ersetzen
  def updateCell(r: Int, c: Int, newCell: Cell): Field =
    val updatedRow = cells(r).updated(c, newCell)
    Field(rows, cols, cells.updated(r, updatedRow))

  // Deckt Nachbarn automatisch auf, wenn sie 0 Minen haben
  def revealEmptyNeighbors(field: Field, r: Int, c: Int): Field =
    var newField = field
    for
      dr <- -1 to 1
      dc <- -1 to 1
      if !(dr == 0 && dc == 0)
      nr = r + dr
      nc = c + dc
      if nr >= 0 && nr < rows && nc >= 0 && nc < cols
    do
      if !newField.cells(nr)(nc).revealed && !newField.cells(nr)(nc).isMine then
        newField = newField.reveal(nr, nc)
    newField

  // Siegbedingung prüfen
  def isWin: Boolean =
    cells.flatten.forall(c => c.isMine || c.revealed)

  // Ausgabe des Spielfelds
  def show(): String =
    val border = "-" * (cols * 2 + 3)
    val body = cells.zipWithIndex.map { (row, r) =>
      val line = row.zipWithIndex.map { (cell, c) =>
        if cell.revealed && !cell.isMine then
          val n = countMinesAround(r, c)
          cell.display(Some(n))
        else cell.display(None)
      }.mkString(" ")
      s"| $line |"
    }.mkString("\n")
    s"$border\n$body\n$border"

// Random Spielfeld erzeugen
def randomField(rows: Int, cols: Int, mineChance: Double = 0.2): Field =
  val generated = Vector.tabulate(rows, cols) { (_, _) =>
    val isMine = Random.nextDouble() < mineChance
    Cell(if isMine then 1 else 0)
  }
  Field(rows, cols, generated)