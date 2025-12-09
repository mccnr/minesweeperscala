package htwg.minesweeperse.controller

sealed trait InputCommand // Es dürfen alle Unterklassen dieses Traits nur in selber Datei definieren
case class Move(r: Int, c: Int) extends InputCommand
case object UndoCmd extends InputCommand // Exakt eine Instanz jeweils
case object RedoCmd extends InputCommand
case object InvalidCmd extends InputCommand

