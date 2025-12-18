package htwg.minesweeperse.util.factory.cellFactory

import htwg.minesweeperse.model.cell.api.ICell
import htwg.minesweeperse.model.cell.impl.implCell

class CellCreator extends ICellFactory:
  override def create(value: Int): ICell =
    implCell(value)

  // T
  def empty(): ICell = create(0)
  def mine(): ICell = create(1)
