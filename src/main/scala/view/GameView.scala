package view

import controller.GameController
import util.Observer
import java.io._

class GameView(
  controller: GameController,
  out: PrintStream = System.out,
  in: BufferedReader = new BufferedReader(new InputStreamReader(System.in))
  ) extends Observer:

  controller.addObserver(this)

  def update(): Unit =
    out.println(controller.field.show())

  def start(): Unit =
    out.println("Willkommen bei Minesweeper")
    out.println(controller.field.show())

    // Nur interaktiv, wenn nicht im Testmodus
    val interactive = !sys.props.contains("test.env")

    while controller.playing && interactive do
      out.print("Gib eine valide Koordinate ein (Z S): ")
      val line = in.readLine()

      if line == null || line.trim.isEmpty then
        controller.playing = false
      else
        val parts = line.split(" ")
        if parts.length == 2 && parts(0).matches("\\d+") && parts(1).matches("\\d+") then
          controller.processMove(parts(0).toInt - 1, parts(1).toInt - 1)
        else
          out.println("Bitte zwei Zahlen eingeben, z. B. 2 3.")
