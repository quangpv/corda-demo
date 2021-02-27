package com.template.core


infix fun Boolean.throws(message: String) {
    if (this) throw IllegalAccessException(message)
}