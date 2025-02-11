package com.mgvpri.fabo.langzeit.service

import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.WorldCreator
import java.nio.file.Paths

object WorldManager {
    fun getMapInServerFolder(): List<String> {
        val folder = Paths.get("").toAbsolutePath().toFile()
        if (folder.isDirectory) {
            return folder.listFiles()!!.filter { it.isDirectory && it.name.startsWith("MAP_") }.map { it.name }.sorted()
        }
        return emptyList()
    }

    fun loadWorld(worldName: String): World? {
        return Bukkit.createWorld(WorldCreator(worldName))
    }

    fun prepareWorld(world: World) {
        world.time = 1000
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false)
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.setStorm(false)
        world.isThundering = false
        world.difficulty = Difficulty.EASY
    }

    fun unloadWorld(world: World) {
        Bukkit.unloadWorld(world, false)
    }
}