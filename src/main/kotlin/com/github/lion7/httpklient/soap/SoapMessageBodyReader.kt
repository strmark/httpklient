package com.github.lion7.httpklient.soap

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import com.github.lion7.httpklient.MediaTypes
import javax.xml.soap.MessageFactory
import javax.xml.soap.MimeHeaders
import javax.xml.soap.SOAPMessage
import java.io.InputStream

class SoapMessageBodyReader(mtomEnabled: Boolean) : BodyReader<SOAPMessage> {

    companion object {
        private val messageFactory: MessageFactory = MessageFactory.newInstance()
    }

    override val accept: String = if (mtomEnabled) listOf(MediaTypes.MULTIPART_RELATED, MediaTypes.TEXT_XML).joinToString() else MediaTypes.TEXT_XML

    override fun <S : InputStream> read(response: HttpResponse<S>): SOAPMessage {
        val mimeHeaders = MimeHeaders()
        response.headers.forEach { name, values -> values.forEach { value -> mimeHeaders.addHeader(name, value.toString()) } }
        return messageFactory.createMessage(mimeHeaders, response.body)
    }
}
