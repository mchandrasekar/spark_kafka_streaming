name := "message-hub-receiver"

version := "1.6"

scalaVersion := "2.11.8"
ensimeScalaVersion in ThisBuild := "2.11.8"
EclipseKeys.withSource := true

lazy val core = RootProject(file("../message-hub-util"))

val main = Project(id = "application", base = file(".")).dependsOn(core)

libraryDependencies ++= {
  val sparkVersion =  "1.6.0"
  Seq(
   "org.apache.spark" % "spark-core_2.11" % "2.0.0",
   "org.apache.spark" % "spark-sql_2.11" % "2.0.0",
   "org.apache.spark" % "spark-streaming_2.11" % "2.0.0",
   "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.0.0",
   "org.apache.kafka" % "kafka-clients" % "0.10.0.0",
	"org.apache.kafka" % "kafka-log4j-appender" % "0.9.0.0",
	"org.codehaus.jackson" % "jackson-mapper-asl" % "1.5.0",
	"com.typesafe.play" % "play-json_2.11" % "2.4.8"
  )
}

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "spark", xs @ _*) => MergeStrategy.first
  case PathList("scala", xs @ _*) => MergeStrategy.discard
  case PathList("com", "ibm", "pixiedust", xs @ _*) => MergeStrategy.discard
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
