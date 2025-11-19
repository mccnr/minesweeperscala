package htwg.minesweeperse.controller
import htwg.minesweeperse.model.Field
import htwg.minesweeperse.model.Cell
import htwg.minesweeperse.util.Observable

enum ControllerResult:
  case Revealed
  case Win
  case GameOver
  case OutOfBounds

class GameController(var field: Field) extends Observable:
  var playing = true

  def processMove(r: Int, c: Int): ControllerResult =
    // ungültige Koordinaten
    if r < 0 || r >= field.rows || c < 0 || c >= field.cols then
      return ControllerResult.OutOfBounds

    // Zelle aufdecken
    field = field.reveal(r, c)
    notifyObservers()

    // Siegesbedingung
    if field.isWin then
      playing = false
      return ControllerResult.Win

    // Mine getroffen?
    if field.cells.flatten.exists(c => c.isMine && c.revealed) then
      playing = false
      return ControllerResult.GameOver

    // Erfolgreicher Zug
    ControllerResult.Revealed