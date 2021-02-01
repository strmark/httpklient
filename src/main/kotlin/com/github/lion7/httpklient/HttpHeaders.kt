package com.github.lion7.httpklient

import java.net.URI
import java.util.Base64
import java.util.LinkedList
import java.util.TreeMap

class HttpHeaders(initialHeaders: HttpHeaders? = null) : TreeMap<String, LinkedList<HttpHeaders.ValueWithParameters>>(String.CASE_INSENSITIVE_ORDER) {

    init {
        initialHeaders?.let(this::putAll)
    }

    fun host(uri: URI) {
        val host = when {
            uri.port == -1 -> uri.host
            uri.scheme == "http" && uri.port == 80 -> uri.host
            uri.scheme == "https" && uri.port == 443 -> uri.host
            uri.scheme == "ftp" && uri.port == 21 -> uri.host
            else -> "${uri.host}:${uri.port}"
        }
        header("Host", host)
    }

    fun accept(value: String) = header("Accept", value)

    fun authorization(f: Authorization.() -> String) = header("Authorization", ValueWithParameters(Authorization.f(), emptyMap()))

    fun connection(value: String) = header("Connection", value)

    fun contentDisposition(value: String, parameters: Map<String, String> = emptyMap()) = header("Content-Disposition", ValueWithParameters(value, parameters))

    fun contentType(value: String) = header("Content-Type", ValueWithParameters.parse(value))

    fun contentType(value: String, parameters: Map<String, String>) = header("Content-Type", ValueWithParameters(value, parameters))

    fun header(name: String, value: String, append: Boolean = false) = header(name, ValueWithParameters.parse(value), append)

    fun header(name: String, value: ValueWithParameters, append: Boolean = false) = header(name, listOf(value), append)

    fun header(name: String, values: List<ValueWithParameters>, append: Boolean = false): HttpHeaders = apply {
        if (append && containsKey(name)) {
            getValue(name).addAll(values)
        } else {
            put(name, LinkedList(values))
        }
    }

    fun mergeMap(headers: Map<String, String>, append: Boolean = false) = apply {
        headers.forEach { (name, value) -> header(name, value, append) }
    }

    fun mergeMultiMap(headers: Map<String, List<String>>, append: Boolean = false) = apply {
        headers.forEach { (name, values) -> header(name, values.map(ValueWithParameters::parse), append) }
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

            // see https://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html for the original list
            private val separators = listOf('(', ')', '<', '>', '@', ',', ';', ':', '\\', '"', '/', '[', ']', '?', '=', '{', '}')
            private fun needsQuotes(c: Char) = c.isISOControl() || c.isWhitespace() || separators.contains(c)
            private fun String.addPrefix(prefix: String) = prefix + this
            private fun String.escapeDoubleQuotes() = replace("\"", "\\\"")
            private fun String.quoteIfNecessary() = if (any(::needsQuotes)) '"' + this.escapeDoubleQuotes() + '"' else this
        }

        override fun toString(): String = if (parameters.isEmpty()) {
            value
        } else {
            value + parameters.map { (key, value) -> key + (value?.quoteIfNecessary()?.addPrefix("=") ?: "") }.joinToString("; ", "; ")
        }
    }

    object Authorization {
        fun basic(username: String, password: String): String =
            "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())

        fun bearer(token: String) = "Bearer $token"
    }
}
