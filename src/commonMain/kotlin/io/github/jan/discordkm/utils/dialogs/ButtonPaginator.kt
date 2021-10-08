package io.github.jan.discordkm.utils.dialogs

import io.github.jan.discordkm.api.entities.clients.DiscordWebSocketClient
import io.github.jan.discordkm.api.entities.guild.Emoji
import io.github.jan.discordkm.api.entities.interactions.StandardInteraction
import io.github.jan.discordkm.api.entities.interactions.components.ActionRow
import io.github.jan.discordkm.api.entities.interactions.components.Button
import io.github.jan.discordkm.api.entities.interactions.components.ButtonBuilder
import io.github.jan.discordkm.api.entities.interactions.components.ButtonStyle
import io.github.jan.discordkm.api.entities.messages.DataMessage
import io.github.jan.discordkm.api.entities.messages.MessageBuilder
import io.github.jan.discordkm.api.entities.messages.buildMessage
import io.github.jan.discordkm.api.events.ButtonClickEvent
import io.github.jan.discordkm.internal.entities.channels.MessageChannel
import io.github.jan.discordkm.utils.dialogs.MessageDialog
import io.github.jan.discordkm.utils.generateId

class ButtonPaginator @PublishedApi internal constructor(val client: DiscordWebSocketClient, var maxPageSize: Int = 5) : MessageDialog {

    var nextButton = Button(emoji = Emoji.fromEmoji("➡️"), style = ButtonStyle.PRIMARY, customId = generateId())
    var previousButton = Button(emoji = Emoji.fromEmoji("⬅️"), style = ButtonStyle.PRIMARY, customId = generateId())
    var firstButton = Button(label = "First", style = ButtonStyle.PRIMARY, customId = generateId())
    var lastButton = Button(label = "Last", style = ButtonStyle.PRIMARY, customId = generateId())
    private var onPageChange: MessageBuilder.(Int) -> Unit = { buildMessage {  } }
    private var page = 1

    fun onPageChange(onPageChange: MessageBuilder.(Int) -> Unit) { this.onPageChange = onPageChange }

    override fun build() = buildMessage {
        import(buildMessage())
        client.on<ButtonClickEvent>(predicate = { it.componentId == nextButton.customId }) { page++; interaction.edit(buildMessage()) }
        client.on<ButtonClickEvent>(predicate = { it.componentId == firstButton.customId }) { page = 1; interaction.edit(buildMessage())  }
        client.on<ButtonClickEvent>(predicate = { it.componentId == lastButton.customId }) { page = maxPageSize; interaction.edit(buildMessage())  }
        client.on<ButtonClickEvent>(predicate = { it.componentId == previousButton.customId }) { page--; interaction.edit(buildMessage())  }
    }

    private fun buildMessage() = buildMessage {
        actionRow {
            val nextButton = if(page == maxPageSize) nextButton.copy(isDisabled = true) else nextButton
            val previousButton = if(page == 1) previousButton.copy(isDisabled = true) else previousButton
            val firstButton = if(page == 1) firstButton.copy(isDisabled = true) else firstButton
            val lastButton = if(page == maxPageSize) lastButton.copy(isDisabled = true) else lastButton
            components += listOf(firstButton, previousButton, nextButton, lastButton)
        }
        onPageChange(this, page)
    }

}

suspend inline fun StandardInteraction.replyPaginator(maxPageSize: Int, ephemeral: Boolean = false, builder: ButtonPaginator.() -> Unit) = reply(ButtonPaginator(client as DiscordWebSocketClient, maxPageSize).apply(builder).build(), ephemeral)

suspend inline fun MessageChannel.sendPaginator(maxPageSize: Int, builder: ButtonPaginator.() -> Unit) = send(ButtonPaginator(client as DiscordWebSocketClient, maxPageSize).apply(builder).build())