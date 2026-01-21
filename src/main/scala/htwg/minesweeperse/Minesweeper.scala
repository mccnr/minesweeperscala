package htwg.minesweeperse

import htwg.minesweeperse.view.GameView
import htwg.minesweeperse.view.GameGUI
import com.google.inject.Guice
import htwg.minesweeperse.controllerComponent.impl.IController

@main def runMain(): Unit =

    // Injector
    val injector = Guice.createInjector(new MinesweeperModule)
    val controller = injector.getInstance(classOf[IController])

    val isDocker = sys.env.get("DOCKER").contains("true")

    if isDocker then
        // nur TUI (Docker)
        val tui = new GameView(controller)
        tui.start()
    else
        // TUI + GUI parallel
        val tui = new GameView(controller)
        val tuiThread = new Thread(() => tui.start())
        tuiThread.setDaemon(true)
        tuiThread.start()

        val gui = GameGUI(controller)
        gui.main(Array())