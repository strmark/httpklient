package com.github.lion7.httpklient

import java.util.Base64
import java.util.LinkedList
import java.util.TreeMap

class HttpHeaders(initialHeaders: Map<String, List<String>> = emptyMap()) : TreeMap<String, LinkedList<String>>(String.CASE_INSENSITIVE_ORDER) {

    init {
        initialHeaders.forEach { (name, value) -> header(name, value) }
    }

    fun accept(value: String) = header("Accept", value)
    fun authorization(f: Authorization.() -> String) = header("Authorization", Authorization.f())
    fun contentDisposition(value: String, parameters: Map<String, String> = emptyMap()) =
        header(
            "Content-Disposition",
            value + parameters.map { (key, value) -> "${key}=\"${value}\"" }.joinToString("; ", "; ")
        )

    fun contentType(value: String) = header("Content-Type", value)
    fun header(name: String, value: String, append: Boolean = false) = header(name, listOf(value), append)
    fun header(name: String, values: List<String>, append: Boolean = false): HttpHeaders = apply {
        if(append && containsKey(name)) {
            getValue(name).addAll(values)
        } else {
            put(name, LinkedList(values))
        }
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