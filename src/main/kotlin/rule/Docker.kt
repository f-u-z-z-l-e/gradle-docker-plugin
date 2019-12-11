package rule

import org.gradle.model.Managed
import java.io.File

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