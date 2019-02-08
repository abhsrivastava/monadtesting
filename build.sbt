ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "monadtesting",
    autoCompilerPlugins := true,
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8"),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "1.2.0",
      "org.scalaz" %% "scalaz-zio" % "0.6.0",
      "org.scalaz" %% "scalaz-zio-interop" % "0.5.0",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ),
    scalacOptions ++= Seq(
      "-Ypartial-unification"
    )
  )