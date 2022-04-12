import com.typesafe.sbt.SbtGit.GitKeys
import sbtcrossproject.{CrossProject, CrossType}
import sbtghactions.JavaSpec.Distribution.Adopt

/// variables

val groupId = "eu.timepit"
val projectName = "status-page"
val gitHubOwner = "fthomas"

val Scala_2_12 = "2.12.15"
val Scala_2_13 = "2.13.8"
val Scala_3 = "3.1.2"

val moduleCrossPlatformMatrix = Map(
  "cats" -> List(JVMPlatform),
  "core" -> List(JVMPlatform)
)

/// sbt-github-actions configuration

ThisBuild / crossScalaVersions := Seq(Scala_2_12, Scala_2_13, Scala_3)
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("master")),
  RefPredicate.StartsWith(Ref.Tag("v"))
)
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Run(
    List("sbt ci-release"),
    name = Some("Publish JARs"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec(Adopt, "8"))
ThisBuild / githubWorkflowBuild :=
  Seq(
    WorkflowStep.Sbt(List("validate"), name = Some("Build project")),
    WorkflowStep.Use(UseRef.Public("codecov", "codecov-action", "v1"), name = Some("Codecov"))
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
  metadataSettings,
  scaladocSettings
)

lazy val compileSettings = Def.settings(
  scalaVersion := Scala_2_13,
  crossScalaVersions := List(Scala_2_12, Scala_2_13, Scala_3)
)

lazy val metadataSettings = Def.settings(
  name := projectName,
  organization := groupId,
  homepage := Some(url(s"https://github.com/$gitHubOwner/$projectName")),
  startYear := Some(2018),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  headerLicense := Some(HeaderLicense.ALv2("2018", s"$projectName contributors")),
  developers := List(
    Developer(
      id = "fthomas",
      name = "Frank S. Thomas",
      email = "",
      url("https://github.com/fthomas")
    )
  )
)

lazy val noPublishSettings = Def.settings(
  publish / skip := true
)

lazy val scaladocSettings = Def.settings(
  Compile / doc / scalacOptions ++= {
    val tree =
      if (isSnapshot.value) GitKeys.gitHeadCommit.value
      else GitKeys.gitDescribedVersion.value.map("v" + _)
    Seq(
      "-doc-source-url",
      s"${scmInfo.value.get.browseUrl}/blob/${tree.get}€{FILE_PATH}.scala",
      "-sourcepath",
      (LocalRootProject / baseDirectory).value.getAbsolutePath
    )
  }
)

/// commands

def addCommandsAlias(name: String, cmds: Seq[String]) =
  addCommandAlias(name, cmds.mkString(";", ";", ""))

addCommandsAlias(
  "validate",
  Seq(
    "clean",
    "headerCheck",
    "scalafmtCheck",
    "scalafmtSbtCheck",
    "test:scalafmtCheck",
    // "coverage",
    "test",
    // "coverageReport",
    "doc",
    "package",
    "packageSrc"
  )
)
