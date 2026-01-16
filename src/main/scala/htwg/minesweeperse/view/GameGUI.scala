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
import scala. compiletime. uninitialized
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.util.Duration
import scalafx.scene.text.Font

class GameGUI(controller: IController) extends JFXApp3 with Observer:

  controller.addObserver(this)

  // UI
  private var fieldButtons: Vector[Vector[Button]] = Vector()

  private var mineCounterLabel: Label = uninitialized
  private var timerLabel: Label = uninitialized
  private var smileyButton: Button = uninitialized

  // Retro Font
  private val retroFontName: String =
    try
      val stream = getClass.getResourceAsStream("/fonts/retro.ttf")
      if stream == null then "Monospaced"
      else
        val loaded = Font.loadFont(stream, 16)
        if loaded == null then "Monospaced"
        else loaded.getName
    catch
      case _: Exception => "Monospaced"

  private def retroStyle(fontSize: Int): String =
    s"-fx-font-family: '$retroFontName'; -fx-font-size: ${fontSize}px;"

  // image loader
  private def safeImage(path: String, fallback: String = "/icons/hidden.png"): Image =
    val stream = getClass.getResourceAsStream(path)
    if stream != null then new Image(stream)
    else
      val fallbackStream = getClass.getResourceAsStream(fallback)
      if fallbackStream != null then new Image(fallbackStream)
      else new Image("file:")

  // Timer State
  private var secondsPassed: Int = 0

  private lazy val timer: Timeline = new Timeline {
    cycleCount = Timeline.Indefinite
    keyFrames = Seq(
      KeyFrame(Duration(1000), onFinished = _ => {
        secondsPassed += 1
        controller.timerSeconds = secondsPassed
        if timerLabel != null then timerLabel.text = s"Time: $secondsPassed"
      })
    )
  }

  // Styles
  private val windowStyle =
    "-fx-background-color: #c0c0c0;"

  private val panelStyle =
    "-fx-background-color: #bdbdbd;" +
      "-fx-border-color: #808080;" +
      "-fx-border-width: 3;" +
      "-fx-padding: 6;"

  private val tileHiddenStyle =
    "-fx-background-color: #c0c0c0;" +
      "-fx-border-color: #ffffff #808080 #808080 #ffffff;" +
      "-fx-border-width: 2;" +
      "-fx-padding: 0;" +
      "-fx-background-insets: 0;" +
      "-fx-border-insets: 0;"

  private val tileRevealedStyle =
    "-fx-background-color: #d6d6d6;" +
      "-fx-border-color: #a0a0a0;" +
      "-fx-border-width: 1;" +
      "-fx-padding: 0;" +
      "-fx-background-insets: 0;" +
      "-fx-border-insets: 0;"

  private val gameButtonStyle =
    "-fx-background-color: #c0c0c0;" +
      "-fx-border-color: #ffffff #808080 #808080 #ffffff;" +
      "-fx-border-width: 2;" +
      "-fx-padding: 6 12 6 12;" +
      "-fx-background-insets: 0;" +
      "-fx-border-insets: 0;" +
      retroStyle(16)

  // Mine Counter + Time Counter
  private def refreshMineCounter(): Unit =
    val minesLeft = controller.field.totalMines - controller.field.totalFlags
    mineCounterLabel.text = s"Mines: $minesLeft"

  private def refreshTimerLabel(): Unit =
    secondsPassed = controller.timerSeconds
    timerLabel.text = s"Time: $secondsPassed"

  // Smiley Graphics
  private def smileyGraphic(): ImageView =
    val path =
      controller.state match
        case _: GameOverState => "/icons/smiley_sad.png"
        case _: WinState => "/icons/smiley_happy.png"
        case _ => "/icons/smiley_normal.png"

    new ImageView(safeImage(path)) {
      fitWidth = 28
      fitHeight = 28
      preserveRatio = true
    }

  // Cell Graphics
  private def cellGraphic(r: Int, c: Int): ImageView =
    val field = controller.field

    val path =
      if field.isFlagged(r, c) && !field.isRevealed(r, c) then "/icons/flag.png"
      else if !field.isRevealed(r, c) then "/icons/hidden.png"
      else if field.isMine(r, c) then "/icons/mine.png"
      else
        val count = field.countMinesAround(r, c)
        s"/icons/n$count.png"

    new ImageView(safeImage(path)) {
      fitWidth = 28
      fitHeight = 28
      preserveRatio = true
    }

  // Root Builder
  private def buildRoot(): VBox =
    new VBox:
      alignment = Pos.TopLeft
      spacing = 8
      padding = Insets(10)
      style = windowStyle
      children = Seq(
        buildTopBar(),
        buildGrid(),
        buildBottomBar()
      )

  // Top Bar
  private def buildTopBar(): VBox =
    if mineCounterLabel == null then mineCounterLabel = new Label("")
    if timerLabel == null then timerLabel = new Label("")

    mineCounterLabel.style = retroStyle(18) + "-fx-font-weight: bold;"
    timerLabel.style = retroStyle(18) + "-fx-font-weight: bold;"

    refreshMineCounter()
    refreshTimerLabel()

    val infoRow = new HBox:
      spacing = 20
      alignment = Pos.CenterLeft
      children = Seq(mineCounterLabel, timerLabel)

    val saveBtn = new Button("Save"):
      style = gameButtonStyle
      onAction = _ => controller.save()

    val loadBtn = new Button("Load"):
      style = gameButtonStyle
      onAction = _ =>
        controller.load()
        refreshTimerLabel()
        refreshMineCounter()

    // new Smiley Button
    smileyButton = new Button:
      minWidth = 40
      minHeight = 40
      style = tileHiddenStyle
      graphic = smileyGraphic()
      onAction = _ =>
        controller.restart()
        //refreshMineCounter()

    val buttonsRow = new HBox:
      spacing = 10
      alignment = Pos.CenterLeft
      children = Seq(saveBtn, smileyButton, loadBtn)

    new VBox:
      spacing = 6
      style = panelStyle
      children = Seq(infoRow, buttonsRow)

  // Grid
  private def buildGrid(): GridPane =
    val gp = new GridPane:
      hgap = 0
      vgap = 0
      style = panelStyle
      alignment = Pos.CenterLeft

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

  // Bottom Bar
  private def buildBottomBar(): HBox =
    val undoBtn = new Button("Undo"):
      style = gameButtonStyle
      onAction = _ => controller.undo()

    val redoBtn = new Button("Redo"):
      style = gameButtonStyle
      onAction = _ => controller.redo()

    val exitBtn = new Button("Exit"):
      style = gameButtonStyle
      onAction = _ => {
        Platform.exit()
      }

    new HBox:
      spacing = 10
      alignment = Pos.CenterLeft
      style = panelStyle
      children = Seq(undoBtn, redoBtn, exitBtn)

  // Start
  override def start(): Unit =
    stage = new PrimaryStage:
      title = "Minesweeper in Scala"
      icons += safeImage("/icons/app_icon.png")

      scene = new Scene:
        root = buildRoot()

    timer.play()

  // Observer Update
  override def update(): Unit =
    Platform.runLater {

      controller.state match
        case _: GameOverState | _: WinState =>
          timer.stop()
        case _ =>
          timer.play()

      if mineCounterLabel != null then refreshMineCounter()
      if timerLabel != null then refreshTimerLabel()

      if smileyButton != null then
        smileyButton.graphic = smileyGraphic()

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