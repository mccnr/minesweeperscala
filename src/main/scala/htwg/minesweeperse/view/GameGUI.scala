package htwg.minesweeperse.view

import htwg.minesweeperse.controllerComponent.impl.IController
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.control.Button
import scalafx.geometry.Insets
import scalafx.application.Platform
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.scene.image.{Image, ImageView}
import htwg.minesweeperse.util.observer.Observer
import htwg.minesweeperse.util.state._
import scalafx.scene.input.MouseButton
import scalafx.scene.input.InputIncludes.jfxMouseEvent2sfx
import scalafx.scene.control.Label

class GameGUI(controller: IController) extends JFXApp3 with Observer:

  controller.addObserver(this)

  private lazy val mineCounterLabel = new Label("Mines: 0")
  private var fieldButtons: Vector[Vector[Button]] = Vector()

  // Styles
  private val windowStyle =
    "-fx-background-color: #c0c0c0;"

  private val toolbarStyle =
    "-fx-background-color: #bdbdbd;" +
      "-fx-border-color: #808080;" +
      "-fx-border-width: 2;" +
      "-fx-padding: 6;"

  // Hidden tile
  private val tileHiddenStyle =
    "-fx-background-color: #c0c0c0;" +
      "-fx-border-color: #ffffff #808080 #808080 #ffffff;" + // top right bottom left
      "-fx-border-width: 2;" +
      "-fx-font-size: 18px;" +
      "-fx-font-weight: bold;" +
      "-fx-padding: 0;" +
      "-fx-background-insets: 0;" +
      "-fx-border-insets: 0;"

  // Revealed tile = flat
  private val tileRevealedStyle =
    "-fx-background-color: #d6d6d6;" +
      "-fx-border-color: #a0a0a0;" +
      "-fx-border-width: 1;" +
      "-fx-font-size: 18px;" +
      "-fx-font-weight: bold;" +
      "-fx-padding: 0;" +
      "-fx-background-insets: 0;" +
      "-fx-border-insets: 0;"

  private val gridPanelStyle =
    "-fx-background-color: #bdbdbd;" +
      "-fx-border-color: #808080;" +
      "-fx-border-width: 3;" +
      "-fx-padding: 6;"

  override def start(): Unit =
    stage = new PrimaryStage:
      title = "Minesweeper"
      scene = new Scene:
        root = new VBox:
          spacing = 8
          padding = Insets(10)
          style = windowStyle
          children = Seq(
            buildToolbar(),
            buildGrid()
          )
          refreshMineCounter()

  // Toolbar
  private def buildToolbar() =
    new HBox:
      spacing = 8
      padding = Insets(5)
      style = toolbarStyle

      val undoBtn = new Button("Undo"):
        minWidth = 80
        onAction = _ => controller.undo()

      val redoBtn = new Button("Redo"):
        minWidth = 80
        onAction = _ => controller.redo()

      val saveBtn = new Button("Save"):
        minWidth = 80
        onAction = _ => controller.save()

      val loadBtn = new Button("Load"):
        minWidth = 80
        onAction = _ => controller.load()

      val minesLeftLabel = mineCounterLabel
      minesLeftLabel.style = "-fx-font-size: 18px; -fx-font-weight: bold;"

      children = Seq(undoBtn, redoBtn, saveBtn, loadBtn, minesLeftLabel)

  // Field
  private def buildGrid(): GridPane =
    val gp = new GridPane:
      padding = Insets(5)
      hgap = 0
      vgap = 0
      style = gridPanelStyle

    fieldButtons =
      (0 until controller.field.rows).map { r =>
        (0 until controller.field.cols).map { c =>
          val btn = new Button:
            minWidth = 32
            minHeight = 32

            // ✅ initial style
            style = if controller.field.isRevealed(r, c) then tileRevealedStyle else tileHiddenStyle

            graphic = cellGraphic(r, c)
            text = ""

            onMouseClicked = e =>
              controller.state match
                case _: GameOverState | _: WinState => ()
                case _ =>
                  if e.button == MouseButton.Secondary then
                    controller.toggleFlag(r, c) // RIGHT CLICK = FLAG
                  else if e.button == MouseButton.Primary then
                    controller.processMove(r, c) // LEFT CLICK = REVEAL

          gp.add(btn, c, r)
          btn
        }.toVector
      }.toVector

    gp

  // Cell Graphic
  private def cellGraphic(r: Int, c: Int): ImageView =

    val field = controller.field

    val img =
      if field.isFlagged(r, c) && !field.isRevealed(r, c) then
        new Image("icons/flag.png")
      else if !field.isRevealed(r, c) then
        new Image("icons/hidden.png")
      else if field.isMine(r, c) then
        new Image("icons/mine.png")
      else
        val count = field.countMinesAround(r, c)
        new Image(s"icons/n$count.png")

    new ImageView(img) {
      fitWidth = 28
      fitHeight = 28
      preserveRatio = true
    }

  private def refreshMineCounter(): Unit =
   val minesLeft = controller.field.totalMines - controller.field.totalFlags
   mineCounterLabel.text = s"Mines: $minesLeft"

  // Observer Update
  override def update(): Unit =
    Platform.runLater {
      refreshMineCounter()

      val minesLeft =
        controller.field.totalMines - controller.field.totalFlags

      mineCounterLabel.text = s"Mines: $minesLeft"

      if fieldButtons.isEmpty ||
        fieldButtons.length != controller.field.rows ||
        fieldButtons.head.length != controller.field.cols
      then
        stage.scene().root = new VBox:
          spacing = 8
          padding = Insets(10)
          style = windowStyle
          children = Seq(
            buildToolbar(),
            buildGrid()
          )
      else
        for r <- 0 until controller.field.rows do
          for c <- 0 until controller.field.cols do
            val btn = fieldButtons(r)(c)

            btn.style =
              if controller.field.isRevealed(r, c) then tileRevealedStyle
              else tileHiddenStyle

            btn.graphic = cellGraphic(r, c)
            btn.text = ""
    }