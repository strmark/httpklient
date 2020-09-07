package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.File
import java.io.OutputStream
import java.nio.file.*

class FileBodyWriter(
    private val path: Path,
    override val contentType: String = "application/octet-stream"
) : BodyWriter {

    companion object {
        fun classpath(
            name: String,
            resolvingClass: Class<*> = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass
        ): FileBodyWriter {
            val uri = resolvingClass.getResource(name).toURI()
            val path = try {
                Paths.get(uri)
            } catch (e: FileSystemNotFoundException) {
                FileSystems.newFileSystem(uri, emptyMap<String, Any>())
                Paths.get(uri)
            }
            return FileBodyWriter(path)
        }
    }

    constructor(file: File) : this(file.toPath())

    override fun write(outputStream: OutputStream) {
        Files.copy(path, outputStream)
    }
}