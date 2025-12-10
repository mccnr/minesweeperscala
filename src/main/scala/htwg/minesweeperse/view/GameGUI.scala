package htwg.minesweeperse.view

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{VBox, HBox, GridPane}
import scalafx.scene.control.Button
import scalafx.geometry.Insets
import scalafx.application.Platform
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.SceneIncludes.jfxScene2sfx

import htwg.minesweeperse.controller.*
import htwg.minesweeperse.util.Observer
import htwg.minesweeperse.util.state.*

class GameGUI(controller: GameController) extends JFXApp3 with Observer:

  controller.addObserver(this)

  private var fieldButtons: Vector[Vector[Button]] = Vector()

  override def start(): Unit =
    stage = new PrimaryStage:
      title = "Minesweeper"
      scene = new Scene:
        root = new VBox:
          spacing = 10
          padding = Insets(10)
          children = Seq(
            buildToolbar(),
            buildGrid()
          )

  // Toolbar mit undo/redo Buttons
  private def buildToolbar() =
    new HBox:
      spacing = 10
      padding = Insets(5)

      val undoBtn = new Button("Undo"):
        minWidth = 80
        onAction = _ => controller.undo()

      val redoBtn = new Button("Redo"):
        minWidth = 80
        onAction = _ => controller.redo()

      children = Seq(undoBtn, redoBtn)

  // Baut Spielfeld neu
  private def buildGrid(): GridPane =
    val gp = new GridPane:
      padding = Insets(10)
      hgap = 5
      vgap = 5

    fieldButtons =
      controller.field.cells.zipWithIndex.map { (row, r) =>
        row.zipWithIndex.map { (cell, c) =>
          val btn = new Button:
            minWidth = 40
            minHeight = 40
            text = cellDisplay(r, c)

            onAction = _ =>
              controller.state match
                case _: GameOverState | _: WinState =>
                  () // blockiert wie TUI
                case _ =>
                  controller.processMove(r, c)

          gp.add(btn, c, r)
          btn
        }.toVector
      }.toVector

    gp

  // GUI Anzeige vom Feld
  private def cellDisplay(r: Int, c: Int): String =
    val cell = controller.field.cells(r)(c)

    if !cell.revealed then "?"
    else if cell.isMine then "*"
    else
      val count = controller.field.countMinesAround(r, c)
      if count == 0 then " " else count.toString

  // Observer update, wird vom Controller aufgerufen
  override def update(): Unit =
    Platform.runLater {

      // Wenn sich die Feldgröße geändert hat, layout neu bauen
      if fieldButtons.isEmpty ||
        fieldButtons.length != controller.field.rows ||
        fieldButtons.head.length != controller.field.cols then

        stage.scene().root = new VBox:
          spacing = 10
          padding = Insets(10)
          children = Seq(
            buildToolbar(),
            buildGrid()
          )
        return
      end if

      // Sonst: Buttons updaten
      for r <- controller.field.cells.indices do
        for c <- controller.field.cells(r).indices do
          val btn = fieldButtons(r)(c)
          btn.text = cellDisplay(r, c)
    }