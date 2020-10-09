package com.github.lion7.httpklient.soap

import java.net.URLDecoder
import java.nio.charset.StandardCharsets.UTF_8
import javax.activation.DataHandler
import javax.xml.bind.attachment.AttachmentUnmarshaller
import javax.xml.soap.AttachmentPart
import javax.xml.soap.SOAPMessage

class MtomUnmarshaller(private val soapMessage: SOAPMessage) : AttachmentUnmarshaller() {
    override fun getAttachmentAsDataHandler(cid: String): DataHandler = findAttachment(cid).dataHandler

    override fun getAttachmentAsByteArray(cid: String): ByteArray = findAttachment(cid).rawContentBytes

    override fun isXOPPackage(): Boolean = soapMessage.soapPartContentType()?.isXOPPackage() == true

    private fun findAttachment(cid: String): AttachmentPart {
        val cidToSearch: String = URLDecoder.decode(cid.removePrefix("cid:"), UTF_8)
        return soapMessage.attachments.asSequence().firstOrNull {
            val attachmentCid = it.contentId.trim('<', '>')
            attachmentCid == cidToSearch
        } ?: throw IllegalArgumentException("Attachment with cid '$cidToSearch' not found!")
    }
}
