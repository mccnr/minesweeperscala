package htwg.minesweeperse.util.factory.revealFactory
import htwg.minesweeperse.util.strategy.reveal.api.IRevealStrategy

trait IRevealFactory:
  def create(): IRevealStrategy
