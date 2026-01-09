package htwg.minesweeperse.util.factory.controllerFactory

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.model.fieldComponent.impl.IField
import htwg.minesweeperse.util.strategy.revealComponent.impl.IRevealStrategy

trait IControllerFactory:
  def create(field: IField, reveal: IRevealStrategy): IController
