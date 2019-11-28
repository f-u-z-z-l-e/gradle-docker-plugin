package rule

import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.model.*
import task.DockerBuildImageTask
import task.DockerPushImageTask
import task.DockerRemoveImageTask
import java.io.File

@Suppress("UnstableApiUsage", "Unused")
class DockerImageRule : RuleSource() {

    @Managed
    interface Docker {
        var contextDirectory: File
        var sourceContextPath: String
        var relativeDockerfilePath: String
        var artifactName: String
        var artifactPath: String
        var tag: String
        var host: String
        var imageId: File
    }

    @Model
    fun Docker.docker() = Unit

    @Defaults
    fun setDefaults(docker: Docker, @Path("tasks.jar") jarTask: Jar?) {
        if (jarTask != null) {
            // used by copy task
            docker.sourceContextPath = "docker"
            docker.contextDirectory = File("build/docker")
            docker.artifactPath = jarTask.project.relativePath(jarTask.archiveFile.get().asFile)

            // used by build task
            docker.relativeDockerfilePath = "."
            docker.artifactName = jarTask.archiveFileName.get()

            docker.tag = jarTask.project.version.toString()
            docker.host = "docker.pkg.github.com/f-u-z-z-l-e"
            docker.imageId = File(docker.contextDirectory, "imageid.txt")

        } else {
            println("Jar task not found! Unable to set default values for dockerBuildImage task!")
        }
    }

    @Mutate
    fun createDockerBuildImageTask(@Path("tasks") tasks: ModelMap<Task>, docker: Docker) {
        val project = tasks.get("jar")?.project

        tasks.create("dockerPrepareContext", Copy::class.java) {
            description = "Copies docker context source and the build artifact to docker context."
            group = "Docker Image Building"

            from(docker.sourceContextPath)
            from(docker.artifactPath)

            into(docker.contextDirectory)
        }

        tasks.create("dockerBuildImage", DockerBuildImageTask::class.java) {
            artifactName = docker.artifactName
            baseName = "${docker.host}/${project?.group}/${project?.name}"
            context = docker.contextDirectory
            imageIdFile = docker.imageId
            relativeDockerfilePath = docker.relativeDockerfilePath
            tag = docker.tag
        }

        tasks.get("dockerBuildImage")?.dependsOn("dockerPrepareContext")
    }

    @Mutate
    fun createDockerPushImageTask(@Path("tasks") tasks: ModelMap<Task>, docker: Docker) {
        val project = tasks.get("jar")?.project
        tasks.create("dockerPushImage", DockerPushImageTask::class.java) {
            baseName = "${docker.host}/${project?.group}/${project?.name}"
        }
    }

    @Mutate
    fun createDockerRemoveImageTask(@Path("tasks") tasks: ModelMap<Task>, docker: Docker) {
        val project = tasks.get("jar")?.project
        tasks.create("dockerRemoveImage", DockerRemoveImageTask::class.java) {
            baseName = "${docker.host}/${project?.group}/${project?.name}"
            imageIdFile = docker.imageId
        }
    }

}