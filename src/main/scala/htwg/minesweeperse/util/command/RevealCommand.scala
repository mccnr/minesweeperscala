/* package htwg.minesweeperse.util.command

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.model.fieldComponent.impl.IField
import htwg.minesweeperse.util.state.ControllerResult._
import htwg.minesweeperse.util.state._

class RevealCommand(controller: IController, r: Int, c: Int) extends Command:

  private val oldField: IField = controller.field
  private var newField: IField = oldField

  override def doStep(): Unit =
    newField = controller.revealStrategy.reveal(oldField, r, c)
    controller.field = newField

  override def undoStep(): Unit =
    controller.field = oldField
    controller.lastResult = Revealed
    controller.state = PlayingState() // state wird geprüft
    PlayingState().playing = true

  override def redoStep(): Unit =
    controller.field = newField
    controller.processMove(r, c)
    //controller.playing = true
    PlayingState().playing = true */

package htwg.minesweeperse.util.command

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.model.fieldComponent.impl.IField
import htwg.minesweeperse.util.state._

class RevealCommand(controller: IController, r: Int, c: Int) extends Command:

  private var before: IField = controller.field
  private var after: IField  = controller.field

  override def doStep(): Unit =
    before = controller.field
    after  = controller.revealStrategy.reveal(before, r, c)
    controller.field = after

  override def undoStep(): Unit =
    controller.field = before

  override def redoStep(): Unit =
    controller.field = after
