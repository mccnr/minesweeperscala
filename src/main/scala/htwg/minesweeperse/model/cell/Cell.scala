package htwg.minesweeperse.model.cell

case class Cell(value: Int, revealed: Boolean = false) {

  def isMine: Boolean =
    value == 1

  def isRevealed: Boolean =
    revealed

  def display(minesAround: Option[Int] = None): String =
    if !revealed then "?"
    else if isMine then "*"
    else if minesAround.contains(0) then " "
    else minesAround.map(_.toString).getOrElse("?")

  def reveal(): Cell =
    copy(revealed = true)
}
