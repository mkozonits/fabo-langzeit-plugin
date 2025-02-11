package com.mgvpri.fabo.langzeit.service

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.bossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

object BossBarManager {
    private val bossBars = mutableMapOf<UUID, BossBar>()

    fun setAllPlayerBossBars(title: Component, progress: Float, color: BossBar.Color) {
        for (p in Bukkit.getOnlinePlayers()) {
            setPlayerBossBar(p, title, progress, color)
        }
    }

    fun setPlayerBossBar(p: Player, title: Component, progress: Float, color: BossBar.Color) {
        val bossBar = bossBars[p.uniqueId] ?: bossBar(title, progress, color, BossBar.Overlay.PROGRESS)
        bossBar.addViewer(p)
        bossBar.name(title)
        bossBar.progress(progress)
        bossBar.color(color)
        bossBars[p.uniqueId] = bossBar
    }

    fun removeAllPlayerBossBars() {
        for (p in Bukkit.getOnlinePlayers()) {
            removePlayerBossBar(p)
        }
    }

    fun removePlayerBossBar(p: Player) {
        val removedBossBar = bossBars.remove(p.uniqueId)
        removedBossBar?.removeViewer(p)
    }
}