package com.orca.match.external.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "external")
data class ServerConfig(
    val services: Map<String, ServerInfo> = emptyMap()
) {
    data class ServerInfo(
        val host: String,
        val port: Int
    )
}