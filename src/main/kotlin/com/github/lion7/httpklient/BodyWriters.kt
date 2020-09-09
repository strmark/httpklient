package com.github.lion7.httpklient

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lion7.httpklient.multipart.Part
import com.github.lion7.httpklient.writers.ByteArrayBodyWriter
import com.github.lion7.httpklient.writers.FileBodyWriter
import com.github.lion7.httpklient.writers.InputStreamBodyWriter
import com.github.lion7.httpklient.writers.JsonBodyWriter
import com.github.lion7.httpklient.writers.MultipartBodyWriter
import com.github.lion7.httpklient.writers.SoapBodyWriter
import com.github.lion7.httpklient.writers.StringBodyWriter
import com.github.lion7.httpklient.writers.XmlBodyWriter
import java.io.File
import java.io.InputStream
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.bind.JAXBContext

object BodyWriters {
    fun ofInputStream(inputStream: InputStream, contentType: String = MediaTypes.APPLICATION_OCTET_STREAM) = InputStreamBodyWriter(inputStream, contentType)

    fun ofByteArray(bytes: ByteArray, contentType: String = MediaTypes.APPLICATION_OCTET_STREAM) = ByteArrayBodyWriter(bytes, contentType)

    fun ofString(string: String, contentType: String = MediaTypes.TEXT_PLAIN) = StringBodyWriter(string, contentType)

    fun ofPath(path: Path, contentType: String = MediaTypes.APPLICATION_OCTET_STREAM) = FileBodyWriter(path, contentType)

    fun ofFile(file: File, contentType: String = MediaTypes.APPLICATION_OCTET_STREAM) = FileBodyWriter(file.toPath(), contentType)

    fun ofClasspathResource(name: String,
                            contentType: String = MediaTypes.APPLICATION_OCTET_STREAM,
                            resolvingClass: Class<*> = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass): FileBodyWriter {
        val uri = resolvingClass.getResource(name).toURI()
        val path = try {
            Paths.get(uri)
        } catch (e: FileSystemNotFoundException) {
            FileSystems.newFileSystem(uri, emptyMap<String, Any>())
            Paths.get(uri)
        }
        return FileBodyWriter(path, contentType)
    }

    fun ofMultipart(vararg parts: Part) = MultipartBodyWriter(*parts)

    inline fun <reified V : Any> ofJson(value: V, objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(), contentType: String = MediaTypes.APPLICATION_JSON_UTF_8) =
            JsonBodyWriter(value, objectMapper, contentType)

    inline fun <reified V : Any> ofXml(element: V, jaxbContext: JAXBContext = JAXBContext.newInstance(V::class.java), contentType: String = MediaTypes.APPLICATION_XML_UTF_8) =
            XmlBodyWriter(element, jaxbContext, contentType)

    inline fun <reified V : Any> ofSoap(element: V, jaxbContext: JAXBContext = JAXBContext.newInstance(V::class.java), contentType: String = MediaTypes.TEXT_XML_UTF_8) =
            SoapBodyWriter(element, jaxbContext, contentType)
}