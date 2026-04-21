package com.github.efelleto.aloAFK.command

import com.github.efelleto.aloAFK.AfkManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AfkCommand(private val afkManager: AfkManager) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isNotEmpty() && args[0].equals("list", ignoreCase = true)) {
            sendAfkList(sender)
            return true
        }

        if (sender !is Player) {
            sender.sendMessage(Component.text("Apenas jogadores podem usar /afk").color(NamedTextColor.RED))
            return true
        }

        if (afkManager.isAfk(sender.uniqueId)) {
            afkManager.setAfk(sender, false)
        } else {
            afkManager.setAfk(sender, true)
        }
        return true
    }

    private fun sendAfkList(sender: CommandSender) {
        val prefix = Component.text("[").color(NamedTextColor.GRAY)
            .append(Component.text("aloAFK").color(NamedTextColor.AQUA))
            .append(Component.text("] ").color(NamedTextColor.GRAY))

        val afkMap = afkManager.getAfkMap()
        if (afkMap.isEmpty()) {
            sender.sendMessage(
                prefix.append(
                    Component.text("Não há nenhum jogador AFK no momento!").color(NamedTextColor.GREEN)
                )
            )
            return
        }

        sender.sendMessage(
            prefix.append(Component.text("Jogadores AFK:").color(NamedTextColor.GRAY))
        )

        val now = System.currentTimeMillis()
        for ((uuid, since) in afkMap) {
            val player = sender.server.getPlayer(uuid) ?: continue
            val elapsed = (now - since) / 1000L
            val line = Component.text("● ").color(NamedTextColor.RED)
                .append(Component.text(player.name).color(NamedTextColor.RED))
                .append(Component.text(" — ").color(NamedTextColor.GRAY))
                .append(Component.text(formatTime(elapsed)).color(NamedTextColor.WHITE))
            sender.sendMessage(line)
        }
    }

    private fun formatTime(seconds: Long): String {
        return if (seconds < 3600) {
            val mins = seconds / 60
            val secs = seconds % 60
            "${mins} minuto${if (mins != 1L) "s" else ""} e ${secs} segundo${if (secs != 1L) "s" else ""}"
        } else {
            val hours = seconds / 3600
            val mins = (seconds % 3600) / 60
            "${hours} hora${if (hours != 1L) "s" else ""} e ${mins} minuto${if (mins != 1L) "s" else ""}"
        }
    }
}
