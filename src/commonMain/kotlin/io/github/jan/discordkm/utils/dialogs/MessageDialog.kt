package io.github.jan.discordkm.utils.dialogs

import io.github.jan.discordkm.api.entities.interactions.components.ActionRow
import io.github.jan.discordkm.api.entities.messages.DataMessage

interface MessageDialog {

    fun build() : DataMessage

}