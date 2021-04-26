package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import java.io.InputStream
import javax.xml.bind.JAXBContext

class XmlBodyReader<T : Any>(private val c: Class<T>, private val jaxbContext: JAXBContext, override val accept: String) : BodyReader<T> {

    override fun <S : InputStream> read(response: HttpResponse<S>): T =
        c.cast(jaxbContext.createUnmarshaller().unmarshal(response.body))
}
