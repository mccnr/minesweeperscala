package htwg.minesweeperse

import htwg.minesweeperse.controller.GameController
import htwg.minesweeperse.model.Field
import htwg.minesweeperse.view.GameView
//TTA
@main def runMain(): Unit =
    val field = Field.random(5, 5)
    val controller = new GameController(field)
    val view = new GameView(controller)
    view.start()