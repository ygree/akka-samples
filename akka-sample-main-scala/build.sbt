organization := "com.typesafe.akka.samples"
name := "akka-sample-main-scala"

val akkaVersion = "2.5.22"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

enablePlugins(Cinnamon)

cinnamon in run := true
cinnamon in test := true

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.typesafe.akka"            %% "akka-slf4j" % akkaVersion,

//  Cinnamon.library.cinnamonCHMetrics,
  Cinnamon.library.cinnamonAkka,

  Cinnamon.library.cinnamonSlf4jMdc, // this dependency required for MDC propagation

  Cinnamon.library.cinnamonPrometheus,
  Cinnamon.library.cinnamonPrometheusHttpServer
)
