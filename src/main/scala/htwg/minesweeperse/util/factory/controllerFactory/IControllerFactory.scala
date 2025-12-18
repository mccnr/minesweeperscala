package htwg.minesweeperse.util.factory.controllerFactory

import htwg.minesweeperse.controller.api.IController
import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.util.strategy.reveal.api.IRevealStrategy

trait IControllerFactory:
  def create(field: IField, reveal: IRevealStrategy): IController
