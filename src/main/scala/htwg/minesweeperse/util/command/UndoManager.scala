package htwg.minesweeperse.util.command

class UndoManager:
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Unit =
    undoStack = command :: undoStack
    command.doStep()
    redoStack = Nil  // nach neuem Schritt kein Redo mehr

  def undo(): Unit =
    undoStack match
      case Nil =>
      case head :: tail =>
        head.undoStep()
        undoStack = tail
        redoStack = head :: redoStack

  def redo(): Unit =
    redoStack match
      case Nil =>
      case head :: tail =>
        head.redoStep()
        redoStack = tail
        undoStack = head :: undoStack