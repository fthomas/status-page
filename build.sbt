import sbtcrossproject.CrossPlugin.autoImport.crossProject
import sbtcrossproject.CrossProject
import sbtcrossproject.CrossType

/// variables

val groupId = "eu.timepit"
val projectName = "status-page"
val gitHubOwner = "fthomas"

val moduleCrossPlatformMatrix = Map(
  "cats" -> List(JVMPlatform),
  "core" -> List(JVMPlatform)
)

/// projects

lazy val root = project
  .in(file("."))
  .aggregate(catsJVM)
  .aggregate(coreJVM)
  .settings(commonSettings)
  .settings(noPublishSettings)

lazy val cats = myCrossProject("cats")
  .dependsOn(core)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.catsCore,
      Dependencies.scalatest % Test
    )
  )

lazy val catsJVM = cats.jvm

lazy val core = myCrossProject("core")
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.scalatest % Test
    )
  )

lazy val coreJVM = core.jvm

/// settings

def myCrossProject(name: String): CrossProject =
  CrossProject(name, file(name))(moduleCrossPlatformMatrix(name): _*)
    .crossType(CrossType.Pure)
    .withoutSuffixFor(JVMPlatform)
    .in(file(s"modules/$name"))
    .settings(moduleName := s"$projectName-$name")
    .settings(commonSettings)

lazy val commonSettings = Def.settings(
  compileSettings,
  metadataSettings
)

lazy val compileSettings = Def.settings(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:higherKinds",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xfuture",
    "-Xlint",
    "-Ypartial-unification",
    "-Ywarn-value-discard"
  )
)

lazy val metadataSettings = Def.settings(
  name := projectName,
  organization := groupId,
  homepage := Some(url(s"https://github.com/$gitHubOwner/$projectName")),
  startYear := Some(2018),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      id = "fthomas",
      name = "Frank S. Thomas",
      email = "",
      url("https://github.com/fthomas")))
)

lazy val noPublishSettings = Def.settings(
  skip in publish := true
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
    "coverage",
    "test",
    "coverageReport",
    "doc",
    "package",
    "packageSrc"
  )
)
