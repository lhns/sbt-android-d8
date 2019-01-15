package de.lolhens.sbt.android

import com.android.tools.r8.D8
import sbt.Keys._
import sbt._

object AndroidPlugin extends AutoPlugin {

  object autoImport extends AndroidKeys {

  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "com.google.android" % "android" % "4.1.1.4"
    ),

    packageAndroid / artifactPath := (Compile / packageBin / artifactPath).value,

    packageAndroid := {
      val outputPath = (packageAndroid / artifactPath).value.asPath

      runD8(D8Options(
        outputPath = outputPath,
        programFiles = Seq((Compile / packageBin).value.toPath),
        libraryFiles = (dependencyClasspath in Compile).value.map(_.data.toPath)
      ))
    }
  )

  def runD8(options: D8Options): Unit =
    D8.run(options.d8Command)
}
