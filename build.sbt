name := "stats"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % "6.44.0",
  "joda-time" % "joda-time" % "2.9.9",
  "com.typesafe" % "config" % "1.3.1",
  "org.json4s" %% "json4s-native" % "3.2.9",
  "org.json4s" %% "json4s-ext" % "3.2.9",
  "org.logback-extensions" % "logback-ext-loggly" % "0.1.4",
  "org.scalactic" %% "scalactic" % "3.0.2",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "org.mockito" % "mockito-core" % "2.8.47"
)
