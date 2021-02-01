package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream
import javax.xml.bind.JAXBContext

class XmlBodyReader<T : Any>(
    private val c: Class<T>,
    private val jaxbContext: JAXBContext = JAXBContext.newInstance(c),
    override val accept: String = MediaTypes.APPLICATION_XML_UTF_8
) : BodyReader<T> {

    override fun <S : InputStream> read(response: HttpResponse<S>): T =
        c.cast(jaxbContext.createUnmarshaller().unmarshal(response.body))
}
