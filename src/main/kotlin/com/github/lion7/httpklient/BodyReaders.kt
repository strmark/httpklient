package com.github.lion7.httpklient

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lion7.httpklient.readers.ByteArrayBodyReader
import com.github.lion7.httpklient.readers.DiscardingBodyReader
import com.github.lion7.httpklient.readers.FileBodyReader
import com.github.lion7.httpklient.readers.HttpResponseBodyReader
import com.github.lion7.httpklient.readers.InputStreamBodyReader
import com.github.lion7.httpklient.readers.JsonBodyReader
import com.github.lion7.httpklient.readers.MultipartBodyReader
import com.github.lion7.httpklient.readers.SoapBodyReader
import com.github.lion7.httpklient.readers.StringBodyReader
import com.github.lion7.httpklient.readers.XmlBodyReader
import javax.xml.bind.JAXBContext

object BodyReaders {
    fun discarding(accept: String = MediaTypes.ALL) = DiscardingBodyReader(accept)

    fun <T> ofHttpResponse(bodyReader: BodyReader<T>) = HttpResponseBodyReader(bodyReader)

    fun ofInputStream(accept: String = MediaTypes.ALL) = InputStreamBodyReader(accept)

    fun ofByteArray(accept: String = MediaTypes.ALL) = ByteArrayBodyReader(accept)

    fun ofString(accept: String = MediaTypes.ALL) = StringBodyReader(accept)

    fun ofFile(accept: String = MediaTypes.ALL) = FileBodyReader(accept)

    fun ofMultipart(accept: String = MediaTypes.MULTIPART_FORM_DATA) = MultipartBodyReader(accept)

    inline fun <reified V : Any> ofJson(objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(), accept: String = MediaTypes.APPLICATION_JSON_UTF_8) =
            JsonBodyReader(object : TypeReference<V>() {}, objectMapper, accept)

    inline fun <reified V : Any> ofJsonList(objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(), accept: String = MediaTypes.APPLICATION_JSON_UTF_8) =
            JsonBodyReader(object : TypeReference<List<V>>() {}, objectMapper, accept)

    inline fun <reified K : Any, reified V : Any> ofJsonMap(objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules(), accept: String = MediaTypes.APPLICATION_JSON_UTF_8) =
            JsonBodyReader(object : TypeReference<Map<K, V>>() {}, objectMapper, accept)

    inline fun <reified V : Any> ofXml(jaxbContext: JAXBContext = JAXBContext.newInstance(V::class.java), accept: String = MediaTypes.APPLICATION_XML_UTF_8) =
            XmlBodyReader(V::class.java, jaxbContext, accept)

    inline fun <reified V : Any> ofSoap(jaxbContext: JAXBContext = JAXBContext.newInstance(V::class.java), accept: String = MediaTypes.TEXT_XML_UTF_8) =
            SoapBodyReader(V::class.java, jaxbContext, accept)
}