name := "stateless-auth-token"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

incOptions := incOptions.value.withNameHashing(true)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.12",
    "io.spray" %% "spray-can" % "1.3.3",
    "io.spray" %% "spray-routing" % "1.3.3",
    "org.jasypt" % "jasypt" % "1.9.2",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.12" % "test",
    "io.spray" %% "spray-testkit" % "1.3.3" % "test"
)
