package htwg.minesweeperse.view

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.control.Button
import scalafx.geometry.Insets
import scalafx.application.Platform
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.scene.image.{Image, ImageView}
import htwg.minesweeperse.controller.api.IController
import htwg.minesweeperse.util.observer.Observer
import htwg.minesweeperse.util.state._

class GameGUI(controller: IController) extends JFXApp3 with Observer:

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

  // Baut Spielfeld
  private def buildGrid(): GridPane =
    val gp = new GridPane:
      padding = Insets(10)
      hgap = 5
      vgap = 5

    /* fieldButtons =
      controller.field.cells.zipWithIndex.map { (row, r) => // Für jede Zeile wird eine neue Zeile von Buttons erzeugt
        row.zipWithIndex.map { (cell, c) => // Spaltennr
          val btn = new Button:
            minWidth = 40
            minHeight = 40

            style = "-fx-padding: 0; -fx-background-insets: 0; -fx-background-radius: 0;"

            graphic = cellGraphic(r, c)
            text = ""

            onMouseEntered = _ => style = "-fx-background-color: lightgray;"
            onMouseExited = _ => style = "-fx-background-color: transparent;"

            onAction = _ =>
              controller.state match
                case _: GameOverState | _: WinState =>
                  () // blockiert wie TUI
                case _ =>
                  controller.processMove(r, c)

          gp.add(btn, c, r) // Button aufs Grid setzen, richtigen pos einfügen
          btn
        }
      } */

    fieldButtons =
      (0 until controller.field.rows).map { r =>
        (0 until controller.field.cols).map { c =>
          val btn = new Button:
            minWidth = 40
            minHeight = 40

            style = "-fx-padding: 0; -fx-background-insets: 0; -fx-background-radius: 0;"

            graphic = cellGraphic(r, c)
            text = ""

            onMouseEntered = _ => style = "-fx-background-color: lightgray;"
            onMouseExited = _ => style = "-fx-background-color: transparent;"

            onAction = _ =>
              controller.state match
                case _: GameOverState | _: WinState => ()
                case _ => controller.processMove(r, c)

          gp.add(btn, c, r)
          btn
        }.toVector
      }.toVector

    gp

  private def cellGraphic(r: Int, c: Int): ImageView =
    //val cell = controller.field.cells(r)(c)
    val field = controller.field

    val img =
      if !field.isRevealed(r, c) then // if !cell.revealed then
        new Image("icons/hidden.png")
      else if field.isMine(r, c) then // if cell.isMine then
        new Image("icons/mine.png")
      else
        val count = field.countMinesAround(r, c)   // val count = controller.field.countMinesAround(r, c)
        new Image(s"icons/n$count.png")

    new ImageView(img) {
      fitWidth = 40
      fitHeight = 40
      preserveRatio = true
    }

  // Observer update, wird vom Controller aufgerufen
  override def update(): Unit =
    Platform.runLater {

      // Wenn sich die Feldgröße geändert hat, layout neu bauen
      if fieldButtons.isEmpty ||
        fieldButtons.length != controller.field.rows ||
        fieldButtons.head.length != controller.field.cols
      then
        stage.scene().root = new VBox:
          spacing = 10
          padding = Insets(10)
          children = Seq(
            buildToolbar(),
            buildGrid()
          )
      else // Aktualisieren
        for r <- 0 until controller.field.rows do
          for c <- 0 until controller.field.cols do
            val btn = fieldButtons(r)(c)
            btn.graphic = cellGraphic(r, c)
            btn.text = ""
    }