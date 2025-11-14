import model.Field
import controller.GameController
import view.GameView

@main def runMain(): Unit =
    val field = Field.random(5, 5)
    val controller = new GameController(field)
    val view = new GameView(controller)
    view.start()