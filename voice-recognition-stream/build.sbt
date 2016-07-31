name := "voice-recognition-stream"

version := "1.0"

scalaVersion := "2.11.8"

val circeVersion = "0.5.0-M2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % "2.4.7",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.7",
  "com.typesafe" % "config" % "1.3.0",
  "de.heikoseeberger" %% "akka-http-circe" % "1.7.0",
  "io.circe" %% "circe-parse" % "0.2.1",
  "io.circe" %% "circe-java8" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "org.typelevel" %% "cats" % "0.6.0",
  "org.typelevel" %% "cats-free" % "0.6.0",
  "org.specs2" % "specs2_2.9.1" % "1.8",
  "org.scalatest" % "scalatest_2.11" % "2.2.4",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.2-RC1"
)

//http://stackoverflow.com/questions/18676712/java-sound-devices-found-when-run-in-intellij-but-not-in-sbt
fork in run := true
connectInput in run := true