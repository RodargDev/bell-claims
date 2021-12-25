package xyz.mizarc.solidclaims.commands

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import org.bukkit.entity.Player
import xyz.mizarc.solidclaims.ChatInfoBuilder
import xyz.mizarc.solidclaims.claims.Claim
import xyz.mizarc.solidclaims.claims.ClaimPartition
import kotlin.math.absoluteValue

@CommandAlias("claim")
class InfoCommand : ClaimCommand() {

    @Subcommand("info")
    fun onClaiminfo(player: Player) {
        val claimPartition = plugin.claimContainer.getClaimPartitionAtLocation(player.location)

        // Check if there is a claim at the player's location
        if (claimPartition == null) {
            player.sendMessage("There is no claim partition at your current location.")
            return
        }

        val claim = claimPartition.claim
        val name = if (claim.name != null) {
            claim.name
        } else {
            "Claim"
        }

        val chatInfo = ChatInfoBuilder("$name Summary")
        if (claim.description != null) {
            chatInfo.addParagraph("${claim.description}")
            chatInfo.addSpace()
        }
        chatInfo.addLinked("Owner", claim.owner.name.toString())
        chatInfo.addLinked("Creation Date", "123129")
        chatInfo.addLinked("Partition Count", claim.claimPartitions.count().toString())
        chatInfo.addLinked("Block Count", claim.getBlockCount().toString())
        chatInfo.addLinked("Trusted Users", claim.playerAccesses.count().toString())
        chatInfo.addSpace()
        chatInfo.addHeader("Current Partition")
        chatInfo.addLinked("First Corner", claimPartition.firstPosition.toString())
        chatInfo.addLinked("Second Corner", claimPartition.secondPosition.toString())
        chatInfo.addLinked("Block Count", claimPartition.getBlockCount().toString())

        player.spigot().sendMessage(*chatInfo.create())
    }
}