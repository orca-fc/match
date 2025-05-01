package com.orca.match.external.redis

import com.orca.match.exception.BaseException
import com.orca.match.exception.ErrorCode
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    val serviceName: String = "club"

    suspend fun get(key: String): String {
        return redisTemplate.opsForValue().get(key).awaitSingleOrNull()
            ?: throw BaseException(ErrorCode.REDIS_KEY_NOT_FOUND)
    }

    suspend fun set(key: String, value: String, duration: Duration) {
        redisTemplate.opsForValue().set(key, value, duration).awaitSingle()
    }

    suspend fun getByServiceName(prefix: String, key: String): String? {
        return redisTemplate.opsForValue().get("${serviceName}:${prefix}:${key}").awaitSingleOrNull()
            ?: throw BaseException(ErrorCode.REDIS_KEY_NOT_FOUND)
    }

    suspend fun setByServiceName(prefix: String, key: String, value: String, duration: Duration = Duration.ofDays(1)) {
        redisTemplate.opsForValue().set("${serviceName}:${prefix}:${key}", value, duration).awaitSingle()
    }

    suspend fun deleteByServiceName(prefix: String, key: String) {
        redisTemplate.delete("${serviceName}:${prefix}:${key}").awaitSingle()
    }
}