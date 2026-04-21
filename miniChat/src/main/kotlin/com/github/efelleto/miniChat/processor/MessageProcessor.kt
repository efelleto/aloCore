package com.github.efelleto.miniChat.processor

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player

object MessageProcessor {

    /** Context tags + emoji substitution — no deserialization yet. Used by ChatListener for per-viewer processing. */
    fun preProcess(rawText: String, player: Player): String {
        val withContext = ContextTagProcessor.process(rawText, player)
        return EmojiProcessor.process(withContext)
    }

    /** Full pipeline: context tags → emoji → legacy color codes. */
    fun process(rawText: String, player: Player): Component {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(preProcess(rawText, player))
    }
}
