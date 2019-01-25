package com.whitedev.easylog.pojo

data class KeyEventPojo(
    val action: String,
    val deviceId: Int,
    val downTime: Int,
    val eventTime: Int,
    val flags: String,
    val hwFlags: String,
    val keyCode: String,
    val metaState: Int,
    val repeatCount: Int,
    val scanCode: Int,
    val source: String
)