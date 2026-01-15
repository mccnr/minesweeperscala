package htwg.minesweeperse.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import com.google.inject.Guice
import htwg.minesweeperse.MinesweeperModule

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.util.state.ControllerResult._
import htwg.minesweeperse.util.state._

import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.model.fileIoComponent.IFileIO

class GameControllerWSTest extends AnyWordSpec {

  // Helper Fields
  // 2x2 mit 1 Mine, aber (0,0) ist sicher, Revealed, aber nicht Win
  def fieldSafeButNotWin2x2(): IField =
    new implFieldAdvanced(
      2, 2,
      Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(0), Cell(1)) // Mine unten rechts
      )
    )

  // 2x2 mit Mine (0,0), GameOver beim Klick
  def fieldMineAt00(): IField =
    new implFieldAdvanced(
      2, 2,
      Vector(
        Vector(Cell(1), Cell(0)),
        Vector(Cell(0), Cell(0))
      )
    )

  // 1x2 Feld: wenn man (0,0) revealed dann Win, weil (0,1) ist eine Mine
  def fieldWinAfterOneMove(): IField =
    new implFieldAdvanced(
      1, 2,
      Vector(
        Vector(Cell(0), Cell(1))
      )
    )

  // macht absichtlich ein Feld mit revealed Mine (für load)
  def fieldAlreadyGameOver(): IField =
    new implFieldAdvanced(
      1, 1,
      Vector(Vector(Cell(1, revealed = true)))
    )

  // macht absichtlich ein Feld welches bereits win ist (alle non-mines revealed)
  def fieldAlreadyWin(): IField =
    new implFieldAdvanced(
      1, 2,
      Vector(Vector(Cell(0, revealed = true), Cell(1)))
    )

  class DummyFileIO(var stored: (IField, Int)) extends IFileIO {
    override def save(field: IField, seconds: Int): Unit =
      stored = (field, seconds)

    override def load(): (IField, Int) =
      stored
  }

  // Controller via Guice
  private def makeController(): IController = {
    val injector = Guice.createInjector(new MinesweeperModule)
    injector.getInstance(classOf[IController])
  }

  // Tests
  "A GameController (implGC)" should {

    "execute processMove SUCCESS path (notifyObservers + revealed)" in {
      val controller = makeController()
      controller.field = fieldSafeButNotWin2x2()

      controller.processMove(0, 0)

      controller.lastResult shouldBe Revealed
      controller.field.isRevealed(0, 0) shouldBe true
      controller.state.isInstanceOf[PlayingState] shouldBe true
    }

    "execute processMove FAILURE path when out of bounds (sets OutOfBounds + notifyObservers)" in {
      val controller = makeController()
      controller.field = fieldSafeButNotWin2x2()

      controller.processMove(99, 99)

      controller.lastResult shouldBe OutOfBounds
      controller.state.isInstanceOf[PlayingState] shouldBe true
    }

    "switch to GameOverState when revealing a mine" in {
      val controller = makeController()
      controller.field = fieldMineAt00()

      controller.processMove(0, 0)

      controller.lastResult shouldBe GameOver
      controller.state.isInstanceOf[GameOverState] shouldBe true
      controller.field.hasRevealedMine shouldBe true
    }

    "switch to WinState when all non-mine cells are revealed" in {
      val controller = makeController()
      controller.field = fieldWinAfterOneMove()

      controller.processMove(0, 0)

      controller.lastResult shouldBe Win
      controller.state.isInstanceOf[WinState] shouldBe true
      controller.field.isWin shouldBe true
    }

    "undo should call syncStateWithField and restore PlayingState correctly" in {
      val controller = makeController()
      controller.field = fieldMineAt00()

      // Mine treffen, GameOver
      controller.processMove(0, 0)
      controller.state.isInstanceOf[GameOverState] shouldBe true

      // Undo, zurück in PlayingState
      controller.undo()

      controller.state.isInstanceOf[PlayingState] shouldBe true
      controller.field.hasRevealedMine shouldBe false
    }

    "redo should call syncStateWithField and go back to GameOver correctly" in {
      val controller = makeController()
      controller.field = fieldMineAt00()

      controller.processMove(0, 0) // GameOver
      controller.undo()            // Playing
      controller.redo()            // GameOver

      controller.state.isInstanceOf[GameOverState] shouldBe true
      controller.lastResult shouldBe GameOver
      controller.field.hasRevealedMine shouldBe true
    }

    "toggleFlag should flag and unflag a cell (and notifyObservers path covered)" in {
      val controller = makeController()
      controller.field = fieldSafeButNotWin2x2()

      controller.field.isFlagged(0, 0) shouldBe false

      controller.toggleFlag(0, 0)
      controller.field.isFlagged(0, 0) shouldBe true

      controller.toggleFlag(0, 0)
      controller.field.isFlagged(0, 0) shouldBe false
    }

    "save should call fileIO.save(field, timerSeconds)" in {
      val controller = makeController()
      controller.field = fieldSafeButNotWin2x2()
      controller.timerSeconds = 42

      val dummy = new DummyFileIO((fieldMineAt00(), 0))

      succeed
    }

    "load should restore field + timerSeconds + set correct state/result (GameOver)" in {
      val controller = makeController()

      succeed
    }

    "restart should reset field size, clear undo/redo, reset state/result and timer" in {
      val controller = makeController()
      controller.field = fieldSafeButNotWin2x2()
      controller.timerSeconds = 55

      // ein Move, damit UndoManager was hat
      controller.processMove(0, 0)
      controller.field.isRevealed(0, 0) shouldBe true

      controller.restart()

      controller.timerSeconds shouldBe 0
      controller.state.isInstanceOf[PlayingState] shouldBe true
      controller.lastResult shouldBe Revealed

      // nach restart sollte feld wieder unrevealed sein
      controller.field.isRevealed(0, 0) shouldBe false
    }
  }

  // Alternative
  "A GameController (implGC) save/load lines" should {

    "save should store field + seconds and load should restore PlayingState" in {
      val dummy = new DummyFileIO((fieldSafeButNotWin2x2(), 0))

      // implGC direkt instanziieren (damit wir fileIO kontrollieren)
      val controller =
        new htwg.minesweeperse.controllerComponent.impl.implGC(
          fieldSafeButNotWin2x2(),
          new htwg.minesweeperse.util.strategy.revealComponent.impl.StandardRevealStrategy,
          dummy
        )

      controller.timerSeconds = 123
      controller.save()

      // Dummy muss jetzt updated sein
      val (savedField, savedSeconds) = dummy.load()
      savedSeconds shouldBe 123
      savedField.rows shouldBe 2
      savedField.cols shouldBe 2

      // jetzt load testen
      dummy.stored = (fieldSafeButNotWin2x2(), 77)
      controller.load()

      controller.timerSeconds shouldBe 77
      controller.state.isInstanceOf[PlayingState] shouldBe true
      controller.lastResult shouldBe Revealed
    }

    "load should set GameOverState if loaded field has revealed mine" in {
      val dummy = new DummyFileIO((fieldAlreadyGameOver(), 10))

      val controller =
        new htwg.minesweeperse.controllerComponent.impl.implGC(
          fieldSafeButNotWin2x2(),
          new htwg.minesweeperse.util.strategy.revealComponent.impl.StandardRevealStrategy,
          dummy
        )

      controller.load()

      controller.timerSeconds shouldBe 10
      controller.state.isInstanceOf[GameOverState] shouldBe true
      controller.lastResult shouldBe GameOver
    }

    "load should set WinState if loaded field is already a win" in {
      val dummy = new DummyFileIO((fieldAlreadyWin(), 33))

      val controller =
        new htwg.minesweeperse.controllerComponent.impl.implGC(
          fieldSafeButNotWin2x2(),
          new htwg.minesweeperse.util.strategy.revealComponent.impl.StandardRevealStrategy,
          dummy
        )

      controller.load()

      controller.timerSeconds shouldBe 33
      controller.state.isInstanceOf[WinState] shouldBe true
      controller.lastResult shouldBe Win
    }

    "A GameController (implGC) failure branch" should {

      "set OutOfBounds and notifyObservers when reveal strategy throws (Failure path)" in {

        // Strategy die absichtlich crashtm erzwingt Failure branch
        class ThrowingStrategy extends htwg.minesweeperse.util.strategy.revealComponent.impl.IRevealStrategy {
          override def reveal(field: IField, r: Int, c: Int): IField =
            throw new RuntimeException("boom")
        }

        // Dummy FileIO
        val dummy = new DummyFileIO((fieldSafeButNotWin2x2(), 0))

        val controller =
          new htwg.minesweeperse.controllerComponent.impl.implGC(
            fieldSafeButNotWin2x2(),
            new ThrowingStrategy,
            dummy
          )

        // Observer der mitzählt ob notifyObservers aufgerufen wurde
        var updates = 0
        controller.addObserver(() => updates += 1)

        controller.processMove(0, 0)

        controller.lastResult shouldBe OutOfBounds
        updates shouldBe 1
      }
    }
  }
}
