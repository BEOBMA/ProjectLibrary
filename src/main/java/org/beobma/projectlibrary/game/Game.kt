@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.game

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.listener.OnDamageEvent
import org.beobma.projectlibrary.listener.OnPlayerDeathEvet
import org.beobma.projectlibrary.listener.OnPlayerJump
import org.beobma.projectlibrary.listener.OnStageStart
import org.beobma.projectlibrary.manager.DefaultTeamService
import org.beobma.projectlibrary.manager.DefaultTeamService.Companion.teams
import org.beobma.projectlibrary.manager.TeamManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

data class Game(
    var players: List<Player>,
    var floor: LibraryFloor = LibraryFloor.GeneralWorks,
    var act: Int = 0,
    val playerMainBookShelf: MutableMap<Player, MainBookShelf> = mutableMapOf(),
    var blueTeamScore: Int = 0,
    var redTeamScore: Int = 0,
    var musicBukkitScheduler: BukkitTask? = null,
    val stageBukkitScheduler: MutableList<BukkitTask> = mutableListOf(),
    val stageEndBukkitScheduler: MutableList<() -> Unit> = mutableListOf(),
    val mapBukkitScheduler: MutableMap<Player, MutableMap<String, BukkitTask>> = mutableMapOf(),
    val stageLognBukkitScheduler: MutableList<BukkitTask> = mutableListOf(),
    val stageEvent: MutableList<String> = mutableListOf()
) {
    fun start() {
        Info.game = this
        Info.starting = true

        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] game start success")
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
                TeamManager(DefaultTeamService()).teamPick()
            }
        }.runTaskLater(ProjectLibrary.instance, 240L)
    }

    fun stop() {
        val game = Info.game ?: return
        ProjectLibrary.instance.server.scheduler.cancelTasks(ProjectLibrary.instance)
        OnDamageEvent().objectReset()

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

        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] game stop success")
    }

    fun resetPlayer(player: Player) {
        player.apply {
            inventory.clear()
            stopAllSounds()
            clearActivePotionEffects()
            gameMode = GameMode.ADVENTURE
            maxHealth = 20.0
            health = 20.0

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.attack_speed base set 4.0")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.movement_speed base set 0.10000000149011612")
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "attribute ${player.name} minecraft:generic.attack_damage base set 1.0"
            )
            scoreboardTags.toList().forEach { tag ->
                removeScoreboardTag(tag)
            }
            player.setPlayerListName(player.name)
            teleport(Location(world, 2.5, -60.0, 0.5, 90f, 0f))
            ProjectLibrary.instance.loggerInfo("[ProjectLibrary] player ${player.name} reset success")
        }
    }

    private fun broadcastDelayedMessages(messages: List<String>) {
        val delay = 60L
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