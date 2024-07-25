@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.manager

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.event.OnStageStartEvent
import org.beobma.projectlibrary.game.LibraryFloor.Kether
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.listener.*
import org.beobma.projectlibrary.manager.DefaultTeamService.Companion.teams
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.toMainBookShelf
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team

interface GameService {
    fun firstStart()
    fun preparationGame()
    fun preparationGameText()
    fun actEndCheck()
}

class DefaultGameService : GameService {
    override fun firstStart() {
        Info.starting = false
        Info.gaming = true
        val game = Info.game ?: return
        val mainBookShelfManager = MainBookShelfManager(DefaultMainBookShelfHandler())
        val assistantLibrarianBookshelf = mainBookShelfManager.createAssistantLibrarianBookshelf()
        game.players.forEach { player ->
            assistantLibrarianBookshelf.copy().toItem().toMainBookShelf().set(player)
            game.mapBukkitScheduler[player] = mutableMapOf()
        }
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] game reset end")
        preparationGame()
    }

    override fun preparationGame() {
        val game = Info.game ?: return
        game.act++
        OnDamageEvent().objectReset()
        if (game.act >= 4) {
            game.act = 1
            if (game.floor != Kether) {
                CompensationManager(DefaultCompensationHandler()).run {
                    game.players.forEach { mainBookShelfCompensation(it) }
                }
                return
            }
        } else {
            game.players.forEach { player ->
                val abnormalStatusManager = AbnormalStatusManager()
                val unableMoveManager = abnormalStatusManager.createUnableMoveManager()
                val unableAttackManager = abnormalStatusManager.createUnableAttackManager()
                val bleedingManager = abnormalStatusManager.createBleedingManager()
                val smokeManager = abnormalStatusManager.createSmokeManager()
                val chargingManager = abnormalStatusManager.createChargingManager()

                bleedingManager.run {
                    player.removeBleeding(Int.MAX_VALUE)
                }

                smokeManager.run {
                    player.removeSmoke(Int.MAX_VALUE)
                }

                chargingManager.run {
                    player.removeCharging(Int.MAX_VALUE)
                }

                val playerBookShelf = player.getMainBookShelf() ?: return
                playerBookShelf.abnormalityCards.remove(playerBookShelf.abnormalityCards.find {
                    it.name == "눈보라" || it.name == "덩굴" || it.name == "마탄" || it.name == "흑염" || it.name == "음악" || it.name == "이계 너머의 메아리" || it.name == "반복되는 연주"
                })

                player.scoreboardTags.remove("isDeath")
                playerBookShelf.emotion = 0
                playerBookShelf.minDiceWeight = 0
                playerBookShelf.maxDiceWeight = 0
                playerBookShelf.disheveled = playerBookShelf.maxDisheveled
                playerBookShelf.set(player)
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.attack_speed base set 4.0"
                )
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "attribute ${player.name} minecraft:generic.movement_speed base set 0.10000000149011612"
                )
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "attribute ${player.name} minecraft:generic.attack_damage base set 1.0"
                )
                player.gameMode = GameMode.ADVENTURE
                unableMoveManager.run { player.addUnableMove() }
                unableAttackManager.run { player.addUnableAttack() }
            }
            FloorManager(DefaultFloorHandler()).handleCurrentFloor()
        }
        preparationGameText()
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] ${game.floor} floor ${game.act} act")
    }

    override fun preparationGameText() {
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 5초 뒤 무대를 시작합니다.")
                object : BukkitRunnable() {
                    override fun run() {
                        stageStart()
                    }
                }.runTaskLater(ProjectLibrary.instance, 100L)
            }
        }.runTaskLater(ProjectLibrary.instance, 40L)
    }

    override fun actEndCheck() {
        val redTeamList = teams["RedTeam"]?.players?.filter { it.player?.gameMode == GameMode.ADVENTURE }
        val blueTeamList = teams["BlueTeam"]?.players?.filter { it.player?.gameMode == GameMode.ADVENTURE }

        if (redTeamList.isNullOrEmpty() || blueTeamList.isNullOrEmpty()) {
            actEnd(if (redTeamList.isNullOrEmpty()) teams["RedTeam"] else teams["BlueTeam"])
            ProjectLibrary.instance.loggerInfo("[ProjectLibray] act end")
        }
    }

    private fun stageStart() {
        val game = Info.game ?: return
        MusicManager(DefaultMusicHandler()).musicDisk()
        game.players.forEach { player ->
            val abnormalStatusManager = AbnormalStatusManager()
            val unableMoveManager = abnormalStatusManager.createUnableMoveManager()
            val unableAttackManager = abnormalStatusManager.createUnableAttackManager()
            player.sendTitle(
                "${ChatColor.BOLD}막의 시작", "${ChatColor.BOLD}Fight", 20, 40, 20
            )
            player.getMainBookShelf()?.light(1)
            unableMoveManager.run { player.removeUnableMove() }
            unableAttackManager.run { player.removeUnableAttack() }
        }

        ProjectLibrary.instance.server.pluginManager.callEvent(OnStageStartEvent())
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] act start")
    }

    private fun actEnd(team: Team?) {
        val game = Info.game ?: return
        game.stageBukkitScheduler.forEach {
            it.cancel()
        }
        game.stageEndBukkitScheduler.forEach {
            it.invoke()
        }
        game.players.forEach { player ->
            game.mapBukkitScheduler[player]?.forEach { (_, u) ->
                u.cancel()
            }
        }
        game.stageEvent.clear()
        game.stageBukkitScheduler.clear()
        game.mapBukkitScheduler.clear()
        game.stageEndBukkitScheduler.clear()
        if (team == null) {
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 무승부, 양팀 점수 없음.")
        } else {
            when (team) {
                teams["RedTeam"] -> {
                    game.redTeamScore++
                    Bukkit.broadcastMessage("${ChatColor.DARK_RED}${ChatColor.BOLD}[!] 레드팀 승, +1점")
                }

                teams["BlueTeam"] -> {
                    game.blueTeamScore++
                    Bukkit.broadcastMessage("${ChatColor.DARK_BLUE}${ChatColor.BOLD}[!] 블루팀 승, +1점")
                }
            }
        }
        Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 현재 점수:")
        Bukkit.broadcastMessage("${ChatColor.DARK_RED}${ChatColor.BOLD}[!] 레드팀 ${game.redTeamScore}점")
        Bukkit.broadcastMessage("${ChatColor.DARK_BLUE}${ChatColor.BOLD}[!] 블루팀 ${game.blueTeamScore}점")
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] ${game.redTeamScore} red score")
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] ${game.blueTeamScore} blue score")
        CompensationManager(DefaultCompensationHandler()).compensationCheck()
    }
}

class GameManager(private val converter: GameService) {
    fun firstStart() {
        converter.firstStart()
    }

    fun preparationGame() {
        converter.preparationGame()
    }

    fun preparationGameText() {
        converter.preparationGameText()
    }

    fun actEndCheck() {
        converter.actEndCheck()
    }
}