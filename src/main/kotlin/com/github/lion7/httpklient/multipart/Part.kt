package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.BodyWriter
import com.github.lion7.httpklient.HttpHeaders

interface Part {
    val name: String
    val headers: HttpHeaders
    val content: BodyWriter
}