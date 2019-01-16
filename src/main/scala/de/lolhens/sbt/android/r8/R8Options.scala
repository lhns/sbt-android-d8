package de.lolhens.sbt.android.r8

import java.nio.file.Path
import java.util

import com.android.tools.r8._
import com.android.tools.r8.origin.Origin
import com.android.tools.r8.utils.AndroidApiLevel

import scala.collection.JavaConverters._

case class R8Options(outputPath: Path,
                     programFiles: Seq[Path],
                     libraryFiles: Seq[Path] = Seq.empty,
                     release: Boolean = false,
                     outputMode: OutputMode = OutputMode.DexIndexed,
                     minApiLevel: Int = AndroidApiLevel.getDefault.getLevel,
                     intermediate: Boolean = false,
                     noDesugaring: Boolean = false,
                     noTreeShaking: Boolean = false,
                     noMinification: Boolean = false,
                     classpathFiles: Seq[Path] = Seq.empty,
                     mainDexFiles: Seq[Path] = Seq.empty,
                     mainDexRules: Seq[String] = Seq.empty,
                     mainDexListConsumer: String => Unit = _ => (),
                     proguardConfigs: Seq[String] = Seq.empty,
                     proguardMapConsumer: String => Unit = _ => (),
                    ) {
  private def addCommonOptions(builder: BaseCompilerCommand.Builder[_, _]): Unit = {
    builder.setMode(
      if (release) CompilationMode.RELEASE
      else CompilationMode.DEBUG
    )

    builder.setOutput(outputPath, outputMode)

    builder.addProgramFiles(programFiles.asJavaCollection)

    builder.addLibraryFiles(libraryFiles.asJavaCollection)

    builder.setMinApiLevel(minApiLevel)

    builder.addMainDexListFiles(mainDexFiles.asJavaCollection)

    builder.setDisableDesugaring(noDesugaring)
  }

  def d8Command: D8Command = {
    val builder = D8Command.builder()

    addCommonOptions(builder)

    builder.setIntermediate(intermediate)

    builder.addClasspathFiles(classpathFiles.asJavaCollection)

    builder.build()
  }

  def r8Command: R8Command = {
    def linesList(string: String): util.List[String] =
      util.Arrays.asList(string.split("\\R"): _*)

    val builder = R8Command.builder()

    addCommonOptions(builder)

    builder.setDisableTreeShaking(noTreeShaking)

    builder.setDisableMinification(noMinification)

    for (config <- proguardConfigs)
      builder.addProguardConfiguration(linesList(config), Origin.unknown())

    builder.setProguardMapConsumer((string: String, _: DiagnosticsHandler) => proguardMapConsumer(string))

    for (rules <- mainDexRules)
      builder.addMainDexRules(linesList(rules), Origin.unknown())

    if (mainDexFiles.nonEmpty || mainDexRules.nonEmpty)
      builder.setMainDexListConsumer((string: String, _: DiagnosticsHandler) => mainDexListConsumer(string))

    builder.build()
  }
}
