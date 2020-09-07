package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.Headers
import java.io.InputStream
import javax.xml.bind.JAXBContext

class XmlBodyReader<T : Any>(
    private val c: Class<T>,
    private val jaxbContext: JAXBContext = JAXBContext.newInstance(c)
) : BodyReader<T> {

    override val accept: String = "application/xml; charset=\"UTF-8\""

    override fun read(statusCode: Int, headers: Headers, inputStream: InputStream): T =
        c.cast(inputStream.use { jaxbContext.createUnmarshaller().unmarshal(it) })
}