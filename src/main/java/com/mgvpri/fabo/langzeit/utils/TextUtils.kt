package com.mgvpri.fabo.langzeit.utils

import net.axay.kspigot.chat.LiteralTextBuilder
import net.axay.kspigot.chat.literalText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/**
 * Text Utils
 */
fun LiteralTextBuilder.tab() = text("    ")
fun LiteralTextBuilder.arrow() = text("» ") {
    color = NamedTextColor.DARK_GRAY
}

fun LiteralTextBuilder.tabArrow() = text("    » ") {
    color = NamedTextColor.DARK_GRAY
}

fun LiteralTextBuilder.line() = text("| ") {
    color = NamedTextColor.DARK_GRAY
}

fun LiteralTextBuilder.grayText(str: String) = text(str) {
    color = NamedTextColor.GRAY
}

fun LiteralTextBuilder.darkGrayText(str: String) = text(str) {
    color = NamedTextColor.DARK_GRAY
}

fun LiteralTextBuilder.whiteText(str: String) = text(str) {
    color = NamedTextColor.WHITE
}

fun LiteralTextBuilder.whiteBoldText(str: String) = text(str) {
    color = NamedTextColor.WHITE
    bold = true
}

fun LiteralTextBuilder.redText(str: String) = text(str) {
    color = NamedTextColor.RED
}

fun LiteralTextBuilder.redBoldText(str: String) = text(str) {
    color = NamedTextColor.RED
    bold = true
}

fun LiteralTextBuilder.yellowText(str: String) = text(str) {
    color = NamedTextColor.YELLOW
}

fun LiteralTextBuilder.yellowBoldText(str: String) = text(str) {
    color = NamedTextColor.YELLOW
    bold = true
}

/**
 * Server Info and Error Messages
 */
fun LiteralTextBuilder.serverInfo(str: String = "Server") = listOf(
    line(),
    whiteBoldText(str),
    grayText(" x ")
)

fun LiteralTextBuilder.challengeInfo() = this.serverInfo("Challenge")
fun LiteralTextBuilder.langzeitInfo() = this.serverInfo("Langzeit")
fun LiteralTextBuilder.serverError() = listOf(
    serverInfo(),
    text("Fehler: ") {
        color = NamedTextColor.RED
    }
)

fun String.serverInfo(str: String = "Server") = literalText {
    serverInfo(str)
    grayText(this@serverInfo)
}

fun String.challengeInfo() = this.serverInfo("Challenge")
fun String.langzeitInfo() = this.serverInfo("Langzeit")
fun String.serverError() = literalText {
    serverError()
    grayText(this@serverError)
}

/**
 * Player and Non-Player Messages
 */
fun playerMessage(displayName: Component, text: String): Component =
    literalText {
        line()
        component(displayName)
        text(" » ") {
            color = NamedTextColor.DARK_GRAY
        }
        grayText(text)
    }

fun nonPlayerMessage(displayName: Component, text: String): Component =
    literalText {
        component(displayName)
        text(" » ") {
            color = NamedTextColor.DARK_GRAY
        }
        grayText(text)
    }
