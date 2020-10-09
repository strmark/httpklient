package com.github.lion7.httpklient.soap

import com.github.lion7.httpklient.HttpHeaders
import java.util.UUID
import javax.activation.DataHandler
import javax.xml.bind.attachment.AttachmentMarshaller
import javax.xml.soap.SOAPMessage

class MtomMarshaller(private val soapMessage: SOAPMessage) : AttachmentMarshaller() {

    override fun addMtomAttachment(data: DataHandler, elementNamespace: String, elementLocalName: String): String = addAttachmentPart(data)

    override fun addMtomAttachment(data: ByteArray, offset: Int, length: Int, mimeType: String, elementNamespace: String, elementLocalName: String): String =
        addAttachmentPart(DataHandler(data, mimeType))

    override fun addSwaRefAttachment(data: DataHandler): String = addAttachmentPart(data)

    override fun isXOPPackage(): Boolean = soapMessage.soapPartContentType()?.isXOPPackage() == true

    private fun addAttachmentPart(data: DataHandler): String {
        val contentId = UUID.randomUUID()
        val contentDisposition = HttpHeaders.ValueWithParameters("attachment", if (data.name != null) mapOf("filename" to data.name) else emptyMap())
        val attachmentPart = soapMessage.createAttachmentPart(data)
        attachmentPart.contentId = "<$contentId>"
        attachmentPart.contentType = data.contentType
        attachmentPart.addMimeHeader("Content-Transfer-Encoding", "binary")
        attachmentPart.addMimeHeader("Content-Disposition", contentDisposition.toString())
        soapMessage.addAttachmentPart(attachmentPart)
        return "cid:$contentId"
    }
}
