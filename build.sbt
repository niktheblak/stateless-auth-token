import scalariform.formatter.preferences._

name := "stateless-auth-token"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.4"

incOptions := incOptions.value.withNameHashing(true)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.6"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.6" % "test"

libraryDependencies += "io.spray" %% "spray-can" % "1.3.2"

libraryDependencies += "io.spray" %% "spray-routing" % "1.3.2"

libraryDependencies += "org.jasypt" % "jasypt" % "1.9.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

libraryDependencies += "io.spray" %% "spray-testkit" % "1.3.2" % "test"

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(PreserveSpaceBeforeArguments, true)
