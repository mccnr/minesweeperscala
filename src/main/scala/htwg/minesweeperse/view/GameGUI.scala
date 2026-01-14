package htwg.minesweeperse.view

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.util.observer.Observer
import htwg.minesweeperse.util.state._

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseButton}
import scalafx.scene.input.InputIncludes.jfxMouseEvent2sfx
import scalafx.scene.layout.{GridPane, HBox, VBox}

import scalafx.animation.{KeyFrame, Timeline}
import scalafx.util.Duration

class GameGUI(controller: IController) extends JFXApp3 with Observer:

  controller.addObserver(this)

  // UI state
  private var fieldButtons: Vector[Vector[Button]] = Vector()

  // Labels
  private lazy val mineCounterLabel = new Label("")
  private lazy val timerLabel = new Label("")

  // Timer State
  private var secondsPassed: Int = 0
  private lazy val timer: Timeline = new Timeline {
    cycleCount = Timeline.Indefinite
    keyFrames = Seq(
      KeyFrame(Duration(1000), onFinished = _ => {
        secondsPassed += 1
        controller.timerSeconds = secondsPassed
        timerLabel.text = s"Time: $secondsPassed"
      })
    )
  }

  // Styles
  private val windowStyle =
    "-fx-background-color: #c0c0c0;"

  private val toolbarStyle =
    "-fx-background-color: #bdbdbd;" +
      "-fx-border-color: #808080;" +
      "-fx-border-width: 2;" +
      "-fx-padding: 6;"

  private val tileHiddenStyle =
    "-fx-background-color: #c0c0c0;" +
      "-fx-border-color: #ffffff #808080 #808080 #ffffff;" +
      "-fx-border-width: 2;" +
      "-fx-font-size: 18px;" +
      "-fx-font-weight: bold;" +
      "-fx-padding: 0;" +
      "-fx-background-insets: 0;" +
      "-fx-border-insets: 0;"

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

  // Start
  override def start(): Unit =
    // init timer state from controller
    secondsPassed = controller.timerSeconds
    timerLabel.text = s"Time: $secondsPassed"

    refreshMineCounter()

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

    // start timer after Stage is built
    timer.play()

  // Toolbar
  private def buildToolbar(): HBox =
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
        onAction = _ =>
          controller.load()

          // sync timer after load
          secondsPassed = controller.timerSeconds
          timerLabel.text = s"Time: $secondsPassed"

          // update mines label immediately
          refreshMineCounter()

      mineCounterLabel.style = "-fx-font-size: 18px; -fx-font-weight: bold;"
      timerLabel.style = "-fx-font-size: 18px; -fx-font-weight: bold;"

      children = Seq(undoBtn, redoBtn, saveBtn, loadBtn, mineCounterLabel, timerLabel)

  // Grid
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

            style =
              if controller.field.isRevealed(r, c) then tileRevealedStyle
              else tileHiddenStyle

            graphic = cellGraphic(r, c)
            text = ""

            onMouseClicked = e =>
              controller.state match
                case _: GameOverState | _: WinState => ()
                case _ =>
                  if e.button == MouseButton.Secondary then
                    controller.toggleFlag(r, c)
                  else if e.button == MouseButton.Primary then
                    controller.processMove(r, c)

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

  // Mine Counter
  private def refreshMineCounter(): Unit =
    val minesLeft = controller.field.totalMines - controller.field.totalFlags
    mineCounterLabel.text = s"Mines: $minesLeft"

  // Observer Update
  override def update(): Unit =
    Platform.runLater {

      controller.state match
        case _: GameOverState | _: WinState =>
          timer.stop()
        case _ =>
          timer.play()

      refreshMineCounter()

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

