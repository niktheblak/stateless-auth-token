import scalariform.formatter.preferences._

name := "stateless-auth-token"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.13.0"

val akkaVersion = "2.5.25"

scalacOptions ++= Seq(
  "-deprecation"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "org.jasypt" % "jasypt" % "1.9.3",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)
