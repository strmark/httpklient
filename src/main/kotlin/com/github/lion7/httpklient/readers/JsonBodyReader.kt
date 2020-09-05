package com.github.lion7.httpklient.readers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.lion7.httpklient.BodyReader
import java.io.InputStream

class JsonBodyReader<T : Any> private constructor(private val t: TypeReference<T>, private val objectMapper: ObjectMapper = jacksonObjectMapper()) :
    BodyReader<T> {

    companion object {
        fun <V: Any> list(objectMapper: ObjectMapper = jacksonObjectMapper()) = JsonBodyReader(object: TypeReference<List<V>>() {}, objectMapper)
        fun <K: Any, V: Any> map(objectMapper: ObjectMapper = jacksonObjectMapper()) = JsonBodyReader(object: TypeReference<Map<K, V>>() {}, objectMapper)
    }

    constructor(objectMapper: ObjectMapper = jacksonObjectMapper()): this(object: TypeReference<T>() {}, objectMapper)

    override val accept: String = "application/json; charset=\"UTF-8\""
    override fun read(inputStream: InputStream): T = inputStream.use { objectMapper.readValue(it, t) }
}