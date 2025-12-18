package htwg.minesweeperse.util.strategy.reveal.api

import htwg.minesweeperse.model.field.api.IField

trait IRevealStrategy:
  def reveal(field: IField, r: Int, c: Int): IField
