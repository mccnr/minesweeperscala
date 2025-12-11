package htwg.minesweeperse

import htwg.minesweeperse.controller.GameController
import htwg.minesweeperse.model.Field
import htwg.minesweeperse.view.GameView
import htwg.minesweeperse.util.strategy._
import htwg.minesweeperse.view.GameGUI
import scalafx.application.JFXApp3
import scalafx.application.Platform

@main def runMain(): Unit =

    val field = Field.random(5, 5)
    val controller = new GameController(field, StandardRevealStrategy())

    // TUI starten
    val tui = new GameView(controller)
    val tuiThread = new Thread(() => tui.start())
    tuiThread.setDaemon(true) // Damit beim schließen der GUI, es parallel terminiert.
    tuiThread.start()

    // GUI starten
    val gui = GameGUI(controller)
    gui.main(Array()) // Startet ScalaFX GUI