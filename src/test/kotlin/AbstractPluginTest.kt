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
