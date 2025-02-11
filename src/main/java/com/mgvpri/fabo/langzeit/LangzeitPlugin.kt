package com.mgvpri.fabo.langzeit

import com.mgvpri.fabo.langzeit.claim.ClaimCommand
import com.mgvpri.fabo.langzeit.claim.ClaimConfig
import com.mgvpri.fabo.langzeit.claim.ClaimListener
import com.mgvpri.fabo.langzeit.claim.ServerConfig
import com.mgvpri.fabo.langzeit.service.BossBarManager
import com.mgvpri.fabo.langzeit.service.ScoreboardManager
import com.mgvpri.fabo.langzeit.service.WorldManager
import com.mgvpri.fabo.langzeit.special.LangzeitMapGeneration
import com.mgvpri.fabo.langzeit.special.LangzeitListener
import com.mgvpri.fabo.langzeit.utils.ServerUtils
import net.axay.kspigot.main.KSpigot
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class LangzeitPlugin : KSpigot() {
    override fun startup() {
        plugin = this
        LOGGER = plugin.logger

        logger.info("Starting Special Plugin for Server: ${ServerUtils.getServerName()}")

        ServerConfig

        BossBarManager
        ScoreboardManager
        WorldManager

        // Special Features
        LangzeitMapGeneration
        LangzeitListener

        if (ServerConfig.claimSystem) {
            logger.info("Claim System enabled")

            ClaimConfig
            ClaimCommand
            ClaimListener
        } else {
            logger.info("Claim System disabled")
        }
    }

    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var LOGGER: Logger
    }
}
