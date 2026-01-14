package htwg.minesweeperse.model.cell

/* case class Cell(value: Int, revealed: Boolean = false, flagged: Boolean = false) {

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
} */

case class Cell(
                 value: Int,
                 revealed: Boolean = false,
                 flagged: Boolean = false
               ) {
  def isMine: Boolean = value == 1

  def isRevealed: Boolean = revealed
  def isFlagged: Boolean = flagged

  def reveal(): Cell =
    if flagged then this else copy(revealed = true)

  def toggleFlag(): Cell =
    if revealed then this else copy(flagged = !flagged)

  def display(minesAround: Option[Int] = None): String =
    if !revealed then
      if flagged then "F" else "?"
    else if isMine then "*"
    else if minesAround.contains(0) then " "
    else minesAround.map(_.toString).getOrElse("?")
}

