package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.HttpHeaders
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

data class Part(
    val name: String,
    val headers: HttpHeaders,
    val content: InputStream
) {

    companion object {
        private const val FORM_DATA = "form-data"
        private const val NAME = "name"
        private const val FILENAME = "filename"

        fun ofFormField(name: String, value: String) = Part(name, HttpHeaders().contentDisposition(FORM_DATA, mapOf(NAME to name)), value.byteInputStream())

        fun ofFile(
            name: String,
            content: InputStream,
            contentType: String
        ) = Part(
            name, HttpHeaders().contentDisposition(FORM_DATA, mapOf(NAME to name)).contentType(contentType), content
        )

        fun ofFile(
            name: String,
            content: InputStream,
            filename: String,
            contentType: String
        ) = Part(
            name, HttpHeaders().contentDisposition(FORM_DATA, mapOf(NAME to name, FILENAME to filename)).contentType(contentType), content
        )

        fun ofFile(
            name: String,
            path: Path,
            filename: String = path.fileName.toString(),
            contentType: String = Files.probeContentType(path)
        ) = ofFile(name, Files.newInputStream(path), filename, contentType)
    }
}
