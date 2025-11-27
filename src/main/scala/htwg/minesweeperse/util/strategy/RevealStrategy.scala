package htwg.minesweeperse.util.strategy

import htwg.minesweeperse.model.Field

trait RevealStrategy:
  def reveal(field: Field, r: Int, c: Int): Field