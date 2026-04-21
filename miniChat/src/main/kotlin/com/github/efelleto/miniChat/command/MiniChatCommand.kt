package com.github.efelleto.miniChat.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class MiniChatCommand : CommandExecutor {

    private val accentColor: TextColor = TextColor.fromHexString("#5865F2")!!
    private val grayColor: TextColor = TextColor.color(0xAAAAAA)
    private val whiteColor: TextColor = TextColor.color(0xFFFFFF)

    private val emojiEntries = listOf(
        Triple("Fogo",      ":fire:",    "&6🔥"),
        Triple("Coração",   ":heart:",   "&c❤"),
        Triple("Olhos",     ":eyes:",    "&f👀"),
        Triple("Estrela",   ":star:",    "&e⭐"),
        Triple("Check",     ":check:",   "&a✅"),
        Triple("X",         ":x:",       "&c❌"),
        Triple("Sorriso",   ":smile:",   "&e😊"),
        Triple("Choro",     ":cry:",     "&9😢"),
        Triple("Caveira",   ":skull:",   "&7💀"),
        Triple("100",       ":100:",     "&c💯"),
        Triple("Joinha",    ":thumbs:",  "&a👍"),
        Triple("Festa",     ":gg:",      "&6🎉")
    )

    private val aliasEntries = listOf(
        Triple("Coração",   "<3 / S2",  "&c❤"),
        Triple("Sorriso",   ":)",       "&e😊"),
        Triple("Choro",     ":(",       "&9😢")
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isNotEmpty() && args[0].equals("emojis", ignoreCase = true)) {
            sendEmojiList(sender)
            return true
        }
        sender.sendMessage(
            Component.text("Uso: ").color(grayColor)
                .append(Component.text("/minichat emojis").color(whiteColor))
        )
        return true
    }

    private fun sendEmojiList(sender: CommandSender) {
        val sep = Component.text("  →  ").color(accentColor)

        sender.sendMessage(
            Component.empty()
                .append(Component.text("══ ").color(accentColor))
                .append(Component.text("Emojis miniChat").color(whiteColor))
                .append(Component.text(" ══").color(accentColor))
        )

        for ((name, shortcut, coloredEmoji) in emojiEntries) {
            val emojiComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(coloredEmoji)
            sender.sendMessage(
                Component.text(name).color(whiteColor)
                    .append(sep)
                    .append(Component.text(shortcut).color(accentColor))
                    .append(sep)
                    .append(emojiComponent)
            )
        }

        sender.sendMessage(Component.text("── Atalhos ──").color(grayColor))

        for ((name, shortcut, coloredEmoji) in aliasEntries) {
            val emojiComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(coloredEmoji)
            sender.sendMessage(
                Component.text(name).color(whiteColor)
                    .append(sep)
                    .append(Component.text(shortcut).color(accentColor))
                    .append(sep)
                    .append(emojiComponent)
            )
        }
    }
}
