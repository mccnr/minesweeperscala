package htwg.minesweeperse.model.fieldComponent.impl

trait IField:
  def rows: Int

  def cols: Int

  def reveal(r: Int, c: Int): IField

  def revealOne(r: Int, c: Int): IField

  def revealAllMines(): IField

  def isWin: Boolean

  def hasRevealedMine: Boolean

  def isRevealed(r: Int, c: Int): Boolean

  def isMine(r: Int, c: Int): Boolean

  def countMinesAround(r: Int, c: Int): Int

  def show(): String

  // Flag test
  def toggleFlag(r: Int, c: Int): IField
  def isFlagged(r: Int, c: Int): Boolean
  def unflagAndRevealOne(r: Int, c: Int): IField

  // Anzahl Minen, Flags

  def totalMines: Int
  def totalFlags: Int

