package com.mgvpri.fabo.langzeit.claim

import net.axay.kspigot.event.listen
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

object ClaimListener {
    init {
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
    return owner != null && owner != player.uniqueId && !player.isOp && (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE)
}