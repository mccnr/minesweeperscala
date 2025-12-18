package htwg.minesweeperse.util.factory.cellFactory

import htwg.minesweeperse.model.cell.api.ICell

trait ICellFactory:
  def create(value: Int): ICell

