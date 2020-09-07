package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.OutputStream
import javax.xml.bind.JAXBContext

class XmlBodyWriter<T : Any>(
    private val element: T,
    private val jaxbContext: JAXBContext = JAXBContext.newInstance(element.javaClass)
) : BodyWriter {

    override val contentType: String = "application/xml; charset=\"UTF-8\""

    override fun write(outputStream: OutputStream) = jaxbContext.createMarshaller().marshal(element, outputStream)
}