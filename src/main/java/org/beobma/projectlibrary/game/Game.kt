@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.game

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.game.GameManager.Companion.teams
import org.beobma.projectlibrary.info.Info
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

data class Game(
    val players: List<Player>,
    val floor: LibraryFloor = LibraryFloor.GeneralWorks,
    var act: Int = 0,
    val playerMainBookShelf: MutableMap<Player, MainBookShelf> = mutableMapOf(),
    var blueTeamScore: Int = 0,
    var redTeamScore: Int = 0,
    var musicBukkitScheduler: BukkitTask? = null
) {
    fun start() {
        Info.game = this
        Info.starting = true

        Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 잠시 후 게임을 준비합니다.")
        broadcastDelayedMessages(
            listOf(
                "${ChatColor.BOLD}[!] 해당 플러그인과 맵, 리소스팩은 BEOBMA에 의해 개발되었으며 2차 창작물로 분류됩니다.",
                "${ChatColor.BOLD}[!] 해당 플러그인에 대한 무단 수정, 배포 등을 금지합니다.",
                "${ChatColor.BOLD}[!] 잠시 후 게임을 시작합니다."
            )
        )

        object : BukkitRunnable() {
            override fun run() {
                GameManager().teamPick()
            }
        }.runTaskLater(ProjectLibrary.instance, 240L)
    }

    fun stop() {
        val game = Info.game ?: return
        ProjectLibrary.instance.server.scheduler.cancelTasks(ProjectLibrary.instance)

        game.players.forEach { player ->
            player.inventory.clear()
            player.stopAllSounds()
            player.clearActivePotionEffects()
            player.gameMode = GameMode.ADVENTURE
            player.maxHealth = 20.0
            player.health = 20.0
            val tags = player.scoreboardTags.toList()
            tags.forEach { tag ->
                player.removeScoreboardTag(tag)
            }
            for ((_, team) in teams) {
                team?.entries?.forEach { entry ->
                    team.removeEntry(entry)
                }
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players reset @a")

            player.teleport(Location(player.world, 2.5, -60.0, 0.5, 90f, 0f))
        }

        Info.game = null
        Info.starting = false
        Info.gaming = false
    }


    private fun broadcastDelayedMessages(messages: List<String>) {
        messages.forEachIndexed { index, message ->
            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.broadcastMessage(message)
                }
            }.runTaskLater(ProjectLibrary.instance, 60L * (index + 1))
        }
    }
}

enum class LibraryFloor {
    GeneralWorks, History, TechnologicalSciences, Literature, Art, NaturalSciences, Language, SocialSciences, Philosophy, Religion, Kether
}