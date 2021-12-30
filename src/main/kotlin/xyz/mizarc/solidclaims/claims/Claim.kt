package xyz.mizarc.solidclaims.claims

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.World
import xyz.mizarc.solidclaims.events.ClaimPermission
import java.time.Instant
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
 * @property partitions The partitions linked to this claim.
 */
class Claim(var id: UUID, var worldId: UUID, var owner: OfflinePlayer, val creationTime: Instant,
            var name: String?, var description: String?, var defaultPermissions: ArrayList<ClaimPermission>,
            var playerAccesses: ArrayList<PlayerAccess>, var partitions: ArrayList<Partition>,
            var mainPartition: Partition?) {
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
    constructor(id: UUID, worldId: UUID, owner: OfflinePlayer, creationTime: Instant, name: String?,
                description: String?, defaultPermissions: ArrayList<ClaimPermission>,
                playerAccesses: ArrayList<PlayerAccess>) : this(id, worldId, owner, creationTime, name, description,
        defaultPermissions, playerAccesses, ArrayList(), null)

    /**
     * Gets a reference to the world if available.
     * @return The World object that the claim exists in. May return null if the world isn't loaded.
     */
    fun getWorld() : World? {
        return Bukkit.getWorld(worldId)
    }

    fun getBlockCount() : Int {
        var count = 0
        for (partition in partitions) {
            count += partition.area.getBlockCount()
        }
        return count
    }

    fun getAdjacentPartitions(partition: Partition): ArrayList<Partition> {
        val adjacentPartitions = ArrayList<Partition>()
        for (existingPartition in partitions) {
            if (existingPartition.isAreaAdjacent(partition.area, partition.claim.getWorld()!!)) {
                adjacentPartitions.add(existingPartition)
            }
        }
        return adjacentPartitions
    }

    fun getLinkedAdjacentPartitions(partition: Partition): ArrayList<Partition> {
        val adjacentPartitions = ArrayList<Partition>()
        for (existingPartition in partitions) {
            if (existingPartition.isAreaAdjacent(partition.area, partition.claim.getWorld()!!) && existingPartition.claim == partition.claim) {
                adjacentPartitions.add(existingPartition)
            }
        }
        return adjacentPartitions
    }

    fun isPartitionConnectedToMain(partition: Partition): Boolean {
        val traversedPartitions = ArrayList<Partition>()
        val partitionQueries = ArrayList<Partition>()
        partitionQueries.add(partition)
        while(partitionQueries.isNotEmpty()) {
            val partitionsToAdd = ArrayList<Partition>()
            val partitionsToRemove = ArrayList<Partition>()
            for (partitionQuery in partitionQueries) {
                val adjacentPartitions = getLinkedAdjacentPartitions(partitionQuery)
                for (adjacentPartition in adjacentPartitions) {
                    if (adjacentPartition.area.lowerPosition == mainPartition!!.area.lowerPosition &&
                        adjacentPartition.area.upperPosition == mainPartition!!.area.upperPosition) {
                        return true
                    }
                    if (adjacentPartition in traversedPartitions) {
                        continue
                    }

                    partitionsToAdd.add(adjacentPartition)
                }
                partitionsToRemove.add(partitionQuery)
                traversedPartitions.add(partitionQuery)
            }
            partitionQueries.removeAll(partitionsToRemove)
            partitionQueries.addAll(partitionsToAdd)
            partitionsToAdd.clear()
        }
        return false
    }

    fun isAnyDisconnectedPartitions(): Boolean {
        for (partition in partitions) {
            if (isPartitionMain(partition)) {
                continue
            }
            if (!isPartitionConnectedToMain(partition)) {
                return true
            }
        }
        return false
    }

    fun isPartitionMain(partition: Partition): Boolean {
        return (partition.claim.mainPartition!!.area.lowerPosition == partition.area.lowerPosition &&
            partition.claim.mainPartition!!.area.upperPosition == partition.area.upperPosition)
    }
}