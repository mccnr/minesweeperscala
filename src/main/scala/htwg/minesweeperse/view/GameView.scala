package htwg.minesweeperse.view

import htwg.minesweeperse.util.state.ControllerResult.{GameOver, OutOfBounds, Revealed, Win}
import htwg.minesweeperse.util.template.BaseView
import htwg.minesweeperse.controllerComponent
import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.util.command.{InputCommand, Move, RedoCmd, UndoCmd}
import htwg.minesweeperse.util.observer.Observer
import htwg.minesweeperse.util.state.ControllerResult
import com.google.inject.Inject

import java.io.*

class GameView (
   controller: IController,
   out: PrintStream = System.out,
   in: BufferedReader = new BufferedReader(new InputStreamReader(System.in))
   ) extends BaseView(controller), Observer:

  controller.addObserver(this)

  override def update(): Unit =
    out.println(controller.field.show())
    handleResult(controller.lastResult)

  override def showWelcome(): Unit =
    out.println("Willkommen bei Minesweeper")

  override def showField(): Unit =
    out.println(controller.field.show())

  override def readInput(): String =
    out.print("Gib eine valide Koordinate ein (Z S): ")
    Option(in.readLine())
      .map(_.trim) // trims non-null input
      .getOrElse("")

  override def parseInput(s: String): Option[InputCommand] =
    s.trim.toLowerCase match
      case "undo" => Some(UndoCmd)
      case "redo" => Some(RedoCmd)
      case str =>
        val parts = str.split(" ")
        if parts.length != 2 then None
        else
          for
            r <- parts(0).toIntOption
            c <- parts(1).toIntOption
          yield Move(r - 1, c - 1)

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