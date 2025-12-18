package htwg.minesweeperse.util.factory.revealFactory

import htwg.minesweeperse.util.strategy.reveal.api.IRevealStrategy
import htwg.minesweeperse.util.strategy.reveal.impl.{NoFloodRevealStrategy, StandardRevealStrategy}

class StandardRevealCreator extends IRevealFactory:
  override def create(): IRevealStrategy =
    new StandardRevealStrategy