package com.mgvpri.fabo.langzeit

import com.mgvpri.fabo.base.utils.getServerName
import com.mgvpri.fabo.langzeit.claim.ClaimCommand
import com.mgvpri.fabo.langzeit.claim.ClaimConfig
import com.mgvpri.fabo.langzeit.claim.ClaimListener
import com.mgvpri.fabo.langzeit.config.LangzeitConfig
import com.mgvpri.fabo.langzeit.special.LangzeitMapGeneration
import com.mgvpri.fabo.langzeit.special.LangzeitListener
import net.axay.kspigot.main.KSpigot
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class LangzeitPlugin : KSpigot() {
    override fun startup() {
        plugin = this
        LOGGER = plugin.logger

        logger.info("Starting Special Plugin for Server: ${getServerName()}")

        LangzeitConfig

        // Special Features
        LangzeitMapGeneration
        LangzeitListener

        if (LangzeitConfig.claimSystem) {
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
