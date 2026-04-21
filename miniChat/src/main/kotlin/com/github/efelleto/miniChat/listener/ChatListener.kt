package com.github.efelleto.miniChat.listener

import com.github.efelleto.miniChat.processor.MessageProcessor
import com.github.efelleto.miniChat.storage.PlayerDataStore
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener(private val store: PlayerDataStore) : Listener {

    private val separatorColor: TextColor = TextColor.fromHexString("#5865F2")!!
    private val messageDefault: TextColor = TextColor.color(0xFFFFFF)
    private val mentionColor: TextColor   = TextColor.color(0xFFFF55)

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val source      = event.player
        val rawText     = PlainTextComponentSerializer.plainText().serialize(event.message())
        val preprocessed = MessageProcessor.preProcess(rawText, source)

        // Collect mentioned online players — exclude source
        val mentionedPlayers: List<Player> = event.viewers()
            .filterIsInstance<Player>()
            .filter { it.uniqueId != source.uniqueId }
            .filter { preprocessed.contains(it.name, ignoreCase = true) }

        event.renderer(ChatRenderer { _, _, _, audience ->
            val nameComponent = store.getPlayerColor(source.uniqueId, source.name)

            val messageText: String = if (audience is Player && audience in mentionedPlayers) {
                // Highlight this viewer's own nick, preserve sender's original capitalisation
                val regex = Regex(Regex.escape(audience.name), RegexOption.IGNORE_CASE)
                val highlighted = regex.replace(preprocessed) { match -> "&a&l${match.value}&r&f" }
                // Notify the mentioned player
                audience.playSound(audience.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
                audience.sendActionBar(
                    Component.text("✦ você foi mencionado no chat!").color(mentionColor)
                )
                highlighted
            } else {
                preprocessed
            }

            val processedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(messageText)

            Component.empty()
                .append(nameComponent)
                .append(Component.text(" » ").color(separatorColor))
                .append(Component.empty().color(messageDefault).append(processedMessage))
        })
    }
}
