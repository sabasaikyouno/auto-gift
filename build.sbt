name := "auto-gift"

version := "0.1"

scalaVersion := "2.13.12"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "4.12.1"
libraryDependencies += "joda-time" % "joda-time" % "2.12.5"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.7.0"
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.32.0"
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"

assemblyMergeStrategy in assembly := {
  case x if x.contains("io.netty.versions.properties") => MergeStrategy.discard
  case PathList("META-INF", "versions", "9", "module-info.class") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}