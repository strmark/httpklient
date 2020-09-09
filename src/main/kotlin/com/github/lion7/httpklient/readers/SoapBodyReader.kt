package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream
import javax.xml.bind.JAXBContext
import javax.xml.soap.MessageFactory
import javax.xml.soap.MimeHeaders

class SoapBodyReader<T : Any>(
        private val c: Class<T>,
        private val jaxbContext: JAXBContext = JAXBContext.newInstance(c),
        override val accept: String = MediaTypes.TEXT_XML_UTF_8
) : BodyReader<T> {

    companion object {
        private val messageFactory: MessageFactory = MessageFactory.newInstance()
    }

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): T {
        val mimeHeaders = MimeHeaders()
        headers.forEach { name, values -> values.forEach { mimeHeaders.addHeader(name, it) } }
        val message = inputStream.use { messageFactory.createMessage(mimeHeaders, it) }
        return c.cast(jaxbContext.createUnmarshaller().unmarshal(message.soapBody.firstChild))
    }
}