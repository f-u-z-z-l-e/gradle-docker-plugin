import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.*
import java.nio.file.Files

abstract class AbstractPluginTest {

    @TempDir
    lateinit var testDir: File

    lateinit var projectDir: File
    lateinit var buildFile: File

    @BeforeEach
    @Throws(Exception::class)
    fun setupProjectDirectories() {
        projectDir = Files.createDirectory(testDir.toPath().resolve("local")).toFile()
        createJavaProjectFiles()
        createDockerfile()
    }

    private fun createJavaProjectFiles() {
        val javaSourceDirectory = Files.createDirectories(projectDir.toPath().resolve("src/main/java")).toFile()
        val javaFile = Files.createFile(javaSourceDirectory.toPath().resolve("Main.java")).toFile()
        val javaFileContent = """
            |public class Main {
            |    public static void main(String[] args) {
            |        System.out.println("Running main class.");
            |    }
            |}
            |""".trimMargin()

        writeFile(javaFile,javaFileContent)
    }

    private fun createDockerfile() {
        val sourceContextPath = Files.createDirectory(projectDir.toPath().resolve("docker")).toFile()
        val dockerfile = Files.createFile(sourceContextPath.toPath().resolve("Dockerfile")).toFile()
        val artifactFileString = "\${ARTIFACT_FILE}"
        val javaOptsFile = "\${JAVA_OPTS}"
        val dockerfileContent = """
            |FROM openjdk:8-jdk-alpine
            |
            |EXPOSE 8080
            |
            |ARG ARTIFACT_FILE
            |COPY "$artifactFileString" app.jar
            |ENV JAVA_OPTS="-Xmx1g"
            |ENTRYPOINT ["sh","-c","java $javaOptsFile -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
            |""".trimMargin()

        writeFile(dockerfile, dockerfileContent)
    }

    @Throws(IOException::class)
    fun writeFile(destination: File, content: String) {
        BufferedWriter(FileWriter(destination)).use { it.write(content) }
    }

    fun InputStream.toFile(file: File) {
        use { input ->
            file.outputStream().use { input.copyTo(it) }
        }
    }

    fun GradleRunner.withJaCoCo(): GradleRunner {
        javaClass.classLoader.getResourceAsStream("testkit-gradle.properties").toFile(File(projectDir, "gradle.properties"))
        return this
    }

}
