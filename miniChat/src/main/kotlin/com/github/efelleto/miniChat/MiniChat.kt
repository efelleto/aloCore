package com.github.efelleto.miniChat

import com.github.efelleto.miniChat.command.CorCommand
import com.github.efelleto.miniChat.command.MiniChatCommand
import com.github.efelleto.miniChat.listener.ChatListener
import com.github.efelleto.miniChat.storage.PlayerDataStore
import org.bukkit.plugin.java.JavaPlugin

class MiniChat : JavaPlugin() {

    lateinit var playerDataStore: PlayerDataStore

    override fun onEnable() {
        playerDataStore = PlayerDataStore(dataFolder)
        playerDataStore.load()

        server.pluginManager.registerEvents(ChatListener(playerDataStore), this)
        getCommand("cor")?.setExecutor(CorCommand(playerDataStore))
        getCommand("minichat")?.setExecutor(MiniChatCommand())
    }

    override fun onDisable() {
        // no-op
    }
}
