package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.MediaTypes
import java.io.OutputStream
import javax.xml.bind.JAXBContext
import javax.xml.validation.Schema

class XmlBodyWriter<T : Any>(
    private val element: T,
    private val jaxbContext: JAXBContext = JAXBContext.newInstance(element.javaClass),
    private val schema: Schema? = null,
    override val contentType: String = MediaTypes.APPLICATION_XML_UTF_8
) : BodyWriter {

    override fun write(outputStream: OutputStream) {
        val marshaller = jaxbContext.createMarshaller()
        if (schema != null) marshaller.schema = schema
        return marshaller.marshal(element, outputStream)
    }
}
