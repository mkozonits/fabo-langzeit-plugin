package com.mgvpri.fabo.langzeit.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object FolderUtils {
    fun deleteFolder(folder: String) {
        if (Files.exists(Paths.get(folder))) {
            try {
                Files.walk(Paths.get(folder)).sorted(Comparator.reverseOrder())
                    .map { obj: Path -> obj.toFile() }
                    .forEach { obj: File -> obj.delete() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun copyFolder(sourceFolder: File, destinationFolder: File) {
        if (sourceFolder.isDirectory()) {
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir()
                println("Directory created :: $destinationFolder")
            }

            val files = sourceFolder.list()

            for (file in files!!) {
                val srcFile = File(sourceFolder, file)
                val destFile = File(destinationFolder, file)

                copyFolder(srcFile, destFile)
            }
        } else {
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING)
            println("File copied :: $destinationFolder")
        }
    }
}