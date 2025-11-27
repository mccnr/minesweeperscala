package htwg.minesweeperse.util.strategy

import htwg.minesweeperse.model.Field
import htwg.minesweeperse.util.strategy.RevealStrategy

class StandardRevealStrategy extends RevealStrategy:
  override def reveal(field: Field, r: Int, c: Int): Field =
    field.reveal(r, c)