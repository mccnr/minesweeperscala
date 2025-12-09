package htwg.minesweeperse.util.strategy

import htwg.minesweeperse.model.Field
import htwg.minesweeperse.util.strategy.RevealStrategy

class NoFloodRevealStrategy extends RevealStrategy:
  override def reveal(field: Field, r: Int, c: Int): Field =
    
    if r < 0 || r >= field.rows || c < 0 || c >= field.cols then
      return field

    val cell = field.cells(r)(c)

    if cell.isMine then
      field.revealAllMines()
    else
      // Normales Aufdecken ohne Flood-Fill
      field.updateCell(r, c, cell.copy(revealed = true))

