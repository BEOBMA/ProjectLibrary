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

        ProjectLibrary.loggerInfo("[ProjectLibrary] game start success")
        Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 잠시 후 게임을 준비합니다.")
        broadcastDelayedMessages(
            listOf(
                "${ChatColor.BOLD}[!] 해당 플러그인과 맵, 리소스팩은 BEOBMA에 의해 개발되었으며 2차 창작물로 분류됩니다.",
                "${ChatColor.BOLD}[!] 해당 플러그인에 대한 무단 수정, 배포 등을 금지합니다.",
                "${ChatColor.BOLD}[!] 잠시 후 게임을 시작합니다."
            ), 60L
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
            resetPlayer(player)
        }

        teams.forEach { (_, team) ->
            team?.entries?.forEach { entry ->
                team.removeEntry(entry)
            }
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players reset @a")

        Info.game = null
        Info.starting = false
        Info.gaming = false

        ProjectLibrary.loggerInfo("[ProjectLibrary] game stop success")
    }

    private fun resetPlayer(player: Player) {
        player.apply {
            inventory.clear()
            stopAllSounds()
            clearActivePotionEffects()
            gameMode = GameMode.ADVENTURE
            maxHealth = 20.0
            health = 20.0
            scoreboardTags.toList().forEach { tag ->
                removeScoreboardTag(tag)
            }
            teleport(Location(world, 2.5, -60.0, 0.5, 90f, 0f))
        }
    }

    private fun broadcastDelayedMessages(messages: List<String>, delay: Long) {
        messages.forEachIndexed { index, message ->
            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.broadcastMessage(message)
                }
            }.runTaskLater(ProjectLibrary.instance, delay * (index + 1))
        }
    }
}

enum class LibraryFloor {
    GeneralWorks, History, TechnologicalSciences, Literature, Art, NaturalSciences, Language, SocialSciences, Philosophy, Religion, Kether
}
