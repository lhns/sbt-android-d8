package de.lolhens.sbt.android

import java.nio.file.{Files, Path}

import com.android.tools.r8.D8
import sbt.Keys._
import sbt._

object AndroidPlugin extends AutoPlugin {

  object autoImport extends AndroidKeys

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "com.google.android" % "android" % "4.1.1.4"
    ),

    Compile / packageDex / crossTarget := (Compile / packageBin / crossTarget).value,

    Compile / packageDex / dexOptions := {
      val outputPath = (Compile / packageDex / crossTarget).value.asPath
      val packageBinValue = (Compile / packageBin).value
      val dependencyClasspathValue = (Compile / dependencyClasspath).value

      DexOptions(
        outputPath = outputPath,
        programFiles = Seq(packageBinValue.toPath),
        libraryFiles = dependencyClasspathValue.map(_.data.toPath)
      )
    },

    Compile / packageDex := {
      val options = (Compile / packageDex / dexOptions).value

      runD8(options).map(_.toFile)
    }
  )

  def runD8(options: DexOptions): Seq[Path] = {
    D8.run(options.d8Command)
    Seq(options.outputPath.resolve("classes.dex")).filter(Files.exists(_))
  }
}
