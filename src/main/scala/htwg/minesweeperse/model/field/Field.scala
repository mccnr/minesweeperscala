/* package htwg.minesweeperse.model.field

import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.field.api.IField

import scala.util.Random

case class Field(rows: Int, cols: Int, cells: Vector[Vector[Cell]]) extends IField:
  def countMinesAround(r: Int, c: Int): Int = {
    val neighbors = for
      dr <- -1 to 1
      dc <- -1 to 1
      if !(dr == 0 && dc == 0)
      nr = r + dr
      nc = c + dc
      if nr >= 0 && nr < rows && nc >= 0 && nc < cols
    yield cells(nr)(nc)
    neighbors.count(_.isMine)
  }

  def reveal(r: Int, c: Int): Field = {
    if cells(r)(c).revealed then this
    else if cells(r)(c).isMine then revealAllMines()
    else {
      val count = countMinesAround(r, c)
      val updated = updateCell(r, c, cells(r)(c).copy(revealed = true))
      if count == 0 then revealEmptyNeighbors(updated, r, c)
      else updated
    }
  }

  override def revealAllMines(): Field =
    val newCells = cells.map(_.map(cell =>
      if cell.isMine then cell.copy(revealed = true) else cell
    ))
    Field(rows, cols, newCells)

  def updateCell(r: Int, c: Int, newCell: Cell): Field = {
    val updatedRow = cells(r).updated(c, newCell)
    Field(rows, cols, cells.updated(r, updatedRow))
  }

  def revealEmptyNeighbors(field: Field, r: Int, c: Int): Field = {
    var newField = field
    for
      dr <- -1 to 1
      dc <- -1 to 1
      if !(dr == 0 && dc == 0)
      nr = r + dr
      nc = c + dc
      if nr >= 0 && nr < rows && nc >= 0 && nc < cols
    do if !newField.cells(nr)(nc).revealed && !newField.cells(nr)(nc).isMine then
      newField = newField.reveal(nr, nc)
    newField
  }

  def isWin: Boolean = cells.flatten.forall(c => c.isMine || c.revealed)

  def show(): String = {
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
  }

  override def hasRevealedMine: Boolean =
    cells.flatten.exists(c => c.isMine && c.revealed)

  override def isRevealed(r: Int, c: Int): Boolean =
    cells(r)(c).revealed

  override def isMine(r: Int, c: Int): Boolean =
    cells(r)(c).isMine

  override def minesAround(r: Int, c: Int): Int =
    countMinesAround(r, c)

  override def revealOne(r: Int, c: Int): IField =
    if cells(r)(c).revealed then this
    else
      val updatedRow = cells(r).updated(c, cells(r)(c).copy(revealed = true))
      Field(rows, cols, cells.updated(r, updatedRow))

object Field:
  def random(rows: Int, cols: Int, mineChance: Double = 0.2): Field =
    val generated = Vector.tabulate(rows, cols) { (_, _) =>
      val isMine = Random.nextDouble() < mineChance
      Cell(if isMine then 1 else 0)
    }
    Field(rows, cols, generated) */