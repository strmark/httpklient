package com.github.lion7.httpklient.multipart

import com.github.lion7.httpklient.HttpHeaders
import java.io.InputStream

interface Part {
    val name: String
    val headers: HttpHeaders
    val content: InputStream
}