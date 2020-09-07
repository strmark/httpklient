package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.Headers

interface Part {
    val name: String
    val content: BodyWriter
    val headers: Headers
}