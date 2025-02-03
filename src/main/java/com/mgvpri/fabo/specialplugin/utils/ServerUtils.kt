package com.mgvpri.fabo.specialplugin.utils

import org.bukkit.Bukkit

object ServerUtils {
    fun getServerName(): String {
        return Bukkit.getServer().worldContainer.absoluteFile.parentFile.name
    }
}