name := "urls"

version := "1.0"

scalaVersion := "2.11.11"

mainClass := Some("HttpServer")

resolvers += "twitter-repo" at "https://maven.twttr.com"

libraryDependencies += "com.twitter" % "finagle-http_2.11" % "6.45.0"
libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.2.2"

enablePlugins(JavaAppPackaging)
