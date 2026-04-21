package com.github.efelleto.aloAFK.listener

import com.github.efelleto.aloAFK.AfkManager
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.plugin.Plugin

class AfkListener(private val afkManager: AfkManager, private val plugin: Plugin) : Listener {

    private fun handleHumanAction(player: Player) {
        if (afkManager.isAfk(player.uniqueId)) {
            afkManager.setAfk(player, false)
        }
        afkManager.updateLastAction(player)
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val from = event.from
        val to = event.to
        if (from.blockX == to.blockX && from.blockY == to.blockY && from.blockZ == to.blockZ) return
        handleHumanAction(event.player)
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        handleHumanAction(event.player)
    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val sender = event.player
        handleHumanAction(sender)

        val plainText = PlainTextComponentSerializer.plainText().serialize(event.message())
        for ((uuid, _) in afkManager.getAfkMap()) {
            val afkPlayer = plugin.server.getPlayer(uuid) ?: continue
            if (afkPlayer == sender) continue
            if (plainText.contains(afkPlayer.name, ignoreCase = true)) {
                val bar = Component.text("☾ ${afkPlayer.name} está AFK!").color(NamedTextColor.RED)
                sender.sendActionBar(bar)
                break
            }
        }
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        handleHumanAction(event.player)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        handleHumanAction(player)
    }

    @EventHandler
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        handleHumanAction(event.player)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        afkManager.initPlayer(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        if (afkManager.isAfk(player.uniqueId)) {
            afkManager.setAfk(player, false)
        }
        afkManager.removePlayer(player)
    }

    @EventHandler
    fun onBedEnter(event: PlayerBedEnterEvent) {
        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            val world = event.player.world
            val nonAfkPlayers = world.players.filter { !afkManager.isAfk(it.uniqueId) }
            if (nonAfkPlayers.isEmpty()) return@Runnable
            if (nonAfkPlayers.all { it.sleepTicks > 0 }) {
                world.time = 0
                world.setStorm(false)
            }
        }, 40L)
    }
}
