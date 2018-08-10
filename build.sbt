import sbtcrossproject.CrossPlugin.autoImport.crossProject
import sbtcrossproject.CrossType

/// variables

val projectName = "status-page"

/// projects

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

lazy val metadataSettings = Def.settings(
  name := projectName,
  startYear := Some(2018)
)

/// commands

def addCommandsAlias(name: String, cmds: Seq[String]) =
  addCommandAlias(name, cmds.mkString(";", ";", ""))

addCommandsAlias(
  "validate",
  Seq(
    "clean",
    "scalafmtCheck",
    "scalafmtSbtCheck",
    "test:scalafmtCheck",
    "test",
    "doc",
    "package",
    "packageSrc"
  )
)
