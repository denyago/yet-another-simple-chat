enablePlugins(JavaAppPackaging)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "name.denyago",
      scalaVersion := "2.12.2",
      version      := "0.0.1-ALPHA"
    )),
    name := "YASC",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-http_2.12" % "10.0.7",

      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
      "ch.qos.logback" % "logback-classic" % "1.1.7",

      "org.scalatest" %% "scalatest" % "3.0.1" % Test,
      "org.scalaj" %% "scalaj-http" % "2.3.0" % Test
    ),
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature", "-language:postfixOps")
  )

