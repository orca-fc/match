package com.orca.match.external.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientFactory(
    serverConfig: ServerConfig
) {
    private val clients = serverConfig.services.mapValues { (name, info) ->
        WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .baseUrl("http://${info.host}:${info.port}/$name")
            .build()
    }

    fun getClient(name: String): WebClient {
        return checkNotNull(clients[name]) { "Undefined service name: $name" }
    }
}


