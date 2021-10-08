package io.github.jan.discordkm.utils

fun generateId(maxCharacters: Int = 30): String {
    val chars = ('a'..'z').toMutableList()
    chars.addAll('A'..'Z')
    return buildString {
        for(i in 0..maxCharacters) {
            append(chars.random())
        }
    }
}