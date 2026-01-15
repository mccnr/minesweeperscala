package htwg.minesweeperse.util.command

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.model.fieldComponent.impl.IField

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
