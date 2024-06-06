name := "awsseract"

version := "1.0-SNAPSHOT"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.16",
    "com.typesafe.akka" %% "akka-remote" % "2.2.3",
    "com.typesafe.akka" %% "akka-kernel" % "2.2.3",
    "com.typesafe.akka" %% "akka-slf4j" % "2.2.3",
  javaJdbc,
  javaEbean,
  cache
)

unmanagedJars in Compile := (baseDirectory.value / "lib" ** "*.jar").classpath

play.Project.playJavaSettings
