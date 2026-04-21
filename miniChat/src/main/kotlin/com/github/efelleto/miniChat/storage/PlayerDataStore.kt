package com.github.efelleto.miniChat.storage

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

class PlayerDataStore(private val dataFolder: File) {

    private val file = File(dataFolder, "players.yml")
    private val config = YamlConfiguration()

    fun load() {
        if (!file.exists()) {
            dataFolder.mkdirs()
            file.createNewFile()
        }
        config.load(file)
    }

    fun savePlayer(uuid: UUID, type: String, data: Map<String, String>) {
        val path = uuid.toString()
        config.set("$path.type", type)
        data.forEach { (key, value) -> config.set("$path.$key", value) }
        config.save(file)
    }

    fun getPlayerColor(uuid: UUID, playerName: String): Component {
        val path = uuid.toString()
        if (!config.contains(path)) {
            return Component.text(playerName).color(TextColor.color(0xFFFFFF))
        }
        return when (config.getString("$path.type")) {
            "solid_legacy" -> {
                val code = config.getString("$path.color") ?: "&f"
                LegacyComponentSerializer.legacyAmpersand().deserialize("$code$playerName")
            }
            "solid_hex" -> {
                val hex = config.getString("$path.color") ?: "#FFFFFF"
                Component.text(playerName).color(TextColor.fromHexString(hex) ?: TextColor.color(0xFFFFFF))
            }
            "gradient" -> {
                val hex1 = config.getString("$path.color1") ?: "#FFFFFF"
                val hex2 = config.getString("$path.color2") ?: "#FFFFFF"
                MiniMessage.miniMessage().deserialize("<gradient:$hex1:$hex2>$playerName</gradient>")
            }
            else -> Component.text(playerName).color(TextColor.color(0xFFFFFF))
        }
    }
}
