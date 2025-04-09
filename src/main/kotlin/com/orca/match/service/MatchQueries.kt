package com.orca.match.service

import com.orca.match.domain.MatchStatus
import org.springframework.data.domain.Sort

data class MatchQuery(
    val status: MatchStatus?,
    val sortOptions: List<Pair<String, Sort.Direction>>
)