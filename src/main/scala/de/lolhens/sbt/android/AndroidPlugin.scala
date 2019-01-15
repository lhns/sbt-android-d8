package de.lolhens.sbt.android

import java.nio.file.Paths

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
    packageAndroid := {
      runD8(D8Options(
        outputPath = Paths.get("."),
        libraryFiles = (Seq((packageBin in Compile).value) ++ (dependencyClasspath in Compile).value.map(_.data)).map(_.toPath)
      ))
    }
  )

  def runD8(options: D8Options): Unit = {
    val command = options.d8Command

    D8.run(command)
  }


}
