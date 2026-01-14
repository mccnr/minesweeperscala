val scala3Version = "3.7.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "ScalaProjekt",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    scalacOptions += "-Wconf:msg=Implicit parameters should be provided with a `using` clause:s",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test,

    // ScalaFX
    libraryDependencies += "org.scalafx" %% "scalafx" % "20.0.0-R31",

    // XML
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.4.0",

    // PlayJson
    libraryDependencies += "com.typesafe.play" % "play-json_2.13" % "2.10.4"
  )