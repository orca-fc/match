package com.orca.match.external.kafka

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.ssl.SslBundles
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfig {

    @Bean
    fun reactiveKafkaProducerTemplate(
        properties: KafkaProperties
        , sslBundles: SslBundles
    ): ReactiveKafkaProducerTemplate<String, Any> {

        val producerProps = properties.buildProducerProperties(sslBundles)
        val senderOptions = SenderOptions.create<String, Any>(producerProps)

        return ReactiveKafkaProducerTemplate(senderOptions)
    }
}