package com.github.efelleto.miniChat.processor

import kotlin.math.roundToInt
import org.bukkit.World
import org.bukkit.entity.Player

object ContextTagProcessor {

    fun process(text: String, player: Player): String {
        var result = text
        result = result.replace("*coords", formatCoords(player))
        result = result.replace("*vida",   formatHealth(player))
        result = result.replace("*bioma",  formatBiome(player))
        result = result.replace("*mundo",  formatWorld(player))
        return result
    }

    private fun formatCoords(player: Player): String {
        val loc = player.location
        return "&aX: &f${loc.blockX} &aY: &f${loc.blockY} &aZ: &f${loc.blockZ}"
    }

    @Suppress("DEPRECATION")
    private fun formatHealth(player: Player): String {
        val health    = player.health.roundToInt()
        val maxHealth = player.maxHealth.roundToInt()
        val pct       = player.health / player.maxHealth
        val color = when {
            pct > 0.75  -> "&a"
            pct >= 0.40 -> "&e"
            else        -> "&4"
        }
        return "&c❤ $color$health&7/$color$maxHealth"
    }

    private fun formatBiome(player: Player): String {
        val world = player.world
        val biome = player.location.block.biome
        val color = when (world.environment) {
            World.Environment.NETHER  -> "&c"
            World.Environment.THE_END -> "&5"
            else -> {
                val name = biome.name().uppercase()
                when {
                    name.contains("CHERRY")                                                   -> "&d"
                    name.contains("MUSHROOM")                                                 -> "&5"
                    name.contains("SNOW") || name.contains("FROZEN") || name.contains("ICE") -> "&b"
                    name.contains("SAVANNA") || name.contains("BADLANDS") || name.contains("ACACIA") -> "&6"
                    name.contains("JUNGLE")                                                   -> "&2"
                    name.contains("SWAMP") || name.contains("MANGROVE")                      -> "&2"
                    name.contains("BEACH") || name.contains("OCEAN") || name.contains("RIVER") -> "&3"
                    name.contains("DESERT")                                                   -> "&e"
                    name.contains("FOREST") || name.contains("TAIGA") || name.contains("GROVE") -> "&a"
                    name.contains("PLAINS") || name.contains("MEADOW")                       -> "&a"
                    name.contains("MOUNTAIN") || name.contains("PEAK") || name.contains("HIGHLANDS") -> "&7"
                    else                                                                      -> "&f"
                }
            }
        }
        val displayName = biome.name()
            .split("_")
            .joinToString(" ") { word: String -> word.lowercase().replaceFirstChar { it.uppercase() } }
        return "$color$displayName"
    }

    private fun formatWorld(player: Player): String {
        return when (player.world.environment) {
            World.Environment.NORMAL  -> "&aOverworld"
            World.Environment.NETHER  -> "&cNether"
            World.Environment.THE_END -> "&5End"
            else                      -> "&f${player.world.name}"
        }
    }
}
