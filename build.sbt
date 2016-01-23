lazy val commonSettings = Seq(
  organization := "com.codecommit",

  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/")),

  scalaVersion := "2.11.7",

  crossScalaVersions := Seq(scalaVersion.value/*, "2.10.6"*/),

  shimsVersion := "0.1-b7e8e5f",

  libraryDependencies += "org.specs2" %% "specs2-core" % "3.6.6" % "test",

  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.7.1" cross CrossVersion.binary),

  scalacOptions += "-language:_",      // I really can't be bothered with SIP-18
  scalacOptions += "-Ybackend:GenBCode",
  // scalacOptions += "-Xlog-implicits",

  scalacOptions in Test += "-Yrangepos",

  isSnapshot := version.value endsWith "SNAPSHOT",      // so… sonatype doesn't like git hash snapshots

  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },

  sonatypeProfileName := "com.codecommit",

  pomExtra :=
    <developers>
      <developer>
        <id>djspiewak</id>
        <name>Daniel Spiewak</name>
        <url>http://www.codecommit.com</url>
      </developer>
      <developer>
        <id>alissapajer</id>
        <name>Alissa Pajer</name>
      </developer>
    </developers>,

  homepage := Some(url("https://github.com/djspiewak/emm")),

  scmInfo := Some(ScmInfo(url("https://github.com/djspiewak/emm"),
    "git@github.com:djspiewak/emm.git")))

lazy val root = project.in(file(".")).settings(commonSettings: _*).aggregate(core, cats, scalaz71, scalaz72).settings(
  name := "emm",

  publish := (),
  publishLocal := (),
  publishArtifact := false)

lazy val core = project.in(file("core")).settings(commonSettings: _*)
lazy val cats = project.in(file("cats")).settings(commonSettings: _*).dependsOn(core)

lazy val scalaz71 = project
  .in(file("scalaz"))
  .settings(commonSettings: _*)
  .dependsOn(core)
  .settings(
    name := "emm-scalaz-71",
    target := target.value / "7.1",
    libraryDependencies += "com.codecommit" %% "shims-scalaz-71" % shimsVersion.value)

lazy val scalaz72 = project
  .in(file("scalaz"))
  .settings(commonSettings: _*)
  .dependsOn(core)
  .settings(
    name := "emm-scalaz-72",
    target := target.value / "7.2",
    libraryDependencies += "com.codecommit" %% "shims-scalaz-72" % shimsVersion.value)

enablePlugins(GitVersioning)

val ReleaseTag = """^v([\d\.]+)$""".r

git.baseVersion := "0.1"

git.gitTagToVersionNumber := {
  case ReleaseTag(version) => Some(version)
  case _ => None
}

git.formattedShaVersion := {
  val suffix = git.makeUncommittedSignifierSuffix(git.gitUncommittedChanges.value, git.uncommittedSignifier.value)

  git.gitHeadCommit.value map { _.substring(0, 7) } map { sha =>
    git.baseVersion.value + "-" + sha + suffix
  }
}
