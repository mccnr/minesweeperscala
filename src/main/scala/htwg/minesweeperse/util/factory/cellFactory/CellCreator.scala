package htwg.minesweeperse.util.factory.cellFactory

import htwg.minesweeperse.model.cell.Cell

class CellCreator extends ICellFactory:
  override def create(value: Int): Cell =
    Cell(value)

  // T
  def empty(): Cell = create(0)
  def mine(): Cell = create(1)
