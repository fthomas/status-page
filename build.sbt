import sbtcrossproject.CrossPlugin.autoImport.crossProject
import sbtcrossproject.CrossType

lazy val root = project
  .in(file("."))
  .aggregate(catsJVM)
  .aggregate(coreJVM)
  .settings(commonSettings)

lazy val cats = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .in(file("modules/cats"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.2.0",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )

lazy val catsJVM = cats.jvm

lazy val core = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .in(file("modules/core"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )

lazy val coreJVM = core.jvm

/// settings

lazy val commonSettings = Def.settings(
  compileSettings
)

lazy val compileSettings = Def.settings(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:higherKinds",
    "-Ypartial-unification",
    "-Ywarn-unused:imports"
  )
)
