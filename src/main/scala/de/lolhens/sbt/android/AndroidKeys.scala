package de.lolhens.sbt.android

import sbt.taskKey

trait AndroidKeys {
  lazy val packageAndroid = taskKey[Unit]("")
}
