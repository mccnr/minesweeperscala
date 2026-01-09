/* package htwg.minesweeperse.util.factory.fieldFactory

import htwg.minesweeperse.model.cell.impl.ICell
import htwg.minesweeperse.model.fieldComponent.impl._
import htwg.minesweeperse.model.fieldComponent.impl._
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldBase, implFieldAdvanced}

class RandomFieldCreator extends IFieldFactory:
  override def create(rows: Int, cols: Int): IField =
    implFieldAdvanced.random(rows, cols)

  // T
  override def fromCells(cells: Vector[Vector[ICell]]): IField =
    implFieldBase(cells.length, cells.head.length, cells) */