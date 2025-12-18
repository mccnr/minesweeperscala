package htwg.minesweeperse.model.cell.impl
import htwg.minesweeperse.model.cell.api.ICell

case class implCell(value: Int, revealed: Boolean = false) extends ICell:
  override def isMine: Boolean = value == 1
  override def isRevealed: Boolean = revealed

  override def display(minesAround: Option[Int] = None): String =
    if !revealed then "?"
    else if isMine then "*"
    else if minesAround.contains(0) then " "
    else minesAround.map(_.toString).getOrElse("?")

  override def reveal(): ICell =
    copy(revealed = true)
