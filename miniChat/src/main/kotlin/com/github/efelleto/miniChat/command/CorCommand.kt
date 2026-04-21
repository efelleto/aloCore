package com.github.efelleto.miniChat.command

import com.github.efelleto.miniChat.storage.PlayerDataStore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CorCommand(private val store: PlayerDataStore) : CommandExecutor {

    private val accentColor: TextColor = TextColor.fromHexString("#5865F2")!!
    private val grayColor: TextColor = TextColor.color(0xAAAAAA)
    private val whiteColor: TextColor = TextColor.color(0xFFFFFF)
    private val redColor: TextColor = TextColor.color(0xFF5555)

    private val validColorChars = setOf('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f')

    private val colorEntries = listOf(
        "&0" to "Preto",
        "&1" to "Azul Escuro",
        "&2" to "Verde Escuro",
        "&3" to "Ciano Escuro",
        "&4" to "Vermelho Escuro",
        "&5" to "Roxo Escuro",
        "&6" to "Ouro",
        "&7" to "Cinza",
        "&8" to "Cinza Escuro",
        "&9" to "Azul",
        "&a" to "Verde",
        "&b" to "Ciano",
        "&c" to "Vermelho",
        "&d" to "Rosa",
        "&e" to "Amarelo",
        "&f" to "Branco"
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Apenas jogadores podem usar este comando.")
            return true
        }
        when {
            args.isEmpty() -> sendHelp(sender)
            args[0].equals("lista", ignoreCase = true) -> sendPalette(sender)
            args.size >= 2 && args[0].startsWith("#") && args[1].startsWith("#") -> setGradient(sender, args[0], args[1])
            args.size == 1 -> setColor(sender, args[0])
            else -> sendHelp(sender)
        }
        return true
    }

    private fun sendHelp(player: Player) {
        player.sendMessage(
            Component.empty()
                .append(Component.text("══ ").color(accentColor))
                .append(Component.text("miniChat").color(whiteColor))
                .append(Component.text(" ══").color(accentColor))
        )
        player.sendMessage(
            Component.text("Formato: ").color(grayColor)
                .append(Component.text("[nome]").color(accentColor))
                .append(Component.text(" » ").color(accentColor))
                .append(Component.text("mensagem").color(whiteColor))
        )
        player.sendMessage(
            Component.text("Cor sólida (código): ").color(grayColor)
                .append(Component.text("/cor &a").color(whiteColor))
                .append(Component.text(" ou ").color(grayColor))
                .append(Component.text("/cor &4").color(whiteColor))
        )
        player.sendMessage(
            Component.text("Cor sólida (HEX): ").color(grayColor)
                .append(Component.text("/cor #FF5733").color(whiteColor))
        )
        player.sendMessage(
            Component.text("Gradiente (dois HEX): ").color(grayColor)
                .append(Component.text("/cor #9B59B6 #3498DB").color(whiteColor))
        )
        player.sendMessage(
            Component.text("Ver cores: ").color(grayColor)
                .append(Component.text("/cor lista").color(accentColor))
        )
    }

    private fun sendPalette(player: Player) {
        player.sendMessage(
            Component.text("══ Cores disponíveis ══").color(accentColor)
        )
        for (i in 0..7) {
            val (leftCode, leftName) = colorEntries[i]
            val (rightCode, rightName) = colorEntries[i + 8]
            val leftChar = leftCode[1]
            val rightChar = rightCode[1]

            val leftEntry = LegacyComponentSerializer.legacyAmpersand()
                .deserialize("$leftCode&&$leftChar $leftName")
            val rightEntry = LegacyComponentSerializer.legacyAmpersand()
                .deserialize("$rightCode&&$rightChar $rightName")

            player.sendMessage(
                Component.empty()
                    .append(leftEntry)
                    .append(Component.text("   ").color(whiteColor))
                    .append(rightEntry)
            )
        }
    }

    private fun setColor(player: Player, value: String) {
        when {
            value.startsWith("&") && value.length == 2 && value[1].lowercaseChar() in validColorChars -> {
                store.savePlayer(player.uniqueId, "solid_legacy", mapOf("color" to value.lowercase()))
                val preview = store.getPlayerColor(player.uniqueId, player.name)
                player.sendMessage(
                    Component.text("Cor definida! Seu nome: ").color(grayColor).append(preview)
                )
            }
            value.startsWith("#") && value.length == 7 && TextColor.fromHexString(value) != null -> {
                store.savePlayer(player.uniqueId, "solid_hex", mapOf("color" to value))
                val preview = store.getPlayerColor(player.uniqueId, player.name)
                player.sendMessage(
                    Component.text("Cor definida! Seu nome: ").color(grayColor).append(preview)
                )
            }
            else -> {
                player.sendMessage(
                    Component.text("Formato inválido. Use ").color(redColor)
                        .append(Component.text("/cor").color(whiteColor))
                        .append(Component.text(" para ajuda.").color(redColor))
                )
            }
        }
    }

    private fun setGradient(player: Player, hex1: String, hex2: String) {
        if (TextColor.fromHexString(hex1) == null || TextColor.fromHexString(hex2) == null) {
            player.sendMessage(Component.text("Cores HEX inválidas.").color(redColor))
            return
        }
        store.savePlayer(player.uniqueId, "gradient", mapOf("color1" to hex1, "color2" to hex2))
        val preview = store.getPlayerColor(player.uniqueId, player.name)
        player.sendMessage(
            Component.text("Gradiente definido! Seu nome: ").color(grayColor).append(preview)
        )
    }
}
