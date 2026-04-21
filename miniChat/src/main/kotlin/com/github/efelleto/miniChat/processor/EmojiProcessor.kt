package com.github.efelleto.miniChat.processor

object EmojiProcessor {

    private val mappings = linkedMapOf(
        ":fire:"    to "&6🔥&f",
        ":heart:"   to "&c❤&f",
        ":eyes:"    to "&f👀",
        ":star:"    to "&e⭐&f",
        ":check:"   to "&a✅&f",
        ":x:"       to "&c❌&f",
        ":smile:"   to "&e😊&f",
        ":cry:"     to "&9😢&f",
        ":skull:"   to "&7💀&f",
        ":100:"     to "&c💯&f",
        ":thumbs:"  to "&a👍&f",
        ":gg:"      to "&6🎉&f",
        "<3"        to "&c❤&f",
        ":)"        to "&e😊&f",
        ":("        to "&9😢&f",
        "S2"        to "&c❤&f"
    )

    fun process(text: String): String {
        var result = text
        mappings.forEach { (key, value) -> result = result.replace(key, value) }
        return result
    }
}
