package htwg.minesweeperse.model.fileIoComponent

import htwg.minesweeperse.model.fieldComponent.impl.IField

trait IFileIO {
  def save(field: IField): Unit
  def load(): IField
}

