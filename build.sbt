import java.util.Properties
import org.scalajs.jsenv.nodejs.NodeJSEnv
import org.scalajs.linker.interface.ESVersion
import xerial.sbt.Sonatype.sonatypeCentralHost

val publishLocalGradleDependencies =
  taskKey[Unit]("Builds and publishes gradle dependencies")

val props =
  settingKey[Properties]("Main project properties")

inThisBuild(
  Seq(
    organization := "org.funfix",
    scalaVersion := "3.8.1",
    // Configure for Sonatype Central Portal
    sonatypeCredentialHost := sonatypeCentralHost,
    usePgpKeyHex(sys.env.getOrElse("PGP_KEY_ID", "")),
    // ---
    // Settings for dealing with the local Gradle-assembled artifacts
    // Also see: publishLocalGradleDependencies
    resolvers ++= Seq(Resolver.mavenLocal),
    versionScheme := Some("early-semver"),
    // For publishing to Maven Central
    homepage := Some(url("https://github.com/funfix/continuations4s")),
    licenses := List(License.Apache2),
    developers := List(
      Developer(
        id = "alexelcu",
        name = "Alexandru Nedelcu",
        email = "noreply@alexn.org",
        url = url("https://alexn.org")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/funfix/continuations4s"),
        "scm:git@github.com:funfix/continuations4s.git"
      )
    ),
    organizationName := "Funfix",
    organizationHomepage := Some(url("https://funfix.org")),
    description :=
      "Exposes a lower-level delimited continuations API, supported on JVM, WASM, and Native platforms."
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges

val sharedSettings = Seq(
  scalacOptions ++= Seq("-no-indent", "-rewrite"),
  // ScalaDoc settings
  autoAPIMappings := true,
  scalacOptions ++= Seq(
    // Note, this is used by the doc-source-url feature to determine the
    // relative path of a given source file. If it's not a prefix of a the
    // absolute path of the source file, the absolute path of that file
    // will be put into the FILE_SOURCE variable, which is
    // definitely not what we want.
    "-sourcepath",
    file(".").getAbsolutePath.replaceAll("[.]$", ""),
    // Debug warnings
    "-Wconf:any:warning-verbose"
  )
)

lazy val continuations4s = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(sharedSettings)
  .settings(
    name := "continuations4s",
    libraryDependencies ++= Seq(
      // Testing
      "org.scalameta" %%% "munit" % "1.3.0" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / doc / scalacOptions ++= Seq(
      "-doc-root-content",
      file("./README.md").getAbsolutePath
    )
  )
  .jvmSettings(
    Seq(
      Test / fork := true
    )
  )
  .nativeSettings(
    Seq(
      nativeConfig ~= { c =>
        c.withMultithreading(true)
      }
    )
  )
  .jsSettings(
    // Emit ES modules with the Wasm backend
    scalaJSLinkerConfig :=
      scalaJSLinkerConfig.value
        .withESFeatures(_.withESVersion(ESVersion.ES2017)) // enable async/await
        .withExperimentalUseWebAssembly(true) // use the Wasm backend
        .withModuleKind(ModuleKind.ESModule) // required by the Wasm backend
    ,
    // Configure Node.js (at least v23) to support the required Wasm features
    jsEnv := {
      val config = NodeJSEnv
        .Config()
        .withArgs(
          List(
            "--experimental-wasm-exnref", // always required
            "--experimental-wasm-jspi", // required for js.async/js.await
            "--experimental-wasm-imported-strings" // optional (good for performance)
            // "--turboshaft-wasm" // optional, but significantly increases stability
          )
        )
      new NodeJSEnv(config)
    }
  )

addCommandAlias(
  "ci-test",
  ";Test/compile;test;scalafmtCheckAll"
)
addCommandAlias(
  "ci-publish-local",
  ";Test/compile; publishLocal"
)
addCommandAlias(
  "ci-publish",
  ";Test/compile; publishSigned; sonaRelease"
)
addCommandAlias(
  "ci-snapshot",
  ";Test/compile; +publishSigned"
)
