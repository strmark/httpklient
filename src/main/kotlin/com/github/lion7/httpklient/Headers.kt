package com.github.lion7.httpklient

import java.util.*

class Headers(headers: Map<String, List<String>> = emptyMap()) : LinkedHashMap<String, LinkedList<String>>() {

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
    fun header(name: String, values: List<String>): Headers = apply {
        computeIfAbsent(name) { LinkedList() }.addAll(values)
    }

    fun headerIfAbsent(name: String, value: String) = headerIfAbsent(name, listOf(value))
    fun headerIfAbsent(name: String, values: List<String>): Headers = apply {
        putIfAbsent(name, LinkedList(values))
    }

    fun merge(headers: Map<String, String>) = apply {
        headers.forEach { (name, value) -> header(name, value) }
    }

    fun merge(headers: Headers) = apply {
        headers.forEach { (name, value) -> header(name, value) }
    }

    object Authorization {
        fun basic(username: String, password: String): String =
            "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())

        fun bearer(token: String) = "Bearer $token"
    }
}