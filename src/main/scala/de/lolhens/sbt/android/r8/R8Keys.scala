package de.lolhens.sbt.android.r8

import sbt.{File, taskKey}

trait R8Keys {
  lazy val packageDex = taskKey[Seq[File]]("Package dex file using D8.")

  lazy val packageMinifiedDex = taskKey[Seq[File]]("Package minified dex file using R8.")

  lazy val dexOptions = taskKey[R8Options]("D8/R8 dexer options.")
}
