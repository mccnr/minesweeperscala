case class Cell(value: Int):
  def isMine: Boolean = value == 1
  def display: String = if isMine then "*" else "."

case class Field(rows: Int, cols: Int, cells: Vector[Vector[Cell]]):
  def show(): String =
    val border = "-" * (cols * 2 + 3)
    val body = cells.map(row => "| " + row.map(_.display).mkString(" ") + " |").mkString("\n")
    s"$border\n$body\n$border"

import scala.util.Random

def randomField(rows: Int, cols: Int): Field =
  val generated = Vector.fill(rows, cols)(Cell(Random.nextInt(2)))
  Field(rows, cols, generated)

// main

@main def Hello(): Unit =
  println("Minesweeper")

  val cell1 = Cell(1)
  println(s"cell1 ist eine Mine? ${cell1.isMine}")

  val cell2 = Cell(0)
  println(s"cell2 ist eine Mine? ${cell2.isMine}")

  val field = randomField(8, 8)
  println(field.show())
