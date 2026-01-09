package htwg.minesweeperse

import htwg.minesweeperse.view.GameView
import htwg.minesweeperse.view.GameGUI
import com.google.inject.Guice
import htwg.minesweeperse.controllerComponent.impl.IController

@main def runMain(): Unit =

    // Injector
    val injector = Guice.createInjector(new MinesweeperModule)
    val controller = injector.getInstance(classOf[IController])

    // TUI starten
    val tui = new GameView(controller)
    val tuiThread = new Thread(() => tui.start()) // Thread, der tui.start() ausführt
    tuiThread.setDaemon(true) // Damit beim schließen der GUI, es parallel terminiert
    tuiThread.start()

    // GUI starten
    val gui = GameGUI(controller)
    gui.main(Array()) // Startet ScalaFX GUI