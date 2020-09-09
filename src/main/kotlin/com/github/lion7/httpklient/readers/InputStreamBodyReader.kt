package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpHeaders
import com.github.lion7.httpklient.MediaTypes
import java.io.InputStream

/**
 * Returns the InputStream of the received HTTP response.
 * It is the caller's responsibility to close the stream!
 */
class InputStreamBodyReader(override val accept: String = MediaTypes.ALL) : BodyReader<InputStream> {

    override fun read(statusCode: Int, headers: HttpHeaders, inputStream: InputStream): InputStream = inputStream
}