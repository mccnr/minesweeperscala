package htwg.minesweeperse.model.field.impl
import htwg.minesweeperse.model.cell.api.ICell
import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.model.cell._

case class implFieldA(
 rows: Int,
 cols: Int,
 cells: Vector[Vector[ICell]]
 ) extends IField:

  override def countMinesAround(r: Int, c: Int): Int =
    val neighbors =
      for
        dr <- -1 to 1
        dc <- -1 to 1
        if !(dr == 0 && dc == 0)
        nr = r + dr
        nc = c + dc
        if nr >= 0 && nr < rows && nc >= 0 && nc < cols
      yield cells(nr)(nc)
    neighbors.count(_.isMine)

  override def reveal(r: Int, c: Int): IField =
    if cells(r)(c).isRevealed then this
    else if cells(r)(c).isMine then revealAllMines()
    else
      val updated = revealOne(r, c)
      if countMinesAround(r, c) == 0 then
        revealEmptyNeighbors(updated, r, c)
      else updated

  override def revealOne(r: Int, c: Int): IField =
    val updatedRow =
      cells(r).updated(c, cells(r)(c).reveal())
    implFieldA(rows, cols, cells.updated(r, updatedRow))

  override def revealAllMines(): IField =
    val newCells =
      cells.map(_.map(cell =>
        if cell.isMine then cell.reveal() else cell
      ))
    implFieldA(rows, cols, newCells)

  private def revealEmptyNeighbors(field: IField, r: Int, c: Int): IField =
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

  override def isRevealed(r: Int, c: Int): Boolean =
    cells(r)(c).isRevealed

  override def isMine(r: Int, c: Int): Boolean =
    cells(r)(c).isMine

  override def hasRevealedMine: Boolean =
    cells.flatten.exists(c => c.isMine && c.isRevealed)

  override def isWin: Boolean =
    cells.flatten.forall(c => c.isMine || c.isRevealed)

  override def show(): String =
    val border = "-" * (cols * 2 + 3)
    val body =
      cells.zipWithIndex.map { (row, r) =>
        val line =
          row.zipWithIndex.map { (cell, c) =>
            if cell.isRevealed && !cell.isMine then
              val n = countMinesAround(r, c)
              cell.display(Some(n))
            else cell.display(None)
          }.mkString(" ")
        s"| $line |"
      }.mkString("\n")
    s"$border\n$body\n$border"