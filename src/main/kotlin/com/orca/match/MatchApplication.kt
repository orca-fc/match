package com.orca.match

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MatchApplication

suspend fun main(args: Array<String>) {
	runApplication<MatchApplication>(*args)
}
