package htwg.minesweeperse.util.factory.cellFactory

import htwg.minesweeperse.model.cell.Cell

trait ICellFactory:
  def create(value: Int): Cell

