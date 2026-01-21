import scala.util.Random

case class Cell(value: Int):
  def isMine: Boolean = value == 1
  def display: String = if isMine then "*" else "."

case class Field(rows: Int, cols: Int, cells: Vector[Vector[Cell]]):
  def show(): String =
    val border = "-" * (cols * 2 + 3)
    val body = cells.map(row => "| " + row.map(_.display).mkString(" ") + " |").mkString("\n")
    s"$border\n$body\n$border"

def randomField(rows: Int, cols: Int): Field =
  val generated = Vector.fill(rows, cols)(Cell(Random.nextInt(2)))
  Field(rows, cols, generated)

val cell1 = Cell(1)
cell1.isMine

cell1.value

val cell2 = Cell(0)
cell2.isMine

cell2.value

val field = randomField(4, 4)
println(field.show())