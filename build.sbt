lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-android-d8",
    version := "0.0.0-SNAPSHOT",

    resolvers += "Google Maven Repository" at "https://maven.google.com",

    libraryDependencies ++= Seq(
      "com.android.tools" % "r8" % "1.2.52"
    )
  )

//lazy val test = (project in file("test"))
//  .enablePlugins(R8Plugin)
