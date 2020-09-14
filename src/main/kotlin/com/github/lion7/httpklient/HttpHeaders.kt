package com.github.lion7.httpklient

import java.util.Base64
import java.util.LinkedList
import java.util.TreeMap

class HttpHeaders(initialHeaders: HttpHeaders? = null) : TreeMap<String, LinkedList<HttpHeaders.ValueWithParameters>>(String.CASE_INSENSITIVE_ORDER) {

    init {
        initialHeaders?.let(this::putAll)
    }

    fun accept(value: String) = header("Accept", value)
    fun authorization(f: Authorization.() -> String) = header("Authorization", Authorization.f())
    fun contentDisposition(value: String, parameters: Map<String, String> = emptyMap()) = header(
            "Content-Disposition",
            value + parameters.map { (key, value) -> "${key}=\"${value}\"" }.joinToString("; ", "; ")
    )

    fun contentType(value: String) = header("Content-Type", value)

    fun header(name: String, value: String, append: Boolean = false) = header(name, listOf(value), append)
    fun header(name: String, values: List<String>, append: Boolean = false): HttpHeaders = apply {
        val newValues = values.map(ValueWithParameters::parse)
        if (append && containsKey(name)) {
            getValue(name).addAll(newValues)
        } else {
            put(name, LinkedList(newValues))
        }
    }

    fun mergeMap(headers: Map<String, String>, append: Boolean = false) = apply {
        headers.forEach { (name, value) -> header(name, value, append) }
    }

    fun mergeMultiMap(headers: Map<String, List<String>>, append: Boolean = false) = apply {
        headers.forEach { (name, value) -> header(name, value, append) }
    }

    data class ValueWithParameters(
            val value: String,
            val parameters: Map<String, String?>
    ) {

        companion object {
            fun parse(s: String): ValueWithParameters {
                val parameters = if (s.contains(';')) s.substringAfter(';').split(';').associate {
                    val key = it.substringBefore('=').trim()
                    val value = if (it.contains('=')) it.substringAfter('=').trim('"') else null
                    key to value
                } else emptyMap()
                val value = s.substringBefore(';').trim()
                return ValueWithParameters(value, parameters)
            }
        }

        override fun toString(): String {
            return value + parameters.entries.joinToString("; ", "; ") { (key, value) -> key + if (value != null) "=\"$value\"" else "" }
        }
    }

    object Authorization {
        fun basic(username: String, password: String): String =
                "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())

        fun bearer(token: String) = "Bearer $token"
    }
}