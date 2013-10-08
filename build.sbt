name := "stateless-auth-token"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.2"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "org.scala-lang" %% "scala-pickling" % "0.8.0-SNAPSHOT"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.0-RC1"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.2.0-RC1" % "test"

libraryDependencies += "io.spray" % "spray-can" % "1.2-M8"

libraryDependencies += "io.spray" % "spray-routing" % "1.2-M8"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.2" % "test"
