package com.mgvpri.fabo.langzeit.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object LangzeitConfig {
    var claimSystem = false
        private set
    var damage = true
        private set
    var pvp = false
        private set
    var food = true
        private set
    var respawn = true
        private set
    var nether = false
        private set
    var end = false
        private set

    init {
        val file = File("./config/langzeit-config.yml")
        val config = YamlConfiguration.loadConfiguration(file)

        config.addDefault("claimSystem", false)
        config.addDefault("damage", true)
        config.addDefault("pvp", false)
        config.addDefault("food", true)
        config.addDefault("respawn", true)
        config.addDefault("nether", false)
        config.addDefault("end", false)

        config.save(file)

        claimSystem = config.getBoolean("claimSystem")
        damage = config.getBoolean("damage")
        pvp = config.getBoolean("pvp")
        food = config.getBoolean("food")
        respawn = config.getBoolean("respawn")
        nether = config.getBoolean("nether")
        end = config.getBoolean("end")
    }
}