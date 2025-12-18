package htwg.minesweeperse.model.field.impl

import htwg.minesweeperse.model.cell.impl.implCell
import scala.util.Random

object implFieldB:

  def random(rows: Int, cols: Int, mineChance: Double = 0.2): implFieldA =
    val generated =
      Vector.tabulate(rows, cols) { (_, _) =>
        if Random.nextDouble() < mineChance then
          implCell(1)
        else
          implCell(0)
      }

    implFieldA(rows, cols, generated)

