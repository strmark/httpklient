package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.Headers
import java.io.InputStream

/**
 * Returns the InputStream of the received HTTP response.
 * It is the caller's responsibility to close the stream!
 */
class InputStreamBodyReader(override val accept: String = "*/*") : BodyReader<InputStream> {

    override fun read(statusCode: Int, headers: Headers, inputStream: InputStream): InputStream = inputStream
}