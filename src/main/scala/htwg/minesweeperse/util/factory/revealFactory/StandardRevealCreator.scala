package htwg.minesweeperse.util.factory.revealFactory

import htwg.minesweeperse.util.strategy.revealComponent.impl.{IRevealStrategy, NoFloodRevealStrategy, StandardRevealStrategy}

class StandardRevealCreator extends IRevealFactory:
  override def create(): IRevealStrategy =
    new StandardRevealStrategy