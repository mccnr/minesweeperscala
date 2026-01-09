package htwg.minesweeperse.util.strategy.revealComponent.impl

import com.google.inject.Inject
import htwg.minesweeperse.model.fieldComponent.impl.IField

class StandardRevealStrategy @Inject() extends IRevealStrategy {
  override def reveal(field: IField, r: Int, c: Int): IField =

    if r < 0 || r >= field.rows || c < 0 || c >= field.cols then
      field
    else
      field.reveal(r, c)
}