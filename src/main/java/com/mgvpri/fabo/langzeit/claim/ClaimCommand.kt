package com.mgvpri.fabo.langzeit.claim

import com.mgvpri.fabo.base.rabbitmq.RankConfig
import com.mgvpri.fabo.base.utils.grayText
import com.mgvpri.fabo.base.utils.langzeitInfo
import com.mgvpri.fabo.base.utils.serverError
import com.mgvpri.fabo.base.utils.whiteText
import com.mgvpri.fabo.base.utils.yellowText
import com.mgvpri.fabo.langzeit.special.LangzeitProperties
import com.mojang.brigadier.arguments.StringArgumentType
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.CommandContext
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.literal
import net.axay.kspigot.commands.register
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestList
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.math.abs

const val PLAYER_MAX_CLAIMS = 4
const val SUBSCRIBER_MAX_CLAIMS = 8

enum class HelpType { BASIC, DETAILED, ADMIN }

object ClaimCommand {
    init {
        val spawn = command("spawn", register = false) {
            runs {
                player.teleport(LangzeitProperties.SPAWN_LOCATION)
            }
        }
        spawn.register(true)

        val claim = command("claim", register = false) {
            runs(outputHelp(HelpType.BASIC))
            literal("chunk") {
                runs(listPlayerChunks())
                literal("add") {
                    runs {
                        val claims = ClaimConfig.getUserClaims(player.uniqueId)
                        if (player.isSub() && claims.size >= SUBSCRIBER_MAX_CLAIMS) {
                            player.sendMessage("Du hast bereits die maximale Anzahl an Chunks geclaimt (Subscriber: bis zu 8 Chunks)".serverError())
                        } else if (player.isNormalPlayer() && claims.size >= PLAYER_MAX_CLAIMS) {
                            player.sendMessage("Du hast bereits die maximale Anzahl von 4 Chunks geclaimt (Spieler: bis zu 4 Chunks)".serverError())
                        } else {
                            // Mods and Admins have unlimited Claims
                            val chunk = player.location.chunk
                            val claimEntry = ClaimEntry(player.location.world.name, chunk.x, chunk.z)
                            val owner = ClaimConfig.getClaimOwner(claimEntry)

                            if ((player.isNormalPlayer() || player.isSub()) && abs(chunk.x) < 3 && abs(chunk.z) < 3) {
                                player.sendMessage("Die Chunks von (-2/-2) bis (2/2) können wegen Spawn-Schutz nicht geclaimt werden.".serverError())
                            } else if (owner != null) {
                                player.sendMessage("Dieser Chunk ist bereits von ${Bukkit.getOfflinePlayer(owner).name ?: "unbekanntem Spieler"} geclaimt.".serverError())
                            } else {
                                ClaimConfig.addUserClaim(player.uniqueId, claimEntry)
                                val remainingClaims = (if (player.isSub()) SUBSCRIBER_MAX_CLAIMS else if (player.isNormalPlayer()) PLAYER_MAX_CLAIMS else 1000) - claims.size
                                player.sendMessage("Der Chunk (${chunk.x} / ${chunk.z}) wurde erfolgreich geclaimt. Du hast noch $remainingClaims Chunks zum claimen übrig.".langzeitInfo())
                            }
                        }
                    }
                }
                literal("remove") {
                    runs {
                        val chunk = player.location.chunk
                        val claimEntry = ClaimEntry(player.location.world.name, chunk.x, chunk.z)
                        val claims = ClaimConfig.getUserClaims(player.uniqueId)
                        val owner = ClaimConfig.getClaimOwner(claimEntry)

                        if (owner == null) {
                            player.sendMessage("Dieser Chunk ist nicht geclaimt.".serverError())
                        } else if (owner != player.uniqueId) {
                            player.sendMessage("Dieser Chunk ist von ${Bukkit.getOfflinePlayer(owner).name ?: "unbekanntem Spieler"} geclaimt.".serverError())
                        } else {
                            ClaimConfig.removeUserClaim(player.uniqueId, claimEntry)
                            val remainingClaims = (if (player.isSub()) SUBSCRIBER_MAX_CLAIMS else if (player.isNormalPlayer()) PLAYER_MAX_CLAIMS else 1000) - claims.size
                            player.sendMessage("Der Chunk (${chunk.x} / ${chunk.z}) wurde von deinen Claims entfernt. Du hast jetzt $remainingClaims Chunks zum claimen übrig.".langzeitInfo())
                        }
                    }
                }
                literal("list") {
                    runs(listPlayerChunks())
                }
            }
            literal("friend") {
                runs(listPlayerFriends())
                literal("add") {
                    argument("player", StringArgumentType.word()) {
                        suggestList { Bukkit.getOnlinePlayers().map { it.name } }
                        runs {
                            val friend = Bukkit.getPlayerExact(getArgument<String>("player"))?.uniqueId
                            val friends = ClaimConfig.getUserFriends(player.uniqueId)

                            if (friend == null) {
                                player.sendMessage("Dieser Spieler ist nicht online.".serverError())
                            } else if (friend == player.uniqueId) {
                                player.sendMessage("Du kannst dich nicht selbst als Freund hinzufügen.".serverError())
                            } else if (friends.contains(friend)) {
                                player.sendMessage("Dieser Spieler ist bereits in deiner Freundesliste.".serverError())
                            } else {
                                ClaimConfig.addUserFriend(player.uniqueId, friend)
                                player.sendMessage("Der Spieler ${Bukkit.getOfflinePlayer(friend).name ?: "unbekannter Spieler"} wurde zu deinen Freunden hinzugefügt.".langzeitInfo())
                                Bukkit.getPlayer(friend)?.sendMessage("Der Spieler ${player.name} hat dich zu seinen Freunden hinzugefügt.".langzeitInfo())
                            }
                        }
                    }
                }
                literal("remove") {
                    argument("player", StringArgumentType.word()) {
                        suggestList { Bukkit.getOfflinePlayers().map { it.name } }
                        runs {
                            val friend = Bukkit.getOfflinePlayer(getArgument<String>("player")).uniqueId
                            val friends = ClaimConfig.getUserFriends(player.uniqueId)

                            if (friend == player.uniqueId) {
                                player.sendMessage("Du kannst dich nicht selbst als Freund entfernen.".serverError())
                            } else if (!friends.contains(friend)) {
                                player.sendMessage("Dieser Spieler ist nicht in deiner Freundesliste.".serverError())
                            } else {
                                ClaimConfig.removeUserFriend(player.uniqueId, friend)
                                player.sendMessage("Der Spieler ${Bukkit.getOfflinePlayer(friend).name ?: "unbekannter Spieler"} wurde von deinen Freunden entfernt.".langzeitInfo())
                            }
                        }
                    }
                }
                literal("list") {
                    runs(listPlayerFriends())
                }
            }
            literal("admin") {
                runs(outputHelp(HelpType.ADMIN))
                literal("add") {
                    argument("player", StringArgumentType.word()) {
                        suggestList { Bukkit.getOnlinePlayers().map { it.name } }
                        runs {
                            if (!player.isAdmin()) {
                                player.sendMessage("Du hast keine Berechtigung für diesen Befehl.".serverError())
                                return@runs
                            }

                            val futureChunkHolder = Bukkit.getPlayerExact(getArgument<String>("player"))?.uniqueId
                            val chunk = player.location.chunk
                            val claimEntry = ClaimEntry(player.location.world.name, chunk.x, chunk.z)
                            val owner = ClaimConfig.getClaimOwner(claimEntry)

                            if (futureChunkHolder == null) {
                                player.sendMessage("Dieser Spieler ist nicht online.".serverError())
                            } else if (owner != null) {
                                player.sendMessage("Dieser Chunk ist bereits von ${Bukkit.getOfflinePlayer(owner).name ?: "unbekanntem Spieler"} geclaimt.".serverError())
                            } else {
                                ClaimConfig.addUserClaim(futureChunkHolder, claimEntry)
                                player.sendMessage("Der Chunk (${chunk.x} / ${chunk.z}) wurde erfolgreich für ${Bukkit.getOfflinePlayer(futureChunkHolder).name ?: "unbekanntem Spieler"} geclaimt.".langzeitInfo())
                            }
                        }
                    }
                }
                literal("remove") {
                    runs {
                        if (!player.isAdmin()) {
                            player.sendMessage("Du hast keine Berechtigung für diesen Befehl.".serverError())
                            return@runs
                        }

                        val chunk = player.location.chunk
                        val claimEntry = ClaimEntry(player.location.world.name, chunk.x, chunk.z)
                        val owner = ClaimConfig.getClaimOwner(claimEntry)

                        if (owner == null) {
                            player.sendMessage("Dieser Chunk ist nicht geclaimt.".serverError())
                        } else {
                            ClaimConfig.removeUserClaim(owner, claimEntry)
                            player.sendMessage("Der Chunk (${chunk.x} / ${chunk.z}) wurde von ${Bukkit.getOfflinePlayer(owner).name ?: "unbekanntem Spieler"} entfernt.".langzeitInfo())
                        }
                    }
                }
                literal("stats") {
                    argument("player", StringArgumentType.word()) {
                        suggestList { Bukkit.getOfflinePlayers().map { it.name } }
                        runs {
                            if (!player.isAdmin()) {
                                player.sendMessage("Du hast keine Berechtigung für diesen Befehl.".serverError())
                                return@runs
                            }

                            val target = Bukkit.getOfflinePlayer(getArgument<String>("player")).uniqueId
                            val claims = ClaimConfig.getUserClaims(target)
                            val friends = ClaimConfig.getUserFriends(target)
                            val addedAsFriend = ClaimConfig.getFriendsAddedForUser(target)

                            player.sendMessage(literalText {
                                langzeitInfo()
                                whiteText("Spielerinfos für ${Bukkit.getOfflinePlayer(target).name ?: "unbekannter Spieler"}:\n")
                                whiteText("Geclaimte Chunks:\n")
                                if (claims.isEmpty()) {
                                    grayText("(keine)\n")
                                } else {
                                    claims.forEach {
                                        grayText("- Chunk (${it.chunkX} / ${it.chunkZ}) in ${it.worldName}\n")
                                    }
                                }
                                whiteText("Freunde:\n")
                                if (friends.isEmpty()) {
                                    grayText("(keine)\n")
                                } else {
                                    friends.forEach {
                                        grayText("- ${Bukkit.getOfflinePlayer(it).name ?: "unbekannter Spieler"}\n")
                                    }
                                }
                                whiteText("Der Spieler wurde von folgenden Spielern als Freund hinzugefügt:\n")
                                if (addedAsFriend.isEmpty()) {
                                    grayText("(keine)\n")
                                } else {
                                    addedAsFriend.forEach {
                                        grayText("- ${Bukkit.getOfflinePlayer(it).name ?: "unbekannter Spieler"}\n")
                                    }
                                }
                            })
                        }
                    }
                }
            }
            literal("help") {
                runs(outputHelp(HelpType.DETAILED))
            }
        }
        claim.register(true)
    }
}

private fun listPlayerFriends(): CommandContext.() -> Unit = {
    val friends = ClaimConfig.getUserFriends(player.uniqueId)
    val addedAsFriend = ClaimConfig.getFriendsAddedForUser(player.uniqueId)

    player.sendMessage(literalText {
        langzeitInfo()
        whiteText("Deine Freunde:\n")
        if (friends.isEmpty()) {
            grayText("(keine)\n")
        } else {
            friends.forEach {
                grayText("- ${Bukkit.getOfflinePlayer(it).name ?: "unbekannter Spieler"}\n")
            }
        }
        whiteText("Du wurdest von folgenden Spielern als Freund hinzugefügt:\n")
        if (addedAsFriend.isEmpty()) {
            grayText("(keine)\n")
        } else {
            addedAsFriend.forEach {
                grayText("- ${Bukkit.getOfflinePlayer(it).name ?: "unbekannter Spieler"}\n")
            }
        }
    })
}

private fun listPlayerChunks(): CommandContext.() -> Unit = {
    val claims = ClaimConfig.getUserClaims(player.uniqueId)

    if (claims.isEmpty()) {
        player.sendMessage("Du hast bisher keine Chunks geclaimt.".serverError())
    } else {
        player.sendMessage(literalText {
            langzeitInfo()
            whiteText("Deine geclaimten Chunks:\n")
            claims.forEach {
                grayText("- Chunk (${it.chunkX} / ${it.chunkZ}) in ${it.worldName}\n")
            }
        })
    }
}

private fun outputHelp(helpType: HelpType): CommandContext.() -> Unit = {
    val isAdminPlayer = player.isAdmin()

    val message = when (helpType) {
        HelpType.BASIC -> literalText {
            langzeitInfo()
            whiteText("Claim-System:\n\n")
            yellowText("/claim help")
            grayText(" - Erklärt alle Befehle zum Claim-System\n")
            yellowText("/claim chunk")
            grayText(" - Verwalte deine Chunks\n")
            yellowText("/claim friend")
            grayText(" - Verwalte deine Freunde, die auf deinen Chunks mitbauen dürfen\n")
            yellowText("/spawn")
            grayText(" - Teleportiere dich zum Spawn\n")
            if (isAdminPlayer) {
                yellowText("/claim admin")
                grayText(" - Admin-Befehle für Mods/Admins\n")
            }
        }
        HelpType.DETAILED -> literalText {
            langzeitInfo()
            whiteText("Alle Befehle für das Claim-System:\n\n")
            yellowText("/claim help")
            grayText(" - Erklärt alle Befehle zum Claim-System\n")
            yellowText("/claim chunk add")
            grayText(" - Sichere dir den Chunk, in dem du gerade stehst\n")
            yellowText("/claim chunk remove")
            grayText(" - Entferne den Chunk, in dem du gerade stehst\n")
            yellowText("/claim chunk list")
            grayText(" - Zeige alle geclaimten Chunks an\n")
            yellowText("/claim friend add <Spieler>")
            grayText(" - Füge einen Spieler zu deinen Freunden hinzu\n")
            yellowText("/claim friend remove <Spieler>")
            grayText(" - Entferne einen Spieler von deinen Freunden\n")
            yellowText("/claim friend list")
            grayText(" - Zeige alle Freunde an\n")
            yellowText("/spawn")
            grayText(" - Teleportiere dich zum Spawn\n")
            if (isAdminPlayer) {
                yellowText("/claim admin add <Spieler>")
                grayText(" - Sichert den aktuellen Chunk für einen Spieler (kein Limit)\n")
                yellowText("/claim admin remove")
                grayText(" - Entfernt den aktuellen Chunk für den dazugehörigen Spieler\n")
                yellowText("/claim admin stats <Spieler>")
                grayText(" - Zeigt alle Spielerinfos an\n")
            }
        }
        HelpType.ADMIN -> literalText {
            if (isAdminPlayer) {
                langzeitInfo()
                whiteText("Alle Admin-Befehle für das Claim-System:\n\n")
                yellowText("/claim admin add <Spieler>")
                grayText(" - Sichert den aktuellen Chunk für einen Spieler (kein Limit)\n")
                yellowText("/claim admin remove")
                grayText(" - Entfernt den aktuellen Chunk für den dazugehörigen Spieler\n")
                yellowText("/claim admin stats <Spieler>")
                grayText(" - Zeigt alle Spielerinfos an\n")
            } else {
                serverError()
                whiteText("Du hast keine Berechtigung für diesen Befehl.\n")
            }
        }
    }
    player.sendMessage(message)
}

private fun Player.isSub() = listOf(RankConfig.Rank.SUBSCRIBER, RankConfig.Rank.FAMOUS, RankConfig.Rank.BUILDER).contains(RankConfig.getRank(this))
private fun Player.isNormalPlayer() = RankConfig.getRank(this) == RankConfig.Rank.PLAYER
private fun Player.isAdmin() = listOf(RankConfig.Rank.MODERATOR, RankConfig.Rank.ADMIN).contains(RankConfig.getRank(this)) || isOp