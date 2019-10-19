val commercialCredentialsFile = Path.userHome / ".lightbend" / "commercial.credentials"

if (commercialCredentialsFile.exists())
  Seq(
    addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.12.0"),
//    addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.13.0-SNAPSHOT"),
    credentials += Credentials(commercialCredentialsFile),
    resolvers += Resolver.url("lightbend-commercial", url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
  )
else Nil
