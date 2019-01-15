lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-android-d8",
    version := "0.0.0",

    resolvers += "google-maven" at "https://maven.google.com/",

    libraryDependencies ++= Seq(
      "com.android.tools" % "r8" % "1.2.52"
    )
  )
