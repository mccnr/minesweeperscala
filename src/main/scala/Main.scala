def runGame(): String =
  val cell1 = Cell(1)
  println(s"cell1 ist eine Mine? ${cell1.isMine}")

  val cell2 = Cell(0)
  println(s"cell2 ist eine Mine? ${cell2.isMine}")

  val field = randomField(5, 5)
  s"Minesweeper\n${cell1.isMine}\n${cell2.isMine}\n${field.show()}"

// main
@main def runMain(): Unit =
  println(runGame())