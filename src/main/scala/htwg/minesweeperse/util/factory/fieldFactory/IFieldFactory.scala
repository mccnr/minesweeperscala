package htwg.minesweeperse.util.factory.fieldFactory
import htwg.minesweeperse.model.cell.api.ICell
import htwg.minesweeperse.model.field.api.IField

trait IFieldFactory:
  def create(rows: Int, cols: Int): IField

  // T
  def fromCells(cells: Vector[Vector[ICell]]): IField