package com.mgvpri.fabo.langzeit.special

import com.mgvpri.fabo.base.manager.BossBarManager
import com.mgvpri.fabo.langzeit.LangzeitPlugin
import com.mgvpri.fabo.base.utils.*
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.register
import net.axay.kspigot.commands.runs
import net.axay.kspigot.extensions.bukkit.allBlocks
import net.axay.kspigot.extensions.bukkit.dispatchCommand
import net.axay.kspigot.runnables.task
import net.axay.kspigot.runnables.taskRunLater
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import java.io.File
import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs

private var progress = 0L
private var skipped = 0L
private var realProcessed = -1L
private var total = 0L
private var paused = true

private val taskQueue: Queue<ChunkData> = LinkedList()
private val bossBarManager = BossBarManager()

data class ChunkData(val x: Int, val z: Int, val emptyChunk: Boolean = false)
data class BlockTypeData(val type: Material, val data: BlockData)

object LangzeitMapGeneration {
    init {
        var savedProgress = 0L
        try {
            val progressFile = File("worldgen_progress.txt")
            savedProgress = progressFile.readText().toInt().toLong()
            LangzeitPlugin.LOGGER.info("WORLD_GEN: Progress file found, starting from $savedProgress.")
        } catch (e: Exception) {
            LangzeitPlugin.LOGGER.info("WORLD_GEN: No progress file found, starting from scratch.")
        }

        val worldGenerationCommand = command("world_generation", false) {
            runs {
                if (this.player.isOp) {
                    processChunksInSpiral()
                    paused = false
                } else {
                    this.player.sendMessage("Es wurde bereits ein World-Generator gestartet.".serverError())
                }
            }
        }
        worldGenerationCommand.register(true)

        val statusCommand = command("world_generation_status", false) {
            runs {
                this.player.sendMessage(
                    literalText {
                        whiteText("Progress: $progress\n")
                        whiteText("Skipped: $skipped\n")
                        whiteText("Real Processed: $realProcessed\n")
                        whiteText("Total: $total\n")
                        whiteText("Paused: $paused\n")
                    }
                )
            }
        }
        statusCommand.register(true)

        if (savedProgress > 0) {
            processChunksInSpiral()
            paused = false

            for (i in 0 until savedProgress) {
                taskQueue.remove()
                progress++
            }
        }

        task(sync = true, delay = 200, period = 10) {
            if (!paused) {
                processNextChunkInQueue()
            }
        }
    }
}

private fun processChunk(chunk: Chunk, emptyChunk: Boolean = false) {
    if (emptyChunk) {
        for (block in chunk.allBlocks) {
            block.type = Material.AIR
        }
        return
    }

    val blockStates = arrayListOf<BlockTypeData>()

    for (y in chunk.world.maxHeight-1 downTo chunk.world.minHeight step 1) {
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                blockStates.add(chunk.getBlock(x, y, z).let { BlockTypeData(it.type, it.state.blockData) })
            }
        }
    }

    for (y in chunk.world.minHeight until chunk.world.maxHeight) {
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val block = chunk.getBlock(x, y, z)

                if (y == LangzeitProperties.WORLD.maxHeight - 1 || y == LangzeitProperties.WORLD.maxHeight - 2) {
                    block.setType(Material.BEDROCK, false)
                } else {
                    val savedBlock = getBlockStateFromSingleArray(blockStates, x, y, z)
                    val saveOppositeBlockAbove = if (y > chunk.world.minHeight) getBlockStateFromSingleArray(blockStates, x, y - 1, z) else null

                    var alreadySet = false
                    if (saveOppositeBlockAbove != null && (saveOppositeBlockAbove.type == Material.AIR || saveOppositeBlockAbove.type == Material.WATER)) {
                        alreadySet = true

                        when (savedBlock.type) {
                            Material.SAND -> block.setType(Material.SANDSTONE, false)
                            Material.GRAVEL -> block.setType(Material.STONE, false)
                            Material.CLAY -> block.setType(Material.STONE, false)
                            Material.RED_SAND -> block.setType(Material.RED_SANDSTONE, false)
                            else -> alreadySet = false
                        }
                    } else if (saveOppositeBlockAbove != null && saveOppositeBlockAbove.type == Material.SNOW) {
                        alreadySet = true

                        when (savedBlock.type) {
                            Material.DIRT -> block.setType(Material.SNOW_BLOCK, false)
                            Material.GRASS_BLOCK -> block.setType(Material.SNOW_BLOCK, false)
                            Material.SAND -> block.setType(Material.SNOW_BLOCK, false)
                            Material.GRAVEL -> block.setType(Material.SNOW_BLOCK, false)
                            Material.CLAY -> block.setType(Material.SNOW_BLOCK, false)
                            Material.STONE -> block.setType(Material.SNOW_BLOCK, false)
                            else -> alreadySet = false
                        }
                    }

                    if (!alreadySet) {
                        when (savedBlock.type) {
                            Material.GRASS_BLOCK -> block.setType(Material.MOSS_BLOCK, false)
                            Material.SNOW -> block.setType(Material.AIR, false)
                            else -> {
                                block.setType(savedBlock.type, false)
                                block.setBlockData(savedBlock.data, false)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getBlockStateFromSingleArray(array: ArrayList<BlockTypeData>, x: Int, y: Int, z: Int): BlockTypeData {
    return array[((y - LangzeitProperties.WORLD.minHeight) * 256) + (x * 16) + z]

}

private fun processNextChunkInQueue() {
    if (taskQueue.isEmpty()) {
        paused = true
        LangzeitPlugin.LOGGER.info("WORLD_GEN: ${progress}/${total} chunks processed, DONE now")

        LangzeitProperties.WORLD.worldBorder.size = LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION * 2.0 - 2.0
        LangzeitProperties.WORLD.worldBorder.setCenter(0.0, 0.0)

        val progressFile = File("worldgen_progress.txt")
        progressFile.delete()

        return
    }
    val nextChunk = taskQueue.remove()
    val chunk = LangzeitProperties.WORLD.getChunkAt(nextChunk.x, nextChunk.z)
    chunk.load()

    progress++

    if (chunk.getBlock(0, chunk.world.maxHeight - 1, 0).type != Material.BEDROCK) {
        processChunk(chunk, nextChunk.emptyChunk)
        realProcessed++

        bossBarManager.setForAll(
            literalText { whiteText("Chunk (${chunk.x}/${chunk.z}) wird generiert... (Fortschritt: ${progress}/${total})") },
            progress.toFloat() / total.toFloat(),
            BossBar.Color.YELLOW
        )
        LangzeitPlugin.LOGGER.info("WORLD_GEN: ${progress}/${total} chunks processed")

        if (realProcessed % 100 == 0L) {
            paused = true
            taskRunLater(1, true) {
                bossBarManager.setForAll(
                    literalText { whiteText("Zwischenstand wird gespeichert... (Fortschritt: ${progress}/${total})") },
                    progress.toFloat() / total.toFloat(),
                    BossBar.Color.BLUE
                )
            }

            taskRunLater(2, true) {
                LangzeitPlugin.LOGGER.info("WORLD_GEN: +++++ Saving world and current progress...")
                val progressFile = File("worldgen_progress.txt")
                progressFile.writeText("${progress.toInt()}")

                LangzeitProperties.WORLD.save()
                paused = false

                if (realProcessed > 2000) {
                    Bukkit.getConsoleSender().dispatchCommand("restart")
                }
            }
        }
    } else {
        skipped++

        bossBarManager.setForAll(
            literalText { whiteText("Chunk (${chunk.x}/${chunk.z}) wird geskippt... (Fortschritt: ${progress}/${total})") },
            progress.toFloat() / total.toFloat(),
            BossBar.Color.YELLOW
        )
    }
}

fun processChunksInSpiral() {
    var x = 0
    var z = 0
    var dx = 1
    var dz = 0
    var segmentLength = 1
    var step = 0
    var directionChanges = 0

    // Process the center chunk first
    taskQueue.add(ChunkData(x, z))

    test@ while (true) {
        for (i in 0 until segmentLength) {
            x += dx
            z += dz

            if (abs(x * 16) > LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION + 32 || abs(z * 16) > LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION + 32) {
                break@test  // Stop if we've reached the boundary
            }

            var emptyChunk = false
            if (abs(x * 16) > LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION || abs(z * 16) > LangzeitProperties.MAX_BLOCKS_EACH_DIRECTION) {
                emptyChunk = true
            }

            taskQueue.add(ChunkData(x, z, emptyChunk))
            step++
        }

        // Change direction: right -> down -> left -> up
        val temp = dx
        dx = -dz
        dz = temp

        directionChanges++

        // Every two direction changes, increase the segment length
        if (directionChanges % 2 == 0) {
            segmentLength++
        }
    }

    total = step.toLong()
}
