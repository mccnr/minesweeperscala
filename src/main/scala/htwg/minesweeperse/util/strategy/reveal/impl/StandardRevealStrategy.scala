package htwg.minesweeperse.util.strategy.reveal.impl

import htwg.minesweeperse.util.strategy.reveal.api.IRevealStrategy
import htwg.minesweeperse.model.field.api.IField

class StandardRevealStrategy extends IRevealStrategy:
  override def reveal(field: IField, r: Int, c: Int): IField =
    
    if r < 0 || r >= field.rows || c < 0 || c >= field.cols then
      field
    else
      field.reveal(r, c)
