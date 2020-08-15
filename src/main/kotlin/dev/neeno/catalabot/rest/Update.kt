package dev.neeno.catalabot.rest

data class Update(val message: Message)

data class Message (val text: String, val chat: Chat)

data class Chat(val id: String)