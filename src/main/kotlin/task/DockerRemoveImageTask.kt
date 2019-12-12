package task

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.File

open class DockerRemoveImageTask : Exec() {

    @Input
    lateinit var baseName: String

    @InputFile
    lateinit var imageIdFile: File

    init {
        group = "Docker Image Building"
        description = "Removes the recent image from the host it was built on."
    }

    @TaskAction
    override fun exec() {
        val stdout = ByteArrayOutputStream()
        val imageId = imageIdFile.readText().substring(7)

        commandLine = mutableListOf("docker", "rmi", "-f", imageId)
        standardOutput = stdout

        super.exec()
        logger.info("Output: $stdout")
    }

}