/* package htwg.minesweeperse

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
    gui.main(Array()) // Startet ScalaFX GUI */

package htwg.minesweeperse

import htwg.minesweeperse.view.GameView
import htwg.minesweeperse.view.GameGUI
import com.google.inject.Guice
import htwg.minesweeperse.controllerComponent.impl.IController

@main def runMain(): Unit =

    // Injector
    val injector = Guice.createInjector(new MinesweeperModule)
    val controller = injector.getInstance(classOf[IController])

    // Wenn DOCKER=true gesetzt ist → nur TUI starten
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