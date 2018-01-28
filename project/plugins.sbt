// fast development turnaround when using sbt ~re-start
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.4" exclude("org.apache.maven", "maven-plugin-api"))

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.21")

addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.5")

addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.9.0")

