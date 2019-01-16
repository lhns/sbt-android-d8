package de.lolhens.sbt.android

import java.nio.file.Path

import com.android.tools.r8.utils.AndroidApiLevel
import com.android.tools.r8.{CompilationMode, D8Command, OutputMode}

import scala.collection.JavaConverters._

case class DexOptions(outputPath: Path,
                      programFiles: Seq[Path],
                      libraryFiles: Seq[Path] = Seq.empty,
                      release: Boolean = false,
                      minApiLevel: Int = AndroidApiLevel.getDefault.getLevel,
                      filePerClass: Boolean = false) {
  def d8Command: D8Command = {
    val builder = D8Command.builder()

    builder.setMode(
      if (release) CompilationMode.RELEASE
      else CompilationMode.DEBUG
    )
    builder.setOutput(outputPath,
      if (filePerClass) OutputMode.DexFilePerClassFile
      else OutputMode.DexIndexed
    )
    builder.addProgramFiles(programFiles.asJavaCollection)
    builder.addLibraryFiles(libraryFiles.asJavaCollection)
    builder.setMinApiLevel(minApiLevel)

    builder.build()
  }
}
