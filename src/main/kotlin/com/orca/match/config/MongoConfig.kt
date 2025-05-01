package com.orca.match.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import reactor.core.publisher.Mono

@EnableReactiveMongoAuditing
@Configuration
class MongoConfig {
    @Bean
    fun reactiveAuditorAware(): ReactiveAuditorAware<String> {
        return ReactiveAuditorAware { Mono.empty() }
    }

}