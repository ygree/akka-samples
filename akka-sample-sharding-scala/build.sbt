import com.lightbend.cinnamon.sbt.Cinnamon.CinnamonKeys.cinnamon
import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

lazy val akkaVersion = "2.5.22"

lazy val `akka-sample-sharding-scala` = project
  .in(file("."))
  .settings(multiJvmSettings: _*)
  .settings(
    organization := "com.typesafe.akka.samples",
    scalaVersion := "2.11.11",
    scalacOptions in Compile ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    javaOptions in run ++= Seq("-Xms128m", "-Xmx1024m", "-Djava.library.path=./target/native"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-remote" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
      "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
      "org.scalatest" %% "scalatest" % "3.0.7" % Test,
      "io.kamon" % "sigar-loader" % "1.6.6-rev002"),
    fork in run := true,
    //    mainClass in (Compile, run) := Some("sample.sharding.ShardingApp"),
    // disable parallel tests
    parallelExecution in Test := false,
    licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
  )
  .configs(MultiJvm)
  .enablePlugins(Cinnamon)
  .settings(
    cinnamon in run := true,
    cinnamon in test := true,

    libraryDependencies ++= Seq(
      //      "ch.qos.logback" % "logback-classic" % "1.1.3",
      //      "com.typesafe.akka"            %% "akka-slf4j" % akkaVersion,

      Cinnamon.library.cinnamonPrometheus,
      Cinnamon.library.cinnamonPrometheusHttpServer,

      //      Cinnamon.library.cinnamonCHMetrics,

      Cinnamon.library.cinnamonAkka
    )
  )

