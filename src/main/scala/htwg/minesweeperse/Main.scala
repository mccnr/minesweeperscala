package htwg.minesweeperse

import htwg.minesweeperse.controller.GameController
import htwg.minesweeperse.model.Field
import htwg.minesweeperse.view.GameView
import htwg.minesweeperse.util.strategy._

@main def runMain(): Unit =
    val field = Field.random(5, 5)
    val controller = new GameController(field, StandardRevealStrategy())
    val view = new GameView(controller)
    view.startGameLoop()