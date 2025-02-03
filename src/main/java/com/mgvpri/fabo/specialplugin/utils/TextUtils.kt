package com.mgvpri.fabo.specialplugin.utils

import net.axay.kspigot.chat.LiteralTextBuilder
import net.axay.kspigot.chat.literalText
import net.kyori.adventure.text.format.NamedTextColor

fun LiteralTextBuilder.tab() = text("    ")

fun LiteralTextBuilder.arrow() = text("» ") {
    color = NamedTextColor.DARK_GRAY
}
fun LiteralTextBuilder.tabArrow() = text("    » ") {
    color = NamedTextColor.DARK_GRAY
}
fun LiteralTextBuilder.grayText(str: String) = text(str) {
    color = NamedTextColor.GRAY
}
fun LiteralTextBuilder.whiteText(str: String) = text(str) {
    color = NamedTextColor.WHITE
}

fun LiteralTextBuilder.whiteBoldText(str: String) = text(str) {
    color = NamedTextColor.WHITE
    bold = true
}
fun LiteralTextBuilder.serverInfo() = listOf(
    text("| ") {
        color = NamedTextColor.DARK_GRAY
    },
    whiteBoldText("Challenge"),
    grayText(" x ")
)

fun LiteralTextBuilder.serverError() = listOf(
    serverInfo(),
    text("Fehler: ") {
        color = NamedTextColor.RED
    }
)

fun String.serverInfo() = literalText {
    serverInfo()
    text(this@serverInfo)
}

fun String.serverError() = literalText {
    serverError()
    text(this@serverError)
}
