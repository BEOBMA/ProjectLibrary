@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.manager

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.game.LibraryFloor.*
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.isTeam
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

interface FloorHandler {
    fun advanceFloor()
    fun handleCurrentFloor()
}

class DefaultFloorHandler : FloorHandler {
    override fun advanceFloor() {
        Info.game!!.musicBukkitScheduler?.cancel()
        Info.game!!.musicBukkitScheduler = null
        val converter = DefaultMainBookShelfHandler()
        val mainBookShelfManager = MainBookShelfManager(converter)
        Info.game!!.players.forEach { player ->
            val abnormalStatusManager = AbnormalStatusManager()
            val unableMoveManager = abnormalStatusManager.createUnableMoveManager()
            val unableAttackManager = abnormalStatusManager.createUnableAttackManager()

            player.stopAllSounds()
            player.scoreboardTags.remove("isDeath")
            player.getMainBookShelf()?.emotion = 0
            player.getMainBookShelf()?.light = 0
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.attack_speed base set 4.0"
            )
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "attribute ${player.name} minecraft:generic.movement_speed base set 0.10000000149011612"
            )
            unableMoveManager.run { player.addUnableMove() }
            unableAttackManager.run { player.addUnableAttack() }
            player.gameMode = GameMode.ADVENTURE
            player.getMainBookShelf()?.set(player) ?: run {
                mainBookShelfManager.createAssistantLibrarianBookshelf().set(player)
            }
        }
        when (Info.game!!.floor) {
            GeneralWorks -> handleHistory()
            History -> handleTechnologicalSciences()
            TechnologicalSciences -> handleLiterature()
            Literature -> handleArt()
            Art -> handleNaturalSciences()
            NaturalSciences -> handleLanguage()
            Language -> handleSocialSciences()
            SocialSciences -> handlePhilosophy()
            Philosophy -> handleReligion()
            Religion -> handleKether()
            Kether -> handleEnd()
        }
        GameManager(DefaultGameService()).preparationGameText()
    }

    override fun handleCurrentFloor() {
        when (Info.game!!.floor) {
            GeneralWorks -> handleGeneralWorks()
            History -> handleHistory()
            TechnologicalSciences -> handleTechnologicalSciences()
            Literature -> handleLiterature()
            Art -> handleArt()
            NaturalSciences -> handleNaturalSciences()
            Language -> handleLanguage()
            SocialSciences -> handleSocialSciences()
            Philosophy -> handlePhilosophy()
            Religion -> handleReligion()
            Kether -> handleKether()
        }
    }

    private fun handleGeneralWorks() {
        Info.game!!.floor = GeneralWorks
        handleFloorChange("총류의 층", "#FFFFFF", Location(Info.world, -343.5, 81.0, -71.5, -90f, 0f), Location(Info.world, -231.5, 81.0, -71.5, 90f, 0f))
    }

    private fun handleHistory() {
        Info.game!!.floor = History
        handleFloorChange("역사의 층", "#ddcc55", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f))
    }

    private fun handleTechnologicalSciences() {
        Info.game!!.floor = TechnologicalSciences
        handleFloorChange(
            "기술과학의 층", "#be90d5", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f)
        )
    }

    private fun handleLiterature() {
        Info.game!!.floor = Literature
        handleFloorChange("문학의 층", "#dd8833", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f))
    }

    private fun handleArt() {
        Info.game!!.floor = Art
        handleFloorChange("예술의 층", "#669944", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f))
    }

    private fun handleNaturalSciences() {
        Info.game!!.floor = NaturalSciences
        handleFloorChange(
            "자연과학의 층", "#ffff00", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f)
        )
    }

    private fun handleLanguage() {
        Info.game!!.floor = Language
        handleFloorChange("언어의 층", "#e54d4d", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f))
    }

    private fun handleSocialSciences() {
        Info.game!!.floor = SocialSciences
        handleFloorChange(
            "사회과학의 층", "#7198ff", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f)
        )
    }

    private fun handlePhilosophy() {
        Info.game!!.floor = Philosophy
        handleFloorChange("철학의 층", "#D4AF37", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f))
    }

    private fun handleReligion() {
        Info.game!!.floor = Religion
        handleFloorChange("종교의 층", "#bbbbbb", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f))
    }

    private fun handleKether() {
        Info.game!!.floor = Kether
        handleFloorChange(
            "총류의 층", "#FFFFFF", Location(Info.world, -88.5, 81.0, -71.0, -90f, 0f), Location(Info.world, -7.5, 81.0, -71.0, 90f, 0f), "Finale"
        )
    }

    private fun handleFloorChange(
        title: String,
        color: String,
        redTeamStartPoint: Location,
        blueTeamStartPoint: Location,
        actTitle: String = "제 ${Info.game!!.act}막"
    ) {
        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of(color)}$title",
                "${ChatColor.BOLD}$actTitle",
                40,
                80,
                40
            )

            if (Info.game!!.act == 1) {
                player.teleport(if (player.isTeam("RedTeam")) redTeamStartPoint else blueTeamStartPoint)
            } else {
                val targetPoint = if (player.isTeam("RedTeam")) redTeamStartPoint else blueTeamStartPoint
                moveToStartPoint(player, targetPoint)
            }
        }
    }

    private fun handleEnd() {
        Info.game!!.players.forEach { player ->
            val abnormalStatusManager = AbnormalStatusManager()
            val unableAttackManager = abnormalStatusManager.createUnableAttackManager()
            unableAttackManager.run { player.addUnableAttack() }
        }
        object : BukkitRunnable() {
            override fun run() {
                ProjectLibrary.instance.loggerInfo("[ProjectLibrary] game end")
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 모든 무대가 종료되었습니다.")
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 현재 점수 계산중입니다...")
                object : BukkitRunnable() {
                    override fun run() {
                        val title = when {
                            Info.game!!.redTeamScore > Info.game!!.blueTeamScore -> "${ChatColor.BOLD}${ChatColor.DARK_RED}레드팀 승리"
                            Info.game!!.redTeamScore == Info.game!!.blueTeamScore -> "${ChatColor.BOLD}${ChatColor.DARK_BLUE}무승부"
                            else -> "${ChatColor.BOLD}${ChatColor.DARK_BLUE}블루팀 승리"
                        }
                        val subtitle = when {
                            Info.game!!.redTeamScore > Info.game!!.blueTeamScore -> "${ChatColor.BOLD}${Info.game!!.redTeamScore - Info.game!!.blueTeamScore} 차이로 승리하였습니다."
                            Info.game!!.redTeamScore == Info.game!!.blueTeamScore -> "${ChatColor.BOLD}양팀 모두 ${Info.game!!.blueTeamScore}점으로 무승부입니다."
                            else -> "${ChatColor.BOLD}${Info.game!!.blueTeamScore - Info.game!!.redTeamScore} 차이로 승리하였습니다."
                        }
                        Info.game!!.players.forEach { player ->
                            player.sendTitle(title, subtitle, 20, 40, 20)
                        }
                        Info.game!!.stop()
                    }
                }.runTaskLater(ProjectLibrary.instance, 100L)
            }
        }.runTaskLater(ProjectLibrary.instance, 40L)
    }

    private fun moveToStartPoint(player: Player, startPoint: Location) {
        player.gameMode = GameMode.SPECTATOR
        object : BukkitRunnable() {
            override fun run() {
                val direction = startPoint.clone().subtract(player.location).toVector().normalize().multiply(3.0)
                player.velocity = direction
                if (player.location.distance(startPoint) <= 2.0) {
                    player.velocity = Vector(0, 0, 0)
                    player.gameMode = GameMode.ADVENTURE
                    this.cancel()
                }
            }
        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
    }
}

class FloorManager(private val converter: FloorHandler) {
    fun advanceFloor() {
        converter.advanceFloor()
    }

    fun handleCurrentFloor() {
        converter.handleCurrentFloor()
    }
}