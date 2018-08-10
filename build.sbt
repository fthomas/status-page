import sbtcrossproject.CrossPlugin.autoImport.crossProject
import sbtcrossproject.CrossType

lazy val root = project
  .in(file("."))
  .aggregate(coreJVM)

lazy val core = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .in(file("modules/core"))

lazy val coreJVM = core.jvm
