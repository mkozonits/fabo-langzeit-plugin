package com.mgvpri.fabo.specialplugin

import com.mgvpri.fabo.specialplugin.service.BossBarManager
import com.mgvpri.fabo.specialplugin.service.ScoreboardManager
import com.mgvpri.fabo.specialplugin.service.WorldManager
import com.mgvpri.fabo.specialplugin.utils.ServerUtils
import net.axay.kspigot.main.KSpigot
import org.bukkit.*
import org.bukkit.plugin.java.JavaPlugin

class SpecialPlugin : KSpigot() {
    override fun startup() {
        plugin = this

        Bukkit.getLogger().info("Starting Special Plugin for Server: ${ServerUtils.getServerName()}")

        BossBarManager
        ScoreboardManager
        WorldManager
    }

    companion object {
        lateinit var plugin: JavaPlugin
    }
}
