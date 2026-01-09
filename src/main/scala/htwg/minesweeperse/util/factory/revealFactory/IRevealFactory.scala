package htwg.minesweeperse.util.factory.revealFactory

import htwg.minesweeperse.util.strategy.revealComponent.impl.IRevealStrategy

trait IRevealFactory:
  def create(): IRevealStrategy
