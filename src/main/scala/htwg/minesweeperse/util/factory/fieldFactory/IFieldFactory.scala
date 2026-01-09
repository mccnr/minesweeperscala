package htwg.minesweeperse.util.factory.fieldFactory

import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.IField

trait IFieldFactory:
  def create(rows: Int, cols: Int): IField

  // T
  def fromCells(cells: Vector[Vector[Cell]]): IField