package htwg.minesweeperse.model.fieldComponent.impl

import com.google.inject.Inject
import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.IField

class implFieldAdvanced @Inject()(
   val rows: Int,
   val cols: Int,
   val cells: Vector[Vector[Cell]]
   ) extends IField {

  override def countMinesAround(r: Int, c: Int): Int =
    (for
      dr <- -1 to 1
      dc <- -1 to 1
      if !(dr == 0 && dc == 0)
      nr = r + dr
      nc = c + dc
      if nr >= 0 && nr < rows && nc >= 0 && nc < cols
    yield cells(nr)(nc)).count(_.isMine)

  override def reveal(r: Int, c: Int): IField =
    if (cells(r)(c).isRevealed) this
    else if (cells(r)(c).isMine) revealAllMines()
    else {
      val updated = revealOne(r, c)
      if countMinesAround(r, c) == 0 then
        revealEmptyNeighbors(updated, r, c)
      else updated
    }

  override def revealOne(r: Int, c: Int): IField = {
    val updatedCells =
      cells.updated(r, cells(r).updated(c, cells(r)(c).reveal()))
    new implFieldAdvanced(rows, cols, updatedCells)
  }

  override def revealAllMines(): IField = {
    val updatedCells =
      cells.map(_.map(cell =>
        if cell.isMine then cell.reveal() else cell
      ))
    new implFieldAdvanced(rows, cols, updatedCells)
  }

  private def revealEmptyNeighbors(field: IField, r: Int, c: Int): IField = {
    var result = field
    for
      dr <- -1 to 1
      dc <- -1 to 1
      if !(dr == 0 && dc == 0)
      nr = r + dr
      nc = c + dc
      if nr >= 0 && nr < rows && nc >= 0 && nc < cols
      if !result.isRevealed(nr, nc) && !result.isMine(nr, nc)
    do
      result = result.reveal(nr, nc)
    result
  }

  override def isRevealed(r: Int, c: Int): Boolean =
    cells(r)(c).isRevealed

  override def isMine(r: Int, c: Int): Boolean =
    cells(r)(c).isMine

  override def hasRevealedMine: Boolean =
    cells.flatten.exists(c => c.isMine && c.isRevealed)

  override def isWin: Boolean =
    cells.flatten.forall(c => c.isMine || c.isRevealed)

  override def show(): String = {
    val border = "-" * (cols * 2 + 3)
    val body =
      cells.zipWithIndex.map { (row, r) =>
        val line =
          row.zipWithIndex.map { (cell, c) =>
            if cell.isRevealed && !cell.isMine then
              cell.display(Some(countMinesAround(r, c)))
            else cell.display()
          }.mkString(" ")
        s"| $line |"
      }.mkString("\n")
    s"$border\n$body\n$border"
  }
}