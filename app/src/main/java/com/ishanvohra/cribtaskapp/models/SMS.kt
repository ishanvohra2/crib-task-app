package com.ishanvohra.cribtaskapp.models

import java.util.*

data class SMS(
    val number: String,
    val message: String,
    val date: Date
) {
}