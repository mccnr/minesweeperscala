package htwg.minesweeperse.view

import htwg.minesweeperse.controller.ControllerResult.{GameOver, OutOfBounds, Revealed, Win}
import htwg.minesweeperse.controller.GameController
import htwg.minesweeperse.util.Observer
import htwg.minesweeperse.util.template.BaseView
import htwg.minesweeperse.controller.ControllerResult

import java.io.*

class GameView(
                controller: GameController,
                out: PrintStream = System.out,
                in: BufferedReader = new BufferedReader(new InputStreamReader(System.in))
              ) extends BaseView(controller), Observer:

  controller.addObserver(this)

  override def update(): Unit =
    out.println(controller.field.show())

  override def showWelcome(): Unit =
    out.println("Willkommen bei Minesweeper")

  override def showField(): Unit =
    out.println(controller.field.show())

  override def readInput(): String =
    out.print("Gib eine valide Koordinate ein (Z S): ")
    val line = in.readLine()
    if line == null then "" else line.trim

  override def parseInput(s: String): Option[(Int, Int)] =
    val parts = s.trim.split(" ")
    if parts.length != 2 || !parts(0).matches("\\d+") || !parts(1).matches("\\d+") then
      None
    else
      Some((parts(0).toInt - 1, parts(1).toInt - 1))

  override def handleInvalidInput(s: String): Unit =
    out.println("Bitte zwei Zahlen eingeben, z. B. 2 3.")

  override def handleResult(result: ControllerResult): Unit =
    controller.lastResult match
      case Revealed =>
        out.println("Erfolgreich aufgedeckt.")
      case OutOfBounds =>
        out.println("Koordinate ist außerhalb des Felds.")
      case GameOver =>
        out.println("Game Over.")
      case Win =>
        out.println("Glückwunsch, du hast alle Minen gefunden!")
