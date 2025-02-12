package com.mgvpri.fabo.langzeit.claim

import com.mgvpri.fabo.langzeit.LangzeitPlugin
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

object ClaimConfig {
    private const val CONFIG_FILE = "./config/claim-config.yml"
    private val config: YamlConfiguration
    private var claimUsers: MutableMap<UUID, ClaimOwner?> = mutableMapOf()

    init {
        val file = File(CONFIG_FILE)
        config = YamlConfiguration.loadConfiguration(file)

        config.getConfigurationSection("users")?.getKeys(false)?.forEach { key ->
            val uuid = UUID.fromString(key)
            val friends = config.getStringList("users.$key.friends").map { UUID.fromString(it) }.toMutableList()
            val claims = config.getStringList("users.$key.claims").map {
                val split = it.split(";")
                ClaimEntry(split[0], split[1].toInt(), split[2].toInt())
            }.toMutableList()

            claimUsers[uuid] = ClaimOwner(claims, friends)
        }

        val users = config.getConfigurationSection("users")?.getKeys(false)?.size ?: 0
        if (users > 0) {
            LangzeitPlugin.LOGGER.info("Loaded $users users from claim config.")
        } else {
            LangzeitPlugin.LOGGER.info("No users found in claim config.")
        }
    }

    fun getUserClaims(uuid: UUID): List<ClaimEntry> {
        return claimUsers.getOrPut(uuid) { ClaimOwner() }!!.claims
    }

    fun addUserClaim(uuid: UUID, claim: ClaimEntry) {
        claimUsers.getOrPut(uuid) { ClaimOwner() }!!.claims.add(claim)
        saveConfig()
    }

    fun removeUserClaim(uuid: UUID, claim: ClaimEntry) {
        claimUsers[uuid]!!.claims.remove(claim)
        saveConfig()
    }

    fun getFriendsAddedForUser(uuid: UUID): List<UUID> {
        return claimUsers.filter { it.value?.friends?.contains(uuid) == true }.keys.toList()
    }

    fun getUserFriends(uuid: UUID): List<UUID> {
        return claimUsers.getOrPut(uuid) { ClaimOwner() }!!.friends
    }

    fun addUserFriend(uuid: UUID, friend: UUID) {
        claimUsers.getOrPut(uuid) { ClaimOwner() }!!.friends.add(friend)
        saveConfig()
    }

    fun removeUserFriend(uuid: UUID, friend: UUID) {
        claimUsers[uuid]!!.friends.remove(friend)
        saveConfig()
    }

    fun getClaimOwner(claim: ClaimEntry): UUID? {
        return claimUsers.entries.find { it.value!!.claims.contains(claim) }?.key
    }

    fun getClaimOwner(location: Location): UUID? {
        val claimEntry = location.chunk.let { ClaimEntry(it.world.name, it.x, it.z) }
        return getClaimOwner(claimEntry)
    }

    fun saveConfig() {
        config.set("users", listOf<String>())

        claimUsers.forEach { (uuid, claimOwner) ->
            val key = uuid.toString()
            config.set("users.$key.friends", claimOwner!!.friends.map { it.toString() })
            config.set("users.$key.claims", claimOwner.claims.map { "${it.worldName};${it.chunkX};${it.chunkZ}" })
        }

        config.save(File(CONFIG_FILE))
    }
}

data class ClaimEntry(
    val worldName: String,
    val chunkX: Int,
    val chunkZ: Int,
)

data class ClaimOwner(
    val claims: MutableList<ClaimEntry> = mutableListOf(),
    val friends: MutableList<UUID> = mutableListOf()
)