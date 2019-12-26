package rule

import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.War
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.model.*
import task.DockerBuildImageTask
import task.DockerPushImageTask
import task.DockerRemoveImageTask
import java.io.File

@Suppress("UnstableApiUsage", "Unused")
class DockerImageRule : RuleSource() {

    @Model
    fun Docker.docker() = Unit

    @Defaults
    fun setDefaults(docker: Docker, @Path("tasks.jar") jarTask: Jar?) {
        val project = jarTask?.project

        docker.sourceContextPath = "docker"
        docker.contextDirectory = File(project?.buildDir, "docker")
        docker.relativeDockerfilePath = "."

        docker.tag = project?.version.toString()
        docker.host = "docker.pkg.github.com/f-u-z-z-l-e"
        docker.imageId = File(docker.contextDirectory, "imageid.txt")

        if (project?.pluginManager!!.hasPlugin("war")) {
            val warTask: War = project.tasks["war"] as War
            docker.artifactPath = warTask.project.relativePath(warTask.archiveFile.get().asFile)
            docker.artifactName = warTask.archiveFileName.get()
        } else {
            docker.artifactPath = jarTask.project.relativePath(jarTask.archiveFile.get().asFile)
            docker.artifactName = jarTask.archiveFileName.get()
        }
    }

    @Mutate
    fun ModelMap<Task>.createDockerBuildImageTask(docker: Docker) {

        create("dockerPrepareContext", Copy::class.java) {
            description = "Copies docker context source and the build artifact to docker context."
            group = "Docker Image Building"

            from(docker.sourceContextPath)
            from(docker.artifactPath)

            into(docker.contextDirectory)
        }

        create("dockerBuildImage", DockerBuildImageTask::class.java) {
            artifactName = docker.artifactName
            baseName = "${docker.host}/${project.group}/${project.name}"
            context = docker.contextDirectory
            imageIdFile = docker.imageId
            relativeDockerfilePath = docker.relativeDockerfilePath
            tag = docker.tag
        }

        get("dockerBuildImage")?.dependsOn("dockerPrepareContext")
    }

    @Mutate
    fun ModelMap<Task>.createDockerPushImageTask(docker: Docker) {
        create("dockerPushImage", DockerPushImageTask::class.java) {
            baseName = "${docker.host}/${project.group}/${project.name}"
        }
    }

    @Mutate
    fun ModelMap<Task>.createDockerRemoveImageTask(docker: Docker) {
        create("dockerRemoveImage", DockerRemoveImageTask::class.java) {
            baseName = "${docker.host}/${project.group}/${project.name}"
            imageIdFile = docker.imageId
        }
    }

}