package com.github.lion7.httpklient.soap

import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import javax.xml.soap.SOAPMessage

fun SOAPMessage.mimeContentType() = mimeHeaders.getHeader(HttpHeaders.CONTENT_TYPE)?.firstOrNull()?.let { HttpHeaders.ValueWithParameters.parse(it) }

fun SOAPMessage.soapPartContentType() = soapPart.getMimeHeader(HttpHeaders.CONTENT_TYPE)?.firstOrNull()?.let { HttpHeaders.ValueWithParameters.parse(it) }

fun HttpHeaders.ValueWithParameters.isXOPPackage(): Boolean = value.equals(MediaTypes.APPLICATION_XOP_XML, true) ||
    value.equals(MediaTypes.MULTIPART_RELATED, true) && parameters["type"]?.equals(MediaTypes.APPLICATION_XOP_XML, true) == true
