package com.github.lion7.httpklient

import java.time.Duration

data class Options(
    val connectTimeout: Duration = Duration.ofSeconds(10),
    val readTimeout: Duration = Duration.ofSeconds(10),
    val followRedirects: Boolean = true
)