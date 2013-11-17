name := "awseract-distributed"

organization := "nl.tudelft.in4392.cloudcomputing"

version := "1.0"

scalaVersion := "2.10.3"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "com.typesafe.akka" %% "akka-remote" % "2.2.3",
  "com.typesafe.akka" %% "akka-kernel" % "2.2.3",
  "com.typesafe.akka" %% "akka-slf4j" % "2.2.3"
)

unmanagedJars in Compile <<= baseDirectory map { base => (base ** "*.jar").classpath }

 
