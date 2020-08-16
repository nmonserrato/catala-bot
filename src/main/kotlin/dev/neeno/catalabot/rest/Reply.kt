package dev.neeno.catalabot.rest

import com.fasterxml.jackson.annotation.JsonProperty

data class Reply (
    val method: String = "sendMessage",
    @JsonProperty("chat_id")
    val chatId: String,
    val text: String,
    @JsonProperty("parse_mode")
    val parseMode: String = "HTML"
)