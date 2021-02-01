package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
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
        private val soapMessageBodyReader = SoapMessageBodyReader
    }

    override val accept: String = soapMessageBodyReader.accept

    override fun <S : InputStream> read(response: HttpResponse<S>): T {
        val message = soapMessageBodyReader.read(response)
        val unmarshaller = jaxbContext.createUnmarshaller()
        unmarshaller.attachmentUnmarshaller = MtomUnmarshaller(message)
        return c.cast(unmarshaller.unmarshal(nodeExtractor(message)))
    }
}
