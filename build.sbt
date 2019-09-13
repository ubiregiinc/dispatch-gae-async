name := "dispatch-gae-async"

version := "0.2.4"

crossScalaVersions := Seq("2.9.2", "2.10.4")

scalaVersion := "2.10.4"

organization := "net.pomu"

// seaser-repo
resolvers += "The Seasar Foundation Maven2 Repository" at "http://maven.seasar.org/maven2"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.9",
  //"org.scala-tools.testing" %% "specs" % "1.6.9" % "test",
  "org.slim3" % "slim3" % "1.0.12" % "test",
  "com.google.appengine" % "appengine-api-1.0-sdk" % "1.5.1"
//  "org.eclipse.jetty" % "jetty-webapp" % "7.4.5.v20110725" % "container"
)

//seq(sbtappengine.Plugin.webSettings: _*)

resolvers += Resolver.sonatypeRepo("releases") 

publishTo := Some(Resolver.file("net/pomu",file("/Users/pomu0325/dev/repo"))(Patterns(true, Resolver.mavenStyleBasePattern)))
