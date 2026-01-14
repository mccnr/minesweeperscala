package htwg.minesweeperse.model.fileIoComponent

import htwg.minesweeperse.model.fieldComponent.impl.IField

/* trait IFileIO {
  def save(field: IField): Unit
  def load(): IField
} */

trait IFileIO {
  def save(field: IField, seconds: Int): Unit
  def load(): (IField, Int)
}

