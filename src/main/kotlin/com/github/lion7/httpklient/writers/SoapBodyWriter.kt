package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import java.io.OutputStream
import javax.xml.bind.JAXBContext
import javax.xml.soap.MessageFactory
import javax.xml.transform.dom.DOMResult

class SoapBodyWriter<T : Any>(
    private val body: T,
    private val jaxbContext: JAXBContext = JAXBContext.newInstance(body.javaClass)
) : BodyWriter {

    companion object {
        private val messageFactory: MessageFactory = MessageFactory.newInstance()
    }

    override val contentType: String = "text/xml; charset=\"UTF-8\""

    override fun write(outputStream: OutputStream) {
        val message = messageFactory.createMessage()
        jaxbContext.createMarshaller().marshal(body, DOMResult(message.soapBody))
        message.writeTo(outputStream)
    }
}