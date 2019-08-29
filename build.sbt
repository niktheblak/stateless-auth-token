name := "stateless-auth-token"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.13.0"

val akkaVersion = "2.5.25"

scalacOptions ++= Seq(
  "-deprecation"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.google.crypto.tink" % "tink" % "1.3.0-rc1",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)
