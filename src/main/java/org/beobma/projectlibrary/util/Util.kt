@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.util

import net.kyori.adventure.Adventure
import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.info.SetUp
import org.beobma.projectlibrary.manager.DefaultTeamService.Companion.teams
import org.beobma.projectlibrary.util.Util.getTeam
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Score
import org.bukkit.scoreboard.Team
import kotlin.random.Random

object Util {
    fun Player.isParticipation(): Boolean {
        if (!Info.isGaming()) return false
        if (player == null) return false
        if (player!!.isDead) return false
        if (player !in Info.game!!.players) return false
        if (player !in Bukkit.getOnlinePlayers()) return false

        return true
    }

    fun Player.isTeam(team: String): Boolean {
        if (player != null) {
            return teams[team]?.hasEntry(player!!.name) == true
        }
        return false
    }

    fun Player.getTeam(): String {
        return if (this.isTeam("RedTeam")) {
            "RedTeam"
        } else {
           "BlueTeam"
        }
    }

    fun Player.getMainBookShelf(): MainBookShelf? {
        return Info.game!!.playerMainBookShelf[player]
    }

    fun ItemStack.toMainBookShelf(): MainBookShelf {
        val lore = this.itemMeta.lore as List<String>

        val mainBookShelf = SetUp.mainBookShelfList.toList().find { it.name.trim() == lore.first().trim() }
        val newMainBookShelf = mainBookShelf?.deepCopyMainBookShelf(mainBookShelf)
        if (newMainBookShelf !is MainBookShelf) {
            return MainBookShelf(
                "보조사서 책장",
                Rating.Supply,
                30.0,
                30.0,
                15,
                15,
                1,
                3,
                mutableListOf(),
                ItemStack(Material.WOODEN_SWORD, 1).apply {
                    itemMeta = itemMeta.apply {
                        setDisplayName("${ChatColor.BOLD}목검")
                        this.lore = arrayListOf(
                            "${ChatColor.GRAY}가장 기본적인 검."
                        )
                        isUnbreakable = true
                    }
                },
                AttackType.Slashing
            )
        }
        return newMainBookShelf
    }

    fun Int.isTrueWithProbability(): Boolean {
        return Random.nextInt(100) < this
    }

    fun Double.increaseByPercentage(percentage: Int): Double {
        return (this + (this * percentage / 100))
    }

    fun Double.percentageOf(percentage: Int): Double {
        return (this * percentage / 100)
    }

    fun Player.getScore(objective: String): Score {
        return this.scoreboard.getObjective(objective)!!.getScore(this.name)
    }

    fun isInsideRegion(location: Location, minPoint: Location, maxPoint: Location): Boolean {
        return (location.x in minPoint.x..maxPoint.x && location.y in minPoint.y..maxPoint.y && location.z in minPoint.z..maxPoint.z)
    }

    fun shootLaser(player: Player): Entity? {
        val world = player.world

        val rayTraceResult = world.rayTraceEntities(
            player.eyeLocation, player.eyeLocation.direction, 100.0, 0.1
        ) {
            it != player
        }

        return rayTraceResult?.hitEntity
    }

    fun getRandomNumberBetween(n1: Int, n2: Int, playerMainBookShelf: MainBookShelf): Int {
        var (min, max) = if (n1 <= n2) n1 to n2 else n2 to n1
        min += playerMainBookShelf.minDiceWeight
        max += playerMainBookShelf.maxDiceWeight
        if (min <= 0) {
            min = 0
        }
        return Random.nextInt(min, max + 1)
    }

    fun Player.myTeamPlayer(): List<Player> {
        val game = Info.game ?: return listOf()
        val team = this.getTeam()
        return game.players.filter { it.isTeam(team) }
    }

    fun Player.enemyTeamPlayer(): List<Player> {
        val game = Info.game ?: return listOf()
        val team = this.getTeam()
        return game.players.filter { !it.isTeam(team) }
    }
}