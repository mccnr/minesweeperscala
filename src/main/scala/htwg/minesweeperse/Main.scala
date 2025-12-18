package htwg.minesweeperse

import htwg.minesweeperse.util.factory.controllerFactory.ControllerCreator
import htwg.minesweeperse.view.GameView
import htwg.minesweeperse.view.GameGUI
import htwg.minesweeperse.util.strategy.reveal._
import htwg.minesweeperse.util.factory.revealFactory.StandardRevealCreator
import htwg.minesweeperse.util.factory.revealFactory.NoFloodRevealCreator
import htwg.minesweeperse.util.factory.fieldFactory.RandomFieldCreator

@main def runMain(): Unit =

    // Field Factory
    val fieldCreator = RandomFieldCreator()
    val field = fieldCreator.create(6,6)

    // Reveal Factory
    val revealCreator = StandardRevealCreator()
    val reveal = revealCreator.create()

    // Controller Factory
    val controllerFactory = ControllerCreator()
    val controller = controllerFactory.create(field, reveal)

    // TUI starten
    val tui = new GameView(controller)
    val tuiThread = new Thread(() => tui.start()) // Thread, der tui.start() ausführt
    tuiThread.setDaemon(true) // Damit beim schließen der GUI, es parallel terminiert
    tuiThread.start()

    // GUI starten
    val gui = GameGUI(controller)
    gui.main(Array()) // Startet ScalaFX GUI