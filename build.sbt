import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

//for scaliriform
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

lazy val server = (project in file("server")).settings(commonSettings).settings(
  name := "MTCServer",
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.1.4",
    guice,
    specs2 % Test
  ),
  routesImport += "utils.route.Binders._",
  sources in (Compile, doc) := Seq.empty,
  publishArtifact in (Compile, packageDoc) := false,
  libraryDependencies += "com.zaxxer" % "HikariCP" % "3.4.2"

).settings(Dependencies.commonSettingsPlayProject).enablePlugins(PlayScala).dependsOn(sharedJvm)



lazy val client = (project in file("client")).settings(commonSettings).settings(
  name := "MTCClient",
  scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "1.0.0",

    // https://mvnrepository.com/artifact/org.lrng.binding/html

    // PLOTLY
    "org.plotly-scala" %%% "plotly-render" % "0.7.2"

  ),
  sources in (Compile, doc) := Seq.empty,
  publishArtifact in (Compile, packageDoc) := false,
  scalacOptions ++= {
    import Ordering.Implicits._
    if (VersionNumber(scalaVersion.value).numbers >= Seq(2L, 13L)) {
      Seq("-Ymacro-annotations")
    } else {
      Nil
    }
  },

  // Enable macro annotations by adding compiler plugins for Scala 2.12
  libraryDependencies ++= {
    import Ordering.Implicits._
    if (VersionNumber(scalaVersion.value).numbers >= Seq(2L, 13L)) {
      Nil
    } else {
      Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full))
    }
  },
  libraryDependencies += "org.lrng.binding" %%% "html" % "1.0.2+98-d8a7f12d"
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)


lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.13.1",
  organization := "io.vmchura",
  libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.9.5",
  version      := "1.1.3"

)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
