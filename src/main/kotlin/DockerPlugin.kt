import org.gradle.api.Plugin
import org.gradle.api.Project
import rule.DockerImageRule

class DockerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("java")
        project.pluginManager.apply(DockerImageRule::class.java)
    }
}