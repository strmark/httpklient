package com.github.lion7.httpklient.writers

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.soap.SoapMessageFactory
import javax.xml.bind.JAXBContext

class SoapBodyWriter<T : Any>(
    private val element: T,
    private val jaxbContext: JAXBContext = JAXBContext.newInstance(element.javaClass),
    private val mtomEnabled: Boolean = false
) : BodyWriter by SoapMessageBodyWriter(SoapMessageFactory.createMessage(element, jaxbContext, mtomEnabled))
