package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import com.github.lion7.httpklient.soap.MtomUnmarshaller
import org.w3c.dom.Node
import java.io.InputStream
import javax.xml.bind.JAXBContext
import javax.xml.soap.MessageFactory
import javax.xml.soap.MimeHeaders
import javax.xml.soap.SOAPMessage

class SoapBodyReader<T : Any>(
    private val c: Class<T>,
    private val jaxbContext: JAXBContext = JAXBContext.newInstance(c),
    private val nodeExtractor: (SOAPMessage) -> Node = { it.soapBody.firstChild }
) : BodyReader<T> {

    companion object {
        private val messageFactory: MessageFactory = MessageFactory.newInstance()
    }

    override val accept: String = listOf(MediaTypes.MULTIPART_RELATED, MediaTypes.TEXT_XML).joinToString()

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): T {
        val mimeHeaders = MimeHeaders()
        headers.forEach { name, values -> values.forEach { value -> mimeHeaders.addHeader(name, value.toString()) } }
        val message = messageFactory.createMessage(mimeHeaders, inputStream)
        val unmarshaller = jaxbContext.createUnmarshaller()
        unmarshaller.attachmentUnmarshaller = MtomUnmarshaller(message)
        return c.cast(unmarshaller.unmarshal(nodeExtractor(message)))
    }
}
