package xyz.mizarc.solidclaims.claims

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.World
import xyz.mizarc.solidclaims.events.ClaimPermission
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList

/**
 * A claim object holds the data for the world its in and the players associated with it. It relies on partitions to
 * define its shape.
 * @constructor Compiles an existing claim with associated ID and trusted players.
 * @property id The unique identifier for the claim.
 * @property worldId the unique identifier for the world.
 * @property owner A reference to the owning player.
 * @property defaultPermissions The permissions of this claim for all players
 * @property playerAccesses A list of trusted players.
 * @property claimPartitions The partitions linked to this claim.
 */
class Claim(var id: UUID, var worldId: UUID, var owner: OfflinePlayer, val creationTime: Instant,
            var name: String?, var description: String?, var defaultPermissions: ArrayList<ClaimPermission>,
            var playerAccesses: ArrayList<PlayerAccess>, var claimPartitions: ArrayList<ClaimPartition>,
            var mainPartition: ClaimPartition?) {
    /**
     * Compiles a new claim based on the world and owning player.
     * @param worldId The unique identifier of the world the claim is to be made in.
     * @param owner A reference to the owning player.
     */
    constructor(worldId: UUID, owner: OfflinePlayer, creationTime: Instant) : this(
        UUID.randomUUID(), worldId, owner, creationTime, null, null,
        ArrayList(), ArrayList(), ArrayList(), null)

    /**
     * Compiles a new claim based on everything but the claim partitions.
     * @param id The unique identifier for the claim.
     * @param worldId The unique identifier of the world the claim is to be made in.
     * @param owner A reference to the owning player.
     * @param defaultPermissions The permissions of this claim for all players
     * @param playerAccesses A list of trusted players.
     */
    constructor(id: UUID, worldId: UUID, owner: OfflinePlayer, creationTime: Instant,
                defaultPermissions: ArrayList<ClaimPermission>, playerAccesses: ArrayList<PlayerAccess>) : this(
        id, worldId, owner, creationTime, null, null, defaultPermissions, playerAccesses, ArrayList(), null)

    /**
     * Gets a reference to the world if available.
     * @return The World object that the claim exists in. May return null if the world isn't loaded.
     */
    fun getWorld() : World? {
        return Bukkit.getWorld(worldId)
    }

    fun getBlockCount() : Int {
        var count = 0
        for (partition in claimPartitions) {
            count += partition.getBlockCount()
        }
        return count
    }
}