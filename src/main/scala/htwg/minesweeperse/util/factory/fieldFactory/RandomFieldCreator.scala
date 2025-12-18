package htwg.minesweeperse.util.factory.fieldFactory

import htwg.minesweeperse.model.field.api._
import htwg.minesweeperse.model.field.impl._
import htwg.minesweeperse.model.cell.api.ICell

class RandomFieldCreator extends IFieldFactory:
  override def create(rows: Int, cols: Int): IField =
    implFieldB.random(rows, cols)

  // T
  override def fromCells(cells: Vector[Vector[ICell]]): IField =
    implFieldA(cells.length, cells.head.length, cells)

