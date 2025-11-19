package htwg.minesweeperse.model

import scala.util.Random

case class Cell(value: Int, revealed: Boolean = false):
  def isMine: Boolean = value == 1
  def display(minesAround: Option[Int] = None): String =
    if !revealed then "?"
    else if isMine then "*"
    else if minesAround.contains(0) then " "
    else minesAround.map(_.toString).getOrElse("?")