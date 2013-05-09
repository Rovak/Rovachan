import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "dist"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    anorm,
    "commons-io" % "commons-io" % "2.1",
    "com.typesafe.akka" %% "akka-actor" % "2.1.2"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
