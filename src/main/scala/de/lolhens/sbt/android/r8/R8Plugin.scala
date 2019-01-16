package de.lolhens.sbt.android.r8

import java.nio.file.{Files, Path}
import java.util.stream.Collectors

import com.android.tools.r8.{D8, R8}
import sbt.Keys._
import sbt._

import scala.collection.JavaConverters._

object R8Plugin extends AutoPlugin {

  object autoImport extends R8Keys

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "com.google.android" % "android" % "4.1.1.4"
    ),

    Compile / packageDex / crossTarget := (Compile / packageBin / crossTarget).value / "dex",
    Compile / packageMinifiedDex / crossTarget := (Compile / packageBin / crossTarget).value / "dex",

    dexOptionsTaskSettings(packageDex),
    dexOptionsTaskSettings(packageMinifiedDex),

    packageDexTaskSettings(packageDex, runD8),
    packageDexTaskSettings(packageMinifiedDex, runR8)
  )

  private def dexOptionsTaskSettings(packageDexKey: TaskKey[Seq[File]]) =
    Compile / packageDexKey / dexOptions := {
      val outputPath = (Compile / packageDexKey / crossTarget).value.asPath
      val packageBinValue = (Compile / packageBin).value
      val dependencyClasspathValue = (Compile / dependencyClasspath).value

      R8Options(
        outputPath = outputPath,
        programFiles = Seq(packageBinValue.toPath),
        libraryFiles = dependencyClasspathValue.map(_.data.toPath)
      )
    }

  private def packageDexTaskSettings(packageDexKey: TaskKey[Seq[File]], run: R8Options => Seq[Path]) =
    Compile / packageDexKey := {
      val options = (Compile / packageDexKey / dexOptions).value

      Files.createDirectories(options.outputPath)

      // TODO: Incremental compilation?
      dexFiles(options.outputPath).foreach(Files.delete)

      run(options).map(_.toFile)
    }

  def runD8(options: R8Options): Seq[Path] = {
    D8.run(options.d8Command)

    dexFiles(options.outputPath)
  }

  // TODO: throws AbortException: Error: Compilation can't be completed because some library classes are missing.
  def runR8(options: R8Options): Seq[Path] = {
    //println(options)

    R8.run(options.r8Command)

    dexFiles(options.outputPath)
  }

  private def dexFiles(path: Path): Seq[Path] =
    Files.list(path).collect(Collectors.toList()).asScala
      .filter(file => file.getFileName.toString.endsWith(".dex") && Files.isRegularFile(file))
}
