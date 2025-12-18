package htwg.minesweeperse.util.factory.controllerFactory

import htwg.minesweeperse.controller.api.IController
import htwg.minesweeperse.controller.impl.implGC
import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.util.strategy.reveal.api.IRevealStrategy

class ControllerCreator extends IControllerFactory:
  override def create(field: IField, reveal: IRevealStrategy): IController =
    new implGC(field, reveal)
