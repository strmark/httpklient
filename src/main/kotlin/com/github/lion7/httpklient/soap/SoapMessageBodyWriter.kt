package com.github.lion7.httpklient.soap

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.MediaTypes
import org.apache.commons.io.output.CountingOutputStream
import java.io.OutputStream
import javax.xml.soap.SOAPMessage

class SoapMessageBodyWriter(private val message: SOAPMessage) : BodyWriter {

    override val contentType: String = message.mimeHeaders.getHeader("Content-Type")?.firstOrNull() ?: MediaTypes.TEXT_XML

    override val contentLength: Long = CountingOutputStream(OutputStream.nullOutputStream()).use {
        write(it)
        it.byteCount
    }

    override fun write(outputStream: OutputStream) = message.writeTo(outputStream)
}
