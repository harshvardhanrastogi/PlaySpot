package com.harsh.playspot.util

import com.harsh.playspot.currentTimeMillis

fun getCurrentTimeWithJoiningBuffer(): Long {
    return currentTimeMillis() + 3600000L // + 1 hr
}