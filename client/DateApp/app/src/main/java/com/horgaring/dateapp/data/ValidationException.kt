package com.horgaring.dateapp.data

import java.io.IOException


class ValidationException(
    private val _errors: HashMap<String, String>,
    val status: Int
) : IOException() {
    val errors: Map<String, String>
        get() = _errors
}