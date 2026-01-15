package htwg.minesweeperse

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.controllerComponent.impl.implGC
import htwg.minesweeperse.model.fieldComponent.impl.IField
import net.codingwell.scalaguice.ScalaModule
import htwg.minesweeperse.model.fieldComponent.impl.implFieldBase
import htwg.minesweeperse.util.strategy.revealComponent.impl.{IRevealStrategy, NoFloodRevealStrategy, StandardRevealStrategy}
import htwg.minesweeperse.model.fileIoComponent._
import htwg.minesweeperse.model.fileIoComponent.fileJsonImpl.implJSON
import htwg.minesweeperse.model.fileIoComponent.fileXmlImpl.implXML

class MinesweeperModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {

    bind[IController].to[implGC]
    bind[IField].annotatedWithName("small").toInstance(new implFieldBase(4,4))
    bind[IField].annotatedWithName("medium").toInstance(new implFieldBase(8,8))
    bind[IField].annotatedWithName("large").toInstance(new implFieldBase(16,16))

    bind[IRevealStrategy].annotatedWith(Names.named("standard")).toInstance((new StandardRevealStrategy))
    bind[IRevealStrategy].annotatedWith(Names.named("noflood")).toInstance(new NoFloodRevealStrategy)

    bind[IFileIO].annotatedWith(Names.named("json")).toInstance((new implJSON))
    bind[IFileIO].annotatedWith(Names.named("xml")).toInstance(new implXML)

    //bind[IFileIO].to[implXML]
  }
}