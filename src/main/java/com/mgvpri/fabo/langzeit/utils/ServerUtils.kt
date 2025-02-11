package com.mgvpri.fabo.langzeit.utils

import org.bukkit.Bukkit

object ServerUtils {
    fun getServerName(): String {
        return Bukkit.getServer().worldContainer.absoluteFile.parentFile.name
    }
}