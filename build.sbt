name := """quotes"""
organization := "com.jdgonzalez907"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

libraryDependencies += guice
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.8"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.2"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
libraryDependencies += "org.mockito" %% "mockito-scala" % "1.5.18" % Test
libraryDependencies += specs2 % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.jdgonzalez907.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.jdgonzalez907.binders._"
