package task

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream

class DockerPushImageTask : Exec() {

    @Input
    lateinit var baseName: String

    init {
        group = "Docker Image Building"
        description = "Pushes a docker image to the currently logged in repository."
    }

    @TaskAction
    override fun exec() {
        val stdout = ByteArrayOutputStream()

        commandLine = mutableListOf("docker", "push", baseName)
        standardOutput = stdout

        super.exec()
        logger.info("Output: $stdout")
    }

}