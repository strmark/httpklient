package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import com.github.lion7.httpklient.multipart.Part
import java.io.InputStream

class MultipartBodyReader(override val accept: String = MediaTypes.MULTIPART_FORM_DATA) : BodyReader<List<Part>> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): List<Part> {
        // TODO
        return emptyList()
    }

}