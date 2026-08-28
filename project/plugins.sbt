addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")
addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.7")
// 3.6.x is not cross-published for Scala 3.8 (the project's Scala version)
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.5.7")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.2")
addSbtPlugin("com.eed3si9n" % "sbt-salad-days" % "0.2.0")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.12.1")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.4.0")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.4.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.22.0")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.12")

// https://github.com/typelevel/sbt-tpolecat/issues/291
libraryDependencies += "org.typelevel" %% "scalac-options" % "0.1.11"
