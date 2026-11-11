package com.fixora.marketplace.data.model

import java.util.Locale

fun Enum<*>.displayName(): String =
    name.lowercase(Locale.ROOT).replace('_', ' ').replaceFirstChar { it.uppercase() }
