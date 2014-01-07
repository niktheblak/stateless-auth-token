name := "stateless-auth-token"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.3"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.3"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test"

libraryDependencies += "io.spray" % "spray-can" % "1.2.0"

libraryDependencies += "io.spray" % "spray-routing" % "1.2.0"

libraryDependencies += "org.jasypt" % "jasypt" % "1.9.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % "test"

libraryDependencies += "io.spray" % "spray-testkit" % "1.2.0" % "test"
