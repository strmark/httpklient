package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.OutputStream
import javax.xml.bind.JAXBContext
import javax.xml.validation.Schema
import org.apache.commons.io.output.CountingOutputStream

class XmlBodyWriter<T : Any>(private val element: T, private val jaxbContext: JAXBContext, private val schema: Schema?, override val contentType: String) : BodyWriter {

    override val contentLength: Long = CountingOutputStream(OutputStream.nullOutputStream()).use {
        write(it)
        it.byteCount
    }

    override fun write(outputStream: OutputStream) {
        val marshaller = jaxbContext.createMarshaller()
        if (schema != null) marshaller.schema = schema
        return marshaller.marshal(element, outputStream)
    }
}
