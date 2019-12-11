import org.gradle.testkit.runner.GradleRunner
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files

class DockerPluginTest : AbstractPluginTest() {

    @BeforeEach
    @Throws(Exception::class)
    fun setup() {
        Assertions.assertTrue(Files.isDirectory(projectDir.toPath()))
        buildFile = Files.createFile(projectDir.toPath().resolve("build.gradle")).toFile()
    }

    @Test
    @Throws(Exception::class)
    fun `Apply plugin to project`() {
        // given
        val eol = System.getProperty("line.separator")
        val buildFileContent = "plugins {  id('ch.fuzzle.gradle.docker-plugin')}$eol"

        writeFile(buildFile, buildFileContent)

        // when
        val result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments("tasks")
                .forwardOutput()
                .withJaCoCo()
                .build()

        // then
        MatcherAssert.assertThat(result.output, Matchers.containsString("dockerPrepareContext"))
        MatcherAssert.assertThat(result.output, Matchers.containsString("dockerBuildImage"))
        MatcherAssert.assertThat(result.output, Matchers.containsString("dockerPushImage"))
        MatcherAssert.assertThat(result.output, Matchers.containsString("dockerRemoveImage"))
        MatcherAssert.assertThat(result.output, Matchers.containsString("1 actionable task: 1 executed"))
        MatcherAssert.assertThat(result.output, Matchers.containsString("BUILD SUCCESSFUL"))
    }
}