package dev.mizarc.bellclaims.api

import dev.mizarc.bellclaims.domain.players.PlayerState
import org.bukkit.OfflinePlayer
import java.util.UUID

interface PlayerStateService {
    fun getAllOnline(): PlayerState
    fun getById(id: UUID): PlayerState
    fun getTotalClaimCount(player: OfflinePlayer): Int
    fun getTotalClaimBlockCount(player: OfflinePlayer): Int
    fun getUsedClaimsCount(player: OfflinePlayer): Int
    fun getUsedClaimBlockCount(player: OfflinePlayer): Int
    fun getRemainingClaimCount(player: OfflinePlayer): Int
    fun getRemainingClaimBlockCount(player: OfflinePlayer): Int
}