package de.lolhens.sbt.android

import sbt._

trait AndroidKeys {
  lazy val packageDex = taskKey[Seq[File]]("Package dex file.")

  lazy val dexOptions = taskKey[DexOptions]("Dexer options.")
}
