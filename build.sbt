/*
on cree nos propres definitions
entre (): defini l'aide qui est affichee pour la task
 */
val gitHeadCommitSha = taskKey[String]("Determine le SHA de l'actuel commit git")

val makeVersionProperties = taskKey[Seq[File]]("Cree un fichier version.properties")

name := "kittens"

//version := "1.0"

scalaVersion := "2.10.6"

/*
on modifie la structure du build du projet, en modifiant ce qui est a compiler ou non,
grace a sourceDirectory
 */
//sourceDirectory := new File(baseDirectory.value, "mesSourceeeees")

sourceDirectory in Compile := new File(sourceDirectory.value, "sourceCompile")

sourceDirectory in Test := new File(sourceDirectory.value, "test")

/*
on filtre selon que l'on veut inclure/exclure les source files
on peut scoper des projects, configurations et tasks avec la methode in
 */

// je veux que les .scala soient les fichiers qui soient compilés dans les unmanaged sources
includeFilter in (Compile, unmanagedSources) := "*.scala" // glob pattern

// pas de fichiers a exclure, y compris les fichiers cachés
excludeFilter in (Compile, unmanagedSources) := NothingFilter

/*
maven repository avec resolver
 */
resolvers += "Preowned Kitten Maven Repository" at "http://internal-repo.preowned-kittens.com"

/*
mappings: pour ajouter un fichier LICENSE au jar final, localisé dans LICENSE dans la base directory,
 avec pour nom PREOWNED-KITTEN-LICENSE
 */
mappings in packageBin in Compile += new File(baseDirectory.value, "LICENSE") -> "PREOWNED-KITTEN-LICENSE"


/*
on cree une fonction pour construire des sous-projets
 */
def preownedKittenProject(name: String): Project ={
  Project(name, file(name))
    .settings(
      version := "1.0",
      organization := "com.preownedkittens",
      //libraryDependencies += "org.specs2" % "specs2_2.10" % "1.14" % "test"
      // le %% permet de s'affranchir de la version de scala
      libraryDependencies += "org.specs2" %% "specs2" % "1.14" % "test"
    )
}

/*
 on cree nos propres tasks
  */

/*
 le git sha n'est pas specifique au common project, mais au build lui meme
 on l'attache donc au build: sbt executera la tache une fois pour toute:
 on a pas a le redefinir dans tous les projets qui l'utilisent
  */
gitHeadCommitSha in ThisBuild := Process("git rev-parse HEAD").lines.head

/*
on doit dire a sbt d'inclure le fichier version.properties dans le runtime classpath du site web
resourceGenerators est un Setting utilise pour dire qu'on genere des ressources
 */
//resourceGenerators in Compile <+= makeVersionProperties

/*
definition d'un autre sous projet dans sbt
Project(nomProjetDansConsoleSbt, file(localisationDuProjet))
il est recommande d'utiliser des lazy vals pour les projets (une val est executee lors de sa definition,
une lazy lors de son premier appel)
 */
lazy val common = {
  preownedKittenProject("common")
    .settings(
      makeVersionProperties := {
        val propFile = new File((resourceManaged in Compile).value, "version.properties")
        val content = "version=%s" format gitHeadCommitSha.value
        IO.write(propFile, content)
        Seq(propFile)
      }
    )
}

// les depdendances de projet sont definies grace a la methode dependsOn
lazy val analytics = {
  preownedKittenProject("analytics")
    .dependsOn(common)
    .settings()
}

lazy val website = {
  preownedKittenProject("website")
    .dependsOn(common)
    .settings()
}