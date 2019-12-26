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
        buildFile = Files.createFile(projectDir.toPath().resolve("build.gradle.kts")).toFile()
    }

    @Test
    @Throws(Exception::class)
    fun `Apply plugin to project`() {
        // given
        val buildFileContent = """
            |plugins {
            |  id ("ch.fuzzle.gradle.docker-plugin")
            |}
            |
            |group = "ch.fuzzle"
            |
            |version = "1.0.0"
            |""".trimMargin()

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

    @Test
    @Throws(Exception::class)
    fun `Build docker image and remove it afterwards with jar`() {
        // given
        val buildFileContent = """
            |plugins {
            |  id ("ch.fuzzle.gradle.docker-plugin")
            |}
            |
            |group = "ch.fuzzle"
            |
            |version = "1.0.0"
            |""".trimMargin()

        writeFile(buildFile, buildFileContent)

        // when
        val result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments("build", "dockerBuildImage", "dockerRemoveImage")
                .forwardOutput()
                .withJaCoCo()
                .build()

        // then
        MatcherAssert.assertThat(result.output, Matchers.containsString("5 actionable tasks: 5 executed"))
        MatcherAssert.assertThat(result.output, Matchers.containsString("BUILD SUCCESSFUL"))
    }

    @Test
    @Throws(Exception::class)
    fun `Build docker image and remove it afterwards with war`() {
        // given
        val buildFileContent = """
            |plugins {
            |  id ("war")
            |  id ("ch.fuzzle.gradle.docker-plugin")
            |}
            |
            |group = "ch.fuzzle"
            |
            |version = "1.0.0"
            |""".trimMargin()

        writeFile(buildFile, buildFileContent)

        // when
        val result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments("build", "dockerBuildImage", "dockerRemoveImage")
                .forwardOutput()
                .withJaCoCo()
                .build()

        // then
        MatcherAssert.assertThat(result.output, Matchers.containsString("5 actionable tasks: 5 executed"))
        MatcherAssert.assertThat(result.output, Matchers.containsString("BUILD SUCCESSFUL"))
    }

}