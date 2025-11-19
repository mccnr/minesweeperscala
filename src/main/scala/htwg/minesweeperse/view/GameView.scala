package htwg.minesweeperse.view

import htwg.minesweeperse.controller.ControllerResult.{GameOver, OutOfBounds, Revealed, Win}
import htwg.minesweeperse.controller.GameController
import htwg.minesweeperse.util.Observer

import java.io.*

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

    //val interactive = !sys.props.contains("test.env")

    while controller.playing do //&& interactive do
      out.print("Gib eine valide Koordinate ein (Z S): ")
      val line = in.readLine()

      if line == null || line.trim.isEmpty then
        controller.playing = false
      else
        val parts = line.split(" ")
        if parts.length != 2 || !parts(0).matches("\\d+") || !parts(1).matches("\\d+") then
          out.println("Bitte zwei Zahlen eingeben, z. B. 2 3.")
        else
          val r = parts(0).toInt - 1
          val c = parts(1).toInt - 1

          controller.processMove(r, c) match
            case Revealed =>
             out.println("Erfolgreich aufgedeckt.")

            case OutOfBounds =>
              out.println("Koordinate ist außerhalb des Felds.")

            case GameOver =>
              out.println("Game Over.")

            case Win =>
              out.println("Glückwunsch, du hast alle Minen gefunden!")