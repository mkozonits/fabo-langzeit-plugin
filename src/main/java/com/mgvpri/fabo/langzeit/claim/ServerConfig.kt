package com.mgvpri.fabo.langzeit.claim

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object ServerConfig {
    var claimSystem = false
        private set

    init {
        val file = File("./config/server-config.yml")
        val config = YamlConfiguration.loadConfiguration(file)

        config.addDefault("claimSystem", false)

        config.save(file)

        claimSystem = config.getBoolean("claimSystem")
    }
}