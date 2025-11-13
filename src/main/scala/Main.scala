import model.Field
import controller.GameController
import view.GameView

@main def runMain(): Unit =
  if !sys.props.contains("test.env") then
    val field = Field.random(4, 3)
    val controller = new GameController(field)
    val view = new GameView(controller)
    view.start()