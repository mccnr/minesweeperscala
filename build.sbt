val scala3Version = "3.7.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "ScalaProjekt",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
	scalacOptions += "-Wconf:msg=Implicit parameters should be provided with a `using` clause:s",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test,

    // ScalaFX
    libraryDependencies += "org.scalafx" %% "scalafx" % "20.0.0-R31",

    // JavaFX (Windows)
    {
      val javaFXVersion = "20"
      libraryDependencies += "org.openjfx" % "javafx-base" % javaFXVersion classifier "win"
      libraryDependencies += "org.openjfx" % "javafx-controls" % javaFXVersion classifier "win"
      libraryDependencies += "org.openjfx" % "javafx-graphics" % javaFXVersion classifier "win"
    }
  )