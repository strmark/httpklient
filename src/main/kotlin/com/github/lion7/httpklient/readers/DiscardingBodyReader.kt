package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream

class DiscardingBodyReader(override val accept: String = MediaTypes.ALL) : BodyReader<Unit> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream) {
        // do nothing
    }
}
