package htwg.minesweeperse.util.command

import htwg.minesweeperse.controller.GameController
import htwg.minesweeperse.model.Field
import htwg.minesweeperse.controller.ControllerResult._
import htwg.minesweeperse.util.state._

class RevealCommand(controller: GameController, r: Int, c: Int) extends Command:

  private val oldField: Field = controller.field
  private var newField: Field = oldField

  override def doStep(): Unit =
    newField = controller.revealStrategy.reveal(oldField, r, c)
    controller.field = newField

  override def undoStep(): Unit =
    controller.field = oldField
    controller.lastResult = Revealed
    controller.state = PlayingState() // state wird geprüft
    //controller.playing = true
    PlayingState().playing = true

  override def redoStep(): Unit =
    controller.field = newField
    controller.processMove(r, c)
    //controller.playing = true
    PlayingState().playing = true