name := "dispatch-gae-async"

version := "0.1"

scalaVersion := "2.8.1"

organization := "net.pomu"

// seaser-repo
resolvers += "The Seasar Foundation Maven2 Repository" at "http://maven.seasar.org/maven2"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.3",
  "org.scala-tools.testing" %% "specs" % "1.6.8" % "test",
  "org.slim3" % "slim3" % "1.0.12" % "test"
)

seq(sbtappengine.AppenginePlugin.webSettings: _*)
