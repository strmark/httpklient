package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.BodyWriter

interface Part {
    val name: String
    val content: BodyWriter
    val headers: Map<String, String>
}