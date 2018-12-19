name := "google-places"

version := "0.1"

scalaVersion := "2.11.0"

libraryDependencies += "com.google.maps" % "google-maps-services" % "0.9.1"
libraryDependencies += "com.typesafe" % "config" % "1.2.1"

unmanagedResourceDirectories in Compile += { baseDirectory.value /"src/main/scala" }
