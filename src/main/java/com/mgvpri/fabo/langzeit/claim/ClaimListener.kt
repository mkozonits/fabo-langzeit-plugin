package com.mgvpri.fabo.langzeit.claim

import com.mgvpri.fabo.langzeit.utils.*
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTakeLecternBookEvent

// mod kann spieler verwalten
// toggle display?

object ClaimListener {
    init {
        task(sync = true, delay = 20L, period = 20L) {
            for (player in Bukkit.getOnlinePlayers()) {
                val loc = player.location
                val owner = ClaimConfig.getClaimOwner(loc)
                val message = if (owner == null) {
                    literalText {
                        grayText("Freier Chunk")
                    }
                } else if (owner != player.uniqueId) {
                    literalText {
                        grayText("Dieser Chunk gehört ")
                        whiteText(Bukkit.getOfflinePlayer(owner).name ?: "Unbekannt")
                    }
                } else {
                    literalText {
                        grayText("Dieser Chunk gehört ")
                        whiteText("dir")
                    }
                }
                player.sendActionBar(message)
            }
        }

        listen<PrePlayerAttackEntityEvent> { e ->
            if (isProtected(e.player, e.attacked.location)) {
                e.isCancelled = true
            }
        }

        listen<BlockPlaceEvent> { e ->
            if (isProtected(e.player, e.block.location)) {
                e.isCancelled = true
            }
        }

        listen<BlockBreakEvent> { e ->
            if (isProtected(e.player, e.block.location)) {
                e.isCancelled = true
            }
        }

        listen<PlayerTakeLecternBookEvent> { e ->
            if (isProtected(e.player, e.lectern.location)) {
                e.isCancelled = true
            }
        }

        listen<HangingBreakByEntityEvent> { e ->
            val entity = e.remover
            if (entity is Player) {
                if (isProtected(entity, e.entity.location)) {
                    e.isCancelled = true
                }
            }
        }

        listen<HangingPlaceEvent> { e ->
            val player = e.player
            if (player != null) {
                if (isProtected(player, e.entity.location)) {
                    e.isCancelled = true
                }
            }
        }

        listen<PlayerInteractAtEntityEvent> { e ->
            if (isProtected(e.player, e.rightClicked.location)) {
                e.isCancelled = true
            }
        }

        listen<PlayerInteractEntityEvent> { e ->
            if (isProtected(e.player, e.rightClicked.location)) {
                e.isCancelled = true
            }
        }

        listen<PlayerInteractEvent> { e ->
            if (e.action == Action.LEFT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_BLOCK) {
                if (isProtected(e.player, e.clickedBlock!!.location)) {
                    e.isCancelled = true
                }
            }
        }
    }
}

private fun isProtected(player: Player, location: Location): Boolean {
    val owner = ClaimConfig.getClaimOwner(location)
    var friend = false
    if (owner != null) {
        val friends = ClaimConfig.getUserFriends(owner)
        if (friends.contains(player.uniqueId)) friend = true
    }
    return owner != null && owner != player.uniqueId && !friend && !player.isOp && (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE)
}