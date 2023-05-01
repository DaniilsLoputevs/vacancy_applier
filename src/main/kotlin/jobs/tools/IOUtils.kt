package jobs.tools

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


fun sureExistsFilePath(filePath: String): String =
    filePath.also { File(filePath).apply { if (!this.exists()) Files.createDirectories(Paths.get(filePath)) } }
