organization := "com.typesafe.akka.samples"

val akkaVersion = "2.6.0-RC1"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-persistence-typed"   % akkaVersion,
  "org.iq80.leveldb"            % "leveldb"                  % "0.7",
  "org.fusesource.leveldbjni"   % "leveldbjni-all"           % "1.8",
  "com.typesafe.akka"          %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest"              %% "scalatest"                % "3.0.7"     % Test
) ++ cinnamonDeps

// To enable https://developer.lightbend.com/docs/telemetry/current
cinnamon in run := true
//libraryDependencies += Cinnamon.library.cinnamonAkka
enablePlugins(Cinnamon)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

lazy val cinnamonDeps = Seq(
  //  Cinnamon.library.cinnamonJvmMetricsProducer,
  Cinnamon.library.cinnamonAkka,
  Cinnamon.library.cinnamonAkkaPersistence,
  //  Cinnamon.library.cinnamonLagom,
  Cinnamon.library.cinnamonScala,

  //  Cinnamon.library.cinnamonCHMetrics,
  //  Cinnamon.library.cinnamonCHMetricsElasticsearchReporter,

  Cinnamon.library.cinnamonPrometheus,
  Cinnamon.library.cinnamonPrometheusHttpServer

  //  Cinnamon.library.cinnamonAkkaHttp,
  //  Cinnamon.library.cinnamonPlay,

//  Cinnamon.library.cinnamonOpenTracing,
//  Cinnamon.library.cinnamonOpenTracingJaeger,
//  Cinnamon.library.cinnamonOpenTracingZipkin
)
