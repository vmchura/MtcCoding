import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.SbtScalariform.autoImport.scalariformAutoformat
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import play.sbt.PlayImport._
import play.sbt.routes.RoutesKeys._
import play.twirl.sbt.Import.TwirlKeys
import sbt.Keys._
import sbt._
import scalariform.formatter.preferences.{DanglingCloseParenthesis, DoubleIndentConstructorArguments, FormatXml, Preserve}

object Dependencies {
  val commonSettingsPlayProject = List(
    //-------------- imports for silhouette
    resolvers += Resolver.jcenterRepo,

    resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",

    libraryDependencies ++= Seq(
      "com.mohiva" %% "play-silhouette" % "6.1.0",
      "com.mohiva" %% "play-silhouette-password-bcrypt" % "6.1.0",
      "com.mohiva" %% "play-silhouette-persistence" % "6.1.0",
      "com.mohiva" %% "play-silhouette-crypto-jca" % "6.1.0",
      "com.mohiva" %% "play-silhouette-totp" % "6.1.0",
      // https://mvnrepository.com/artifact/org.webjars/webjars-play
      "org.webjars" %% "webjars-play" % "2.8.0",

      "org.webjars" % "bootstrap" % "4.3.1" exclude("org.webjars", "jquery"),
      "org.webjars" % "jquery" % "3.2.1",
      "org.webjars" % "jquery-easing" % "1.4.1",
      "net.codingwell" %% "scala-guice" % "4.2.6",
      "com.iheart" %% "ficus" % "1.4.7",
      "com.typesafe.play" %% "play-mailer" % "8.0.0",
      "com.typesafe.play" %% "play-mailer-guice" % "8.0.0",
      "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.2-akka-2.6.x",
      // https://mvnrepository.com/artifact/com.adrianhurt/play-bootstrap
      "com.adrianhurt" %% "play-bootstrap" % "1.5.1-P27-B4",
      "com.mohiva" %% "play-silhouette-testkit" % "6.1.0" % "test",
      specs2 % Test,
      ehcache,
      guice,
      filters
    ),


    // https://github.com/playframework/twirl/issues/105
    TwirlKeys.templateImports := Seq(),

    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      //"-Xlint", // Enable recommended additional warnings.
      "-Ywarn-numeric-widen", // Warn when numerics are widened.
      // Play has a lot of issues with unused imports and unsued params
      // https://github.com/playframework/playframework/issues/6690
      // https://github.com/playframework/twirl/issues/105
      "-Xlint:-unused,_"
    ),

    //********************************************************
    // Scalariform settings
    //********************************************************

    scalariformAutoformat := true,

    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(FormatXml, false)
      .setPreference(DoubleIndentConstructorArguments, false)
      .setPreference(DanglingCloseParenthesis, Preserve),
    // end silhouette import

    //for slick
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-slick" % "5.0.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
    ),

    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5",

    //end slick
    //test on play
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % "test",
    //chimmey macro to copy case classes
    libraryDependencies += "io.scalaland" %% "chimney" % "0.4.1",

    //refined, to improve basic types
    libraryDependencies ++= Seq(
      "eu.timepit"  %%  "refined" % "0.9.12",
      "be.venneborg" %% "slick-refined" %"0.5.0",
      "eu.timepit" %% "refined-scalaz"          % "0.9.12"),


    //scalaz
    // https://mvnrepository.com/artifact/org.scalaz/scalaz-core
    libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.3.0-M32",



    //for testing
    //libraryDependencies += "com.h2database" % "h2" % "1.4.200",

      // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
    EclipseKeys.preTasks := Seq(compile in Compile)
  )
}
