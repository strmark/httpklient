package com.github.lion7.httpklient.impl

import com.github.lion7.httpklient.HttpKlientOptions
import com.github.lion7.httpklient.HttpRequest
import java.net.InetSocketAddress
import java.net.Socket

class SocketHttpKlient(override val options: HttpKlientOptions) : AbstractRawHttpKlient() {

    override fun connect(request: HttpRequest): ConnectionInfo {
        val socket = Socket()
        socket.soTimeout = options.readTimeout.toMillis().toInt()
        socket.connect(InetSocketAddress(request.uri.host, request.uri.port), options.connectTimeout.toMillis().toInt())
        return ConnectionInfo(socket.getOutputStream(), socket.getInputStream(), socket)
    }
}
