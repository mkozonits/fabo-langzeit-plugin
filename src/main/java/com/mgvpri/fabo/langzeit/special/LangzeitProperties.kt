package com.mgvpri.fabo.langzeit.special

import org.bukkit.Bukkit
import org.bukkit.Location

object LangzeitProperties {
    const val MAX_BLOCKS_EACH_DIRECTION = 1250

    val WORLD = Bukkit.getWorld("world")!!
    val SPAWN_LOCATION = Location(WORLD, 0.0, 173.0, 0.0)
}