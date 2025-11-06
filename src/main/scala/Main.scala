def runGameInteractive(
  in: java.io.BufferedReader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in)),
  out: java.io.PrintStream = System.out,
  initialField: Field = randomField(5, 5)
   ): Field =

  var field = initialField
  var playing = true

  out.println("Willkommen bei Minesweeper")
  out.println(field.show())

  while playing do
    out.print("\nGib eine valide Koordinate ein (Z S): ")
    val input = in.readLine()

    if input == null then
      playing = false
    else
      val parts = input.split(" ")

      if parts.length == 2 && parts(0).matches("\\d+") && parts(1).matches("\\d+") then
        val r = parts(0).toInt - 1
        val c = parts(1).toInt - 1

        if r >= 0 && r < field.rows && c >= 0 && c < field.cols then
          field = field.reveal(r, c)
          out.println(field.show())

          if field.isWin then
            out.println("Glückwunsch, du hast alle Minen gefunden!")
            playing = false
        else
          out.println("Koordinate ist außerhalb des Felds.")
      else
        out.println("Bitte zwei Zahlen eingeben, z. B. 2 3.")

      if field.cells.flatten.exists(c => c.isMine && c.revealed) && !field.isWin then
        out.println("Game Over.")
        playing = false

  field // Rückgabewert, finaler Spielzustand

@main def runMain(): Unit =
  // Nur das Spiel starten, wenn keine Tests laufen
  if !sys.props.contains("test.env") then
    runGameInteractive()