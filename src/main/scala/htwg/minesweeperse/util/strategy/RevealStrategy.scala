package htwg.minesweeperse.util.strategy

import htwg.minesweeperse.model.field.api.IField

trait RevealStrategy:
  def reveal(field: IField, r: Int, c: Int): IField