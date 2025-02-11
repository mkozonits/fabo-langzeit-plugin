package com.mgvpri.fabo.langzeit.special

import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.kill
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.abs

object LangzeitListener {
    init {
        listen<PlayerJoinEvent> { event ->
            if (!event.player.isOp && event.player.isSurvivalMode() && event.player.location.world.name == LangzeitProperties.WORLD.name) {
                if (abs(event.player.location.blockX) > LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION ||
                    abs(event.player.location.blockZ) > LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION ||
                    abs(event.player.location.blockY) > event.player.world.maxHeight - 2
                ) {
                    event.player.teleport(LangzeitProperties.SPAWN_LOCATION)
                }
            }
            event.player.setRespawnLocation(LangzeitProperties.SPAWN_LOCATION, true)
        }

        listen<PlayerMoveEvent> { event ->
            if (!event.player.isOp && event.player.isSurvivalMode() && event.player.location.world.name == LangzeitProperties.WORLD.name) {
                val location = event.to
                if (location.blockX !in -LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION..LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION ||
                    location.blockZ !in -LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION..LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION ||
                    location.blockY !in -64..location.world.maxHeight-2) {
                    event.isCancelled = true
                    event.player.setRespawnLocation(LangzeitProperties.SPAWN_LOCATION, true)
                    event.player.kill()
                }
            }
        }

        listen<BlockFromToEvent> { event ->
            if (event.block.world.name == LangzeitProperties.WORLD.name) {
                if (event.toBlock.y < LangzeitProperties.WORLD.maxHeight - 127 && event.block.y >= LangzeitProperties.WORLD.maxHeight - 127) {
                    if (event.block.type == Material.WATER || event.block.type == Material.LAVA) {
                        event.isCancelled = true
                    }
                }
            }
        }
    }
}

fun Player.isSurvivalMode() = listOf(GameMode.SURVIVAL, GameMode.ADVENTURE).contains(this.gameMode)