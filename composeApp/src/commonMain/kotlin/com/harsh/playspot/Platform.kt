package com.harsh.playspot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform