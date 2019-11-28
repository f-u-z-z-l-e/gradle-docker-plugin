package task

import org.gradle.api.tasks.*
import java.io.ByteArrayOutputStream
import java.io.File

open class DockerBuildImageTask : Exec() {

    private val imageIdFilename: String = "imageid.txt"

    @Input
    lateinit var tag: String

    @Input
    lateinit var baseName: String

    @Input
    lateinit var artifactName: String

    @Input
    lateinit var relativeDockerfilePath: String

    @InputDirectory
    lateinit var context: File

    @OutputFile
    lateinit var imageIdFile: File

    init {
        group = "Docker Image Building"
        description = "Builds a docker image, based on the provided Dockerfile."
    }

    @TaskAction
    override fun exec() {
        val latest = "$baseName:latest"
        val versionNumber = "$baseName:$tag"
        val stdout = ByteArrayOutputStream()

        workingDir = context
        commandLine = mutableListOf("docker", "build", "--iidfile", imageIdFilename, "--build-arg", "ARTIFACT_FILE=${artifactName}", "-t", versionNumber, "-t", latest, relativeDockerfilePath)
        standardOutput = stdout

        super.exec()
        logger.info("Output: $stdout")

        imageIdFile = File(context, imageIdFilename)
    }

}
