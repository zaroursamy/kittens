/*
on cree nos propres definitions
entre (): defini l'aide qui est affichee pour la task
 */
val gitHeadCommitSha = taskKey[String]("Determine le SHA de l'actuel commit git")
val makeVersionProperties = taskKey[Seq[File]]("Cree un fichier version.properties")

name := "kittens"

version := "1.0"

scalaVersion := "2.10.6"

libraryDependencies += "org.specs2" % "specs2_2.10" % "1.14" % "test"

/*
 on cree nos propres tasks
  */
gitHeadCommitSha := Process("git rev-parse HEAD").lines.head

makeVersionProperties := {
  val propFile = new File((resourceManaged in Compile).value, "version.properties")
  val content = "version=%s" format gitHeadCommitSha.value
  IO.write(propFile, content)
  Seq(propFile)
}

/*
on doit dire a sbt d'inclure le fichier version.properties dans le runtime classpath du site web
resourceGenerators est un Setting utilise pour dire qu'on genere des ressources
 */
resourceGenerators in Compile <+= makeVersionProperties


/*
definition d'un autre sous projet dans sbt
Project(nomProjetDansConsoleSbt, file(localisationDuProjet))
 */
lazy val common = {
  Project("common", file("common")).settings(
    libraryDependencies += "org.specs2" % "specs2_2.10" % "1.14" % "test"
  )
}

// les depdendances de projet sont definies grace a la methode dependsOn
lazy val analytics = {
  Project("analytics", file("analytics"))
    .dependsOn(common)
    .settings()
}

lazy val website = {
  Project("website", file("website"))
    .dependsOn(common)
    .settings()
}