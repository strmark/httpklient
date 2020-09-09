package com.github.lion7.httpklient

import java.util.Base64
import java.util.LinkedList
import java.util.TreeMap

class HttpHeaders(headers: Map<String, List<String>> = emptyMap()) : TreeMap<String, LinkedList<String>>(String.CASE_INSENSITIVE_ORDER) {

    init {
        headers.forEach { (name, value) -> header(name, value) }
    }

    fun accept(value: String) = header("Accept", value)
    fun authorization(f: Authorization.() -> String) = header("Authorization", Authorization.f())
    fun contentDisposition(value: String, parameters: Map<String, String> = emptyMap()) =
        header(
            "Content-Disposition",
            value + parameters.map { (key, value) -> "${key}=\"${value}\"" }.joinToString("; ", "; ")
        )

    fun contentType(value: String) = header("Content-Type", value)
    fun header(name: String, value: String) = header(name, listOf(value))
    fun header(name: String, values: List<String>): HttpHeaders = apply {
        computeIfAbsent(name) { LinkedList() }.addAll(values)
    }

    fun merge(headers: Map<String, String>) = apply {
        headers.forEach { (name, value) -> header(name, value) }
    }

    fun merge(headers: HttpHeaders) = apply {
        headers.forEach { (name, value) -> header(name, value) }
    }

    object Authorization {
        fun basic(username: String, password: String): String =
            "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())

        fun bearer(token: String) = "Bearer $token"
    }
}