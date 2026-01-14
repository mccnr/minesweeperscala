package htwg.minesweeperse.view

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.util.observer.Observer
import htwg.minesweeperse.util.state._

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseButton
import scalafx.scene.input.InputIncludes.jfxMouseEvent2sfx
import scalafx.scene.layout.{GridPane, HBox, VBox}

import scalafx.animation.{KeyFrame, Timeline}
import scalafx.util.Duration
import scalafx.scene.text.Font

class GameGUI(controller: IController) extends JFXApp3 with Observer:

  controller.addObserver(this)

  // Retro Font
  private val retroFontName: String =
    try
      val stream = getClass.getResourceAsStream("/fonts/retro.ttf")
      if stream == null then "Monospaced"
      else
        val loaded = Font.loadFont(stream, 16)
        if loaded == null then "Monospaced" else loaded.getName
    catch case _: Exception =>
      "Monospaced"

  // UI
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
        refreshTimerLabel()
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

  private val gridPanelStyle =
    "-fx-background-color: #bdbdbd;" +
      "-fx-border-color: #808080;" +
      "-fx-border-width: 3;" +
      "-fx-padding: 6;"

  // Hidden tile
  private val tileHiddenStyle =
    "-fx-background-color: #c0c0c0;" +
      "-fx-border-color: #ffffff #808080 #808080 #ffffff;" +
      "-fx-border-width: 2;" +
      "-fx-padding: 0;" +
      "-fx-background-insets: 0;" +
      "-fx-border-insets: 0;"

  // Revealed tile
  private val tileRevealedStyle =
    "-fx-background-color: #d6d6d6;" +
      "-fx-border-color: #a0a0a0;" +
      "-fx-border-width: 1;" +
      "-fx-padding: 0;" +
      "-fx-background-insets: 0;" +
      "-fx-border-insets: 0;"

  // Game button style
  private def gameButtonStyle =
    s"-fx-background-color: #c0c0c0;" +
      "-fx-border-color: #ffffff #808080 #808080 #ffffff;" +
      "-fx-border-width: 2;" +
      s"-fx-font-family: '$retroFontName';" +
      "-fx-font-size: 14px;" +
      "-fx-font-weight: bold;" +
      "-fx-padding: 6 12 6 12;"

  private def gameButtonPressedStyle =
    s"-fx-background-color: #c0c0c0;" +
      "-fx-border-color: #808080 #ffffff #ffffff #808080;" +
      "-fx-border-width: 2;" +
      s"-fx-font-family: '$retroFontName';" +
      "-fx-font-size: 14px;" +
      "-fx-font-weight: bold;" +
      "-fx-padding: 7 11 5 13;"

  private def styleAsGameButton(btn: Button): Unit =
    btn.style = gameButtonStyle
    btn.font = Font.font(retroFontName, 14)

    btn.onMousePressed = _ => btn.style = gameButtonPressedStyle
    btn.onMouseReleased = _ => btn.style = gameButtonStyle

  private def styleAsRetroLabel(lbl: Label): Unit =
    lbl.font = Font.font(retroFontName, 16)
    lbl.style = "-fx-font-weight: bold;"

  // Mine Counter + Timer Counter UI
  private def refreshMineCounter(): Unit =
    val minesLeft = controller.field.totalMines - controller.field.totalFlags
    mineCounterLabel.text = s"Mines: $minesLeft"

  private def refreshTimerLabel(): Unit =
    timerLabel.text = s"Time: ${controller.timerSeconds}"

  // Start
  override def start(): Unit =
    secondsPassed = controller.timerSeconds
    refreshMineCounter()
    refreshTimerLabel()

    stage = new PrimaryStage:
      title = "Minesweeper in Scala"
      scene = new Scene:
        root = buildRoot()

    timer.play()

  private def buildRoot(): VBox =
    new VBox:
      spacing = 10
      padding = Insets(10)
      style = windowStyle
      alignment = Pos.TopCenter
      children = Seq(
        buildTopPanel(),
        buildGrid(),
        buildBottomPanel()
      )

  // Top Panel
  private def buildTopPanel(): VBox =
    new VBox:
      spacing = 6
      alignment = Pos.Center

      val row1 = new HBox:
        spacing = 8
        alignment = Pos.Center
        style = toolbarStyle

        val saveBtn = new Button("Save"):
          minWidth = 90
          onAction = _ => controller.save()

        val loadBtn = new Button("Load"):
          minWidth = 90
          onAction = _ => controller.load()

        styleAsGameButton(saveBtn)
        styleAsGameButton(loadBtn)

        children = Seq(saveBtn, loadBtn)

      val row2 = new HBox:
        spacing = 20
        alignment = Pos.Center
        style = toolbarStyle

        styleAsRetroLabel(mineCounterLabel)
        styleAsRetroLabel(timerLabel)

        children = Seq(mineCounterLabel, timerLabel)

      children = Seq(row1, row2)

  // Bottom Panel
  private def buildBottomPanel(): HBox =
    new HBox:
      spacing = 8
      alignment = Pos.Center
      style = toolbarStyle

      val undoBtn = new Button("Undo"):
        minWidth = 90
        onAction = _ => controller.undo()

      val redoBtn = new Button("Redo"):
        minWidth = 90
        onAction = _ => controller.redo()

      val restartBtn = new Button("Restart"):
        minWidth = 90
        onAction = _ =>
          controller.restart()
          secondsPassed = 0
          controller.timerSeconds = 0
          refreshTimerLabel()
          refreshMineCounter()
          timer.play()

      styleAsGameButton(undoBtn)
      styleAsGameButton(redoBtn)
      styleAsGameButton(restartBtn)

      children = Seq(undoBtn, redoBtn, restartBtn)

  // Grid
  private def buildGrid(): GridPane =
    val gp = new GridPane:
      padding = Insets(6)
      hgap = 0
      vgap = 0
      style = gridPanelStyle
      alignment = Pos.Center

    fieldButtons =
      (0 until controller.field.rows).map { r =>
        (0 until controller.field.cols).map { c =>
          val btn = new Button:
            minWidth = 32
            minHeight = 32
            maxWidth = 32
            maxHeight = 32

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

  // Cell Graphics
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

  // Observer Update
  override def update(): Unit =
    Platform.runLater {

      // stop timer on win/gameover
      controller.state match
        case _: GameOverState | _: WinState =>
          timer.stop()
        case _ =>
          timer.play()

      refreshMineCounter()
      refreshTimerLabel()

      // rebuild UI if size changed
      if fieldButtons.isEmpty ||
        fieldButtons.length != controller.field.rows ||
        fieldButtons.head.length != controller.field.cols
      then
        stage.scene().root = buildRoot()
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
