package htwg.minesweeperse.util.strategy.revealComponent.impl

import com.google.inject.Inject
import htwg.minesweeperse.model.fieldComponent.impl.IField

class NoFloodRevealStrategy @Inject() extends IRevealStrategy {

   override def reveal(field: IField, r: Int, c: Int): IField =
     if r < 0 || r >= field.rows || c < 0 || c >= field.cols then
       field
     else if field.isMine(r, c) then
       field.revealAllMines()
     else
       field.revealOne(r, c)
  }


