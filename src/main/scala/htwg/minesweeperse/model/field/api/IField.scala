package htwg.minesweeperse.model.field.api

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
