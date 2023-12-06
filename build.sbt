name := "stateless-auth-token"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.13.0"

val akkaVersion = "2.5.25"

val ScalatraVersion = "2.7.0-RC1"

scalacOptions ++= Seq(
  "-deprecation"
)

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.google.crypto.tink" % "tink" % "1.12.0",
  "javax.servlet" % "javax.servlet-api" % "4.0.1",
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)

enablePlugins(ScalatraPlugin)
