package com.github.lion7.httpklient.soap

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import java.io.InputStream
import javax.xml.bind.JAXBContext
import javax.xml.soap.SOAPMessage
import org.w3c.dom.Node

class SoapBodyReader<T : Any>(private val c: Class<T>, private val jaxbContext: JAXBContext, private val nodeExtractor: (SOAPMessage) -> Node, val mtomEnabled: Boolean) :
    BodyReader<T> {

    private val soapMessageBodyReader = SoapMessageBodyReader(mtomEnabled)

    override val accept: String = soapMessageBodyReader.accept

    override fun <S : InputStream> read(response: HttpResponse<S>): T {
        val message = soapMessageBodyReader.read(response)
        val unmarshaller = jaxbContext.createUnmarshaller()
        unmarshaller.attachmentUnmarshaller = MtomUnmarshaller(message)
        return c.cast(unmarshaller.unmarshal(nodeExtractor(message)))
    }
}
