import sbt._

import scalariform.formatter.preferences.RewriteArrowSymbols

name          := "spray-oauth-provider-crud"

version       := "0.1"

scalaVersion  := Version.scala

resolvers ++= Seq(
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Dependencies.spraySlick

releaseSettings

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(RewriteArrowSymbols, true)

Revolver.settings

mainClass in Revolver.reStart := Some("net.andrewhj.oauth.Boot")
