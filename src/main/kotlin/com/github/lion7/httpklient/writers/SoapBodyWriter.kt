package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.MediaTypes
import java.io.OutputStream
import javax.xml.soap.SOAPMessage

class SoapBodyWriter(private val message: SOAPMessage) : BodyWriter {

    override val contentType: String = message.mimeHeaders.getHeader("Content-Type")?.firstOrNull() ?: MediaTypes.TEXT_XML

    override fun write(outputStream: OutputStream) = message.writeTo(outputStream)
}
