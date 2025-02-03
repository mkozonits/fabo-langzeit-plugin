package com.mgvpri.fabo.specialplugin.service

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.scoreboard.*

object ScoreboardManager {
    private var board: Scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    private var objective: Objective? = null

    fun setGlobalObjective(title: Component) {
        if (board.getObjective("score") != null) {
            board.getObjective("score")!!.unregister()
        }

        objective = board.registerNewObjective("score", Criteria.DUMMY, title)
        objective!!.displaySlot = DisplaySlot.SIDEBAR
    }

    fun setScore(name: String, score: Int) {
        objective?.getScore(name)?.score = score
    }

    fun removeScore(name: String) {
        board.resetScores(name)
    }
}
