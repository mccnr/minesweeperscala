package htwg.minesweeperse.util.strategy

import htwg.minesweeperse.model.Field
import htwg.minesweeperse.util.strategy.RevealStrategy

class StandardRevealStrategy extends RevealStrategy:
  override def reveal(field: Field, r: Int, c: Int): Field =
    
    if r < 0 || r >= field.rows || c < 0 || c >= field.cols then
      field
    else
      field.reveal(r, c)
