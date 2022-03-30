package com.github.lion7.httpklient.readers

import com.github.lion7.httpklient.BodyReader
import com.github.lion7.httpklient.HttpResponse
import java.io.InputStream

class DiscardingBodyReader(override val accept: String) : BodyReader<Unit?> {

    override fun <S : InputStream> read(response: HttpResponse<S>): Unit? = null
}
