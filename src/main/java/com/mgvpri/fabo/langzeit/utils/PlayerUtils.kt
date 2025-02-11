package com.mgvpri.fabo.langzeit.utils

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player

fun Player.playInfoSound() = this.playSound(this.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
fun Player.playErrorSound() = this.playSound(this.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f)

fun runForAllPlayers(action: (player: Player) -> Unit) {
    for (player in Bukkit.getOnlinePlayers()) {
        action(player)
    }
}
