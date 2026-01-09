package htwg.minesweeperse.util.strategy.revealComponent.impl

import htwg.minesweeperse.model.fieldComponent.impl.IField

trait IRevealStrategy:
  def reveal(field: IField, r: Int, c: Int): IField
