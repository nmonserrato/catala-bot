package dev.neeno.catalabot

data class Word(
    val original: String,
    val frequency: Long,
    val tags: Set<String> = HashSet(),
    val synonyms: List<String>,
    val examples: List<String>,
    val translations: List<String> = ArrayList()
)