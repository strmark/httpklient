package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import java.io.InputStream

/**
 * Returns the InputStream of the received HTTP response.
 * It is the caller's responsibility to close the stream!
 */
class InputStreamBodyReader(override val accept: String = "*/*") : BodyReader<InputStream> {

    override fun read(inputStream: InputStream): InputStream = inputStream
}