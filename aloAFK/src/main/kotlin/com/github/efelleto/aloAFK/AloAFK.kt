package com.github.efelleto.aloAFK

import com.github.efelleto.aloAFK.command.AfkCommand
import com.github.efelleto.aloAFK.listener.AfkListener
import org.bukkit.plugin.java.JavaPlugin

class AloAFK : JavaPlugin() {
    private lateinit var afkManager: AfkManager

    override fun onEnable() {
        afkManager = AfkManager()
        server.pluginManager.registerEvents(AfkListener(afkManager, this), this)
        getCommand("afk")?.setExecutor(AfkCommand(afkManager))

        for (player in server.onlinePlayers) {
            afkManager.initPlayer(player)
        }

        // Verificar AFK a cada 600 ticks (30s), limite de inatividade de 10 minutos
        server.scheduler.runTaskTimer(this, Runnable {
            for (player in server.onlinePlayers) {
                if (afkManager.isAfk(player.uniqueId)) continue
                val last = afkManager.getLastActionMap()[player.uniqueId] ?: continue
                if (System.currentTimeMillis() - last > 600_000L) {
                    afkManager.setAfk(player, true)
                }
            }
        }, 600L, 600L)
    }

    override fun onDisable() {
        server.scheduler.cancelTasks(this)
    }
}
