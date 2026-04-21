package com.github.efelleto.aloAFK

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.UUID

class AfkManager {
    private val afkSince = HashMap<UUID, Long>()
    private val lastAction = HashMap<UUID, Long>()
    private val originalNames = HashMap<UUID, Component>()

    fun setAfk(player: Player, afk: Boolean) {
        if (afk) {
            afkSince[player.uniqueId] = System.currentTimeMillis()
            originalNames[player.uniqueId] = player.playerListName()

            val tabName = Component.text("[AFK] ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text(player.name).color(NamedTextColor.RED))
            player.playerListName(tabName)

            val actionBar = Component.text("☾ ${player.name} está AFK!").color(NamedTextColor.RED)
            for (online in player.server.onlinePlayers) {
                online.sendActionBar(actionBar)
                online.playSound(online.location, Sound.ENTITY_BAT_AMBIENT, 1.0f, 0.8f)
            }
        } else {
            afkSince.remove(player.uniqueId)
            val original: Component = originalNames.remove(player.uniqueId) ?: Component.text(player.name)
            player.playerListName(original)

            val actionBar = Component.text("☀ ${player.name} voltou da inatividade!").color(NamedTextColor.GREEN)
            for (online in player.server.onlinePlayers) {
                online.sendActionBar(actionBar)
                online.playSound(online.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
            }
        }
    }

    fun isAfk(uuid: UUID): Boolean = afkSince.containsKey(uuid)

    fun updateLastAction(player: Player) {
        lastAction[player.uniqueId] = System.currentTimeMillis()
    }

    fun initPlayer(player: Player) {
        lastAction[player.uniqueId] = System.currentTimeMillis()
    }

    fun removePlayer(player: Player) {
        afkSince.remove(player.uniqueId)
        lastAction.remove(player.uniqueId)
        originalNames.remove(player.uniqueId)
    }

    fun getAfkMap(): Map<UUID, Long> = afkSince

    fun getLastActionMap(): Map<UUID, Long> = lastAction
}
