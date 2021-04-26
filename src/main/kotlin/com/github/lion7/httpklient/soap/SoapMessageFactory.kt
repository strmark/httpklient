package com.github.lion7.httpklient.soap

import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import javax.xml.bind.JAXBContext
import javax.xml.soap.MessageFactory
import javax.xml.soap.SOAPMessage
import javax.xml.transform.dom.DOMResult

object SoapMessageFactory {

    private val messageFactory: MessageFactory = MessageFactory.newInstance()

    fun createEmptyMessage(): SOAPMessage = messageFactory.createMessage()

    fun <T : Any> createMessage(
        body: T,
        jaxbContext: JAXBContext = JAXBContext.newInstance(body.javaClass),
        mtomEnabled: Boolean = false
    ): SOAPMessage {
        val message = messageFactory.createMessage()
        val marshaller = jaxbContext.createMarshaller()
        if (mtomEnabled) {
            val soapPartContentType = message.soapPartContentType()
            val soapPartXopContentType = HttpHeaders.ValueWithParameters(MediaTypes.APPLICATION_XOP_XML, mapOf("charset" to "UTF-8", "type" to (soapPartContentType?.value ?: MediaTypes.TEXT_XML)))
            message.soapPart.setMimeHeader(HttpHeaders.CONTENT_TYPE, soapPartXopContentType.toString())

            val mimeXopContentType = HttpHeaders.ValueWithParameters(MediaTypes.MULTIPART_RELATED, mapOf("type" to soapPartXopContentType.value, "start-info" to soapPartXopContentType.parameters["type"]))
            message.mimeHeaders.setHeader(HttpHeaders.CONTENT_TYPE, mimeXopContentType.toString())

            marshaller.attachmentMarshaller = MtomMarshaller(message)
        }
        marshaller.marshal(body, DOMResult(message.soapBody))
        if (message.saveRequired()) {
            message.saveChanges()
        }
        return message
    }
}
