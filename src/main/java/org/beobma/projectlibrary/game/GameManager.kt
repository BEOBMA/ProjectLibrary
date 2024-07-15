@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.game

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.abnormalstatus.AbnormalStatusManager
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.game.LibraryFloor.*
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.info.SetUp
import org.beobma.projectlibrary.localization.Localization
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.isTeam
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import org.bukkit.util.Vector

class GameManager {
    companion object {
        var teams = mutableMapOf(
            "RedTeam" to Bukkit.getScoreboardManager().mainScoreboard.getTeam("RedTeam"),
            "BlueTeam" to Bukkit.getScoreboardManager().mainScoreboard.getTeam("BlueTeam")
        )
    }

    fun teamPick() {
        for (explayer in Info.game!!.players) {
            explayer.teleport(Location(Bukkit.getWorld("world"), -21.5, -47.0, 23.0, -90F, 0F))
            explayer.playSound(explayer.location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 2.0F)
        }

        Bukkit.getServer()
            .broadcastMessage("\n${ChatColor.YELLOW}10초 후, 정면 문을 기준으로 로비를 반으로 나눠 왼쪽에 있는 플레이어는 레드팀, 오른쪽은 블루팀으로 결정됩니다.")
        ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
            for (explayer in Info.game!!.players) {
                if (isInsideRegion(
                        explayer.location, Location(
                            Bukkit.getWorld("world"), -22.0, -47.0, 23.0
                        ), Location(
                            Bukkit.getWorld("world"), -4.0, -40.0, 26.0
                        )
                    )
                ) {
                    //블루팀
                    teams["BlueTeam"]?.addEntry(explayer.name)
                    val nametag = explayer.playerListName
                    explayer.setPlayerListName("${ChatColor.DARK_BLUE}$nametag")


                } else if (isInsideRegion(
                        explayer.location, Location(
                            Bukkit.getWorld("world"), -22.0, -47.0, 19.0
                        ), Location(
                            Bukkit.getWorld("world"), -4.0, -40.0, 22.0
                        )
                    )
                ) {
                    //레드팀
                    teams["RedTeam"]?.addEntry(explayer.name)
                    val nametag = explayer.playerListName
                    explayer.setPlayerListName("${ChatColor.DARK_RED}$nametag")


                }
            }

            /*
            if (teams["RedTeam"]?.players?.isEmpty() == true) {
                Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}상대 팀이 존재하지 않아 게임을 진행할 수 없습니다.")
                Info.game!!.stop()
                return@Runnable
            } else if (teams["BlueTeam"]?.players?.isEmpty() == true) {
                Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}상대 팀이 존재하지 않아 게임을 진행할 수 없습니다.")
                Info.game!!.stop()
                return@Runnable
            }

             */

            Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}팀 등록이 완료되었습니다.")
            broadcastTeamPlayers(teams["RedTeam"], "레드")
            broadcastTeamPlayers(teams["BlueTeam"], "블루")

            ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
                Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}잠시 후 게임이 시작됩니다.")
                ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
                    firstStart()
                }, 30L)
            }, 30L)
        }, 200L)
    }

    private fun broadcastTeamPlayers(team: Team?, teamName: String) {
        val playerNames = team?.entries?.joinToString(", ") ?: "없음"
        Bukkit.broadcastMessage("\n${ChatColor.WHITE}$teamName 팀: ${ChatColor.RESET}$playerNames")
    }

    private fun isInsideRegion(location: Location, minPoint: Location, maxPoint: Location): Boolean {
        return (location.x >= minPoint.x && location.x <= maxPoint.x && location.y >= minPoint.y && location.y <= maxPoint.y && location.z >= minPoint.z && location.z <= maxPoint.z)
    }

    private fun firstStart() {
        Info.starting = false
        Info.gaming = true
        Info.game!!.players.forEach { player ->
            val assistantLibrarianBookshelf = MainBookShelf("보조사서 책장",
                Rating.Supply,
                30.0,
                30.0,
                15,
                15,
                mutableListOf(),
                ItemStack(Material.WOODEN_SWORD, 1).apply {
                    itemMeta = itemMeta.apply {
                        setDisplayName("${ChatColor.BOLD}목검")
                        lore = arrayListOf(
                            "${ChatColor.GRAY}가장 기본적인 검."
                        )
                        isUnbreakable = true
                    }
                })
            assistantLibrarianBookshelf.set(player)
        }

        preparationGame()
    }

    fun preparationGame() {
        Info.game!!.act++
        if (Info.game!!.floor != Kether || Info.game!!.act <= 1) {
            Info.game!!.musicBukkitScheduler?.cancel()
            Info.game!!.musicBukkitScheduler = null
        }
        Info.game!!.players.forEach { player ->
            AbnormalStatusManager().run {
                player.scoreboardTags.remove("isDeath")
                player.getMainBookShelf()!!.emotion = 0
                player.addUnableMove()
                player.addUnableAttack()
                if (Info.game!!.floor != Kether || Info.game!!.act <= 1) {
                    player.stopAllSounds()
                }
                player.gameMode = GameMode.ADVENTURE

                val playerMainBookShelf = player.getMainBookShelf()
                if (playerMainBookShelf == null) {
                    val assistantLibrarianBookshelf = MainBookShelf("보조사서 책장",
                        Rating.Supply,
                        30.0,
                        30.0,
                        15,
                        15,
                        mutableListOf(),
                        ItemStack(Material.WOODEN_SWORD, 1).apply {
                            itemMeta = itemMeta.apply {
                                setDisplayName("${ChatColor.BOLD}목검")
                                lore = arrayListOf(
                                    "${ChatColor.GRAY}가장 기본적인 검."
                                )
                                isUnbreakable = true
                            }
                        })
                    assistantLibrarianBookshelf.set(player)
                }
                player.maxHealth = playerMainBookShelf!!.maxHealth
                player.health = player.maxHealth
            }
        }
        if (Info.game!!.act >= 4) {
            Info.game!!.act = 1
            if (Info.game!!.floor != Kether) {
                Info.game!!.players.forEach {
                    it.mainBookShelfCompensation()
                }
                return
            }
            when (Info.game!!.floor) {
                GeneralWorks -> handleHistory()
                History -> handleTechnologicalSciences()
                TechnologicalSciences -> handleLiterature()
                Literature -> handleArt()
                LibraryFloor.Art -> handleNaturalSciences()
                NaturalSciences -> handleLanguage()
                Language -> handleSciences()
                SocialSciences -> handlePhilosophy()
                Philosophy -> handleReligion()
                Religion -> handleKether()
                Kether -> handleEnd()
            }
        } else {
            when (Info.game!!.floor) {
                GeneralWorks -> handleGeneralWorks()
                History -> handleHistory()
                TechnologicalSciences -> handleTechnologicalSciences()
                Literature -> handleLiterature()
                LibraryFloor.Art -> handleArt()
                NaturalSciences -> handleNaturalSciences()
                Language -> handleLanguage()
                SocialSciences -> handleSciences()
                Philosophy -> handlePhilosophy()
                Religion -> handleReligion()
                Kether -> handleKether()
            }
        }
        preparationGameText()
    }

    fun mainBookShelfEnd() {
        when (Info.game!!.floor) {
            GeneralWorks -> handleHistory()
            History -> handleTechnologicalSciences()
            TechnologicalSciences -> handleLiterature()
            Literature -> handleArt()
            LibraryFloor.Art -> handleNaturalSciences()
            NaturalSciences -> handleLanguage()
            Language -> handleSciences()
            SocialSciences -> handlePhilosophy()
            Philosophy -> handleReligion()
            Religion -> handleKether()
            Kether -> handleEnd()
        }
        preparationGameText()
    }

    private fun preparationGameText() {
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 20초 뒤 무대를 시작합니다.")
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] /pl info 명령어를 통해 자신의 핵심 책장 능력을 확인할 수 있습니다.")
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] (이 무대가 첫 번째 무대라면 핵심 책장 능력이 존재하지 않습니다.)")
                object : BukkitRunnable() {
                    override fun run() {
                        stageStart()
                    }
                }.runTaskLater(ProjectLibrary.instance, 400L)
            }
        }.runTaskLater(ProjectLibrary.instance, 40L)
    }

    private fun stageStart() {
        musicDisk()
        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}막의 시작",
                "${ChatColor.BOLD}Fight",
                40,
                80,
                40
            )

            AbnormalStatusManager().run {
                player.removeUnableMove()
                player.removeUnableAttack()
            }
        }
    }

    private fun musicDisk() {
        Info.game!!.players.forEach { player ->
            when (Info.game!!.floor) {
                GeneralWorks -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.keterbattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.keterbattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 768L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.keterbattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.keterbattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 678L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.keterbattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.keterbattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 904L)
                    }
                }
                History -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.malkuthbattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.malkuthbattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 873L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.malkuthbattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.malkuthbattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 668L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.malkuthbattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.malkuthbattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 600L)
                    }
                }
                TechnologicalSciences -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.yesodbattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.yesodbattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 784L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.yesodbattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.yesodbattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 738L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.yesodbattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.yesodbattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 814L)
                    }
                }
                Literature -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.hodbattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.hodbattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 660L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.hodbattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.hodbattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 621L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.hodbattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.hodbattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 768L)
                    }
                }
                LibraryFloor.Art -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.netzachbattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.netzachbattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 840L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.netzachbattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.netzachbattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 864L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.netzachbattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.netzachbattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 751L)
                    }
                }
                NaturalSciences -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.tipherethbattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.tipherethbattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1956L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.tipherethbattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.tipherethbattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1822L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.tipherethbattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.tipherethbattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1866L)
                    }
                }
                Language -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.geburabattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.geburabattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1833L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.geburabattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.geburabattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1833L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.geburabattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.geburabattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1853L)
                    }
                }
                SocialSciences -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.chesedbattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.chesedbattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1778L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.chesedbattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.chesedbattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1796L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.chesedbattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.chesedbattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1920L)
                    }
                }
                Philosophy -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.binahbattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.binahbattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 2010L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.binahbattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.binahbattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1846L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.binahbattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.binahbattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1800L)
                    }
                }
                Religion -> when (Info.game!!.act) {
                    1 -> {
                        player.playSound(player.location, "custom.music.hokmabattle01_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.hokmabattle01_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1801L)
                    }

                    2 -> {
                        player.playSound(player.location, "custom.music.hokmabattle02_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.hokmabattle02_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1872L)
                    }

                    3 -> {
                        player.playSound(player.location, "custom.music.hokmabattle03_01", 0.1f, 1.0f)
                        Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                            override fun run() {
                                Info.game!!.players.forEach {
                                    it.playSound(it.location, "custom.music.hokmabattle03_01", 0.1f, 1.0f)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1883L)
                    }
                }
                Kether -> {
                    player.playSound(player.location, "custom.music.geadeyeclaw_01", 0.1f, 1.0f)
                    Info.game!!.musicBukkitScheduler = object : BukkitRunnable() {
                        override fun run() {
                            Info.game!!.players.forEach {
                                it.playSound(it.location, "custom.music.geadeyeclaw_01", 0.1f, 1.0f)
                            }
                        }
                    }.runTaskTimer(ProjectLibrary.instance, 0L, 876L)
                }
            }
        }
    }

    fun actEndCheck() {
        val redTeamList = teams["RedTeam"]?.players?.filter { it.player?.gameMode == GameMode.ADVENTURE }
        val blueTeamList = teams["BlueTeam"]?.players?.filter { it.player?.gameMode == GameMode.ADVENTURE }

        if (!redTeamList.isNullOrEmpty() && !blueTeamList.isNullOrEmpty()) {
            return
        }

        if (redTeamList.isNullOrEmpty()) {
            actEnd(teams["RedTeam"])
        } else if (blueTeamList.isNullOrEmpty()) {
            actEnd(teams["BlueTeam"])
        } else {
            actEnd(null)
        }
    }

    private fun actEnd(team: Team?) {
        if (team == null) {
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 무승부, 양팀 점수 없음.")
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 현재 점수:")
            Bukkit.broadcastMessage("${ChatColor.DARK_RED}${ChatColor.BOLD}[!] 레드팀 ${Info.game!!.redTeamScore}점")
            Bukkit.broadcastMessage("${ChatColor.DARK_BLUE}${ChatColor.BOLD}[!] 블루팀 ${Info.game!!.blueTeamScore}점")
            compensationCheck()
        } else {
            when (team) {
                teams["RedTeam"] -> {
                    Info.game!!.redTeamScore++
                    Bukkit.broadcastMessage("${ChatColor.DARK_RED}${ChatColor.BOLD}[!] 레드팀 승, +1점")
                }

                teams["BlueTeam"] -> {
                    Info.game!!.blueTeamScore++
                    Bukkit.broadcastMessage("${ChatColor.DARK_BLUE}${ChatColor.BOLD}[!] 블루팀 승, +1점")
                }
            }
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 현재 점수:")
            Bukkit.broadcastMessage("${ChatColor.DARK_RED}${ChatColor.BOLD}[!] 레드팀 ${Info.game!!.redTeamScore}점")
            Bukkit.broadcastMessage("${ChatColor.DARK_BLUE}${ChatColor.BOLD}[!] 블루팀 ${Info.game!!.blueTeamScore}점")
            compensationCheck()
        }
    }

    private fun compensationCheck() {
        if (Info.game!!.floor == Kether && Info.game!!.act == 3) {
            preparationGame()
            return
        }
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 잠시 후, 보상을 획득합니다.")
                Bukkit.broadcastMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 보상 획득 창을 끄면 보상을 포기합니다.")
                object : BukkitRunnable() {
                    override fun run() {
                        Info.game!!.players.forEach { player ->
                            player.abnormalityCardCompensation()
                        }
                    }
                }.runTaskLater(ProjectLibrary.instance, 60L)
            }
        }.runTaskLater(ProjectLibrary.instance, 60L)
    }

    private fun Player.mainBookShelfCompensation() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "핵심 책장을 교체하세요.") // 3줄짜리 인벤토리
        fillInventoryWithNullPane(inventory)

        val mainBookShelf = when (Info.game!!.floor) {
            GeneralWorks -> SetUp.rumorMainBookShelfList
            History -> SetUp.rumorMainBookShelfList
            TechnologicalSciences -> SetUp.urbanGhostMainBookShelfList
            Literature -> SetUp.urbanGhostMainBookShelfList
            LibraryFloor.Art -> SetUp.urbanLegendMainBookShelfList
            NaturalSciences -> SetUp.urbanDiseaseMainBookShelfList
            Language -> SetUp.urbanNightmareMainBookShelfList
            SocialSciences -> SetUp.cityStarMainBookShelfList
            Philosophy -> SetUp.cityStarMainBookShelfList
            Religion -> SetUp.ImpuritiesMainBookShelfList
            Kether -> SetUp.ImpuritiesMainBookShelfList
        }.toList()

        mainBookShelf.shuffled()

        inventory.setItem(11, mainBookShelf[0].toItem())
        inventory.setItem(13, mainBookShelf[1].toItem())
        inventory.setItem(15, mainBookShelf[2].toItem())
        player!!.scoreboardTags.add("rewardChose")
        player!!.getMainBookShelf()?.loadToInventory(player!!)
        player!!.openInventory(inventory)

    }

    private fun Player.abnormalityCardCompensation() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "환상체 책장을 선택하세요.") // 3줄짜리 인벤토리
        val game = Info.game ?: return

        fillInventoryWithNullPane(inventory)
        val cardList = SetUp.abnormalityCardList.filter { it.floor == game.floor && it.act == game.act }.toList()

        cardList.shuffled()

        val finalCardList: MutableList<AbnormalityCard> = mutableListOf()
        if (player!!.getMainBookShelf()!!.emotion > 5) {
            cardList.filter { it.emotion == EmotionType.Affirmation }.forEach { finalCardList.add(it) }
        } else if (player!!.getMainBookShelf()!!.emotion > 0) {
            cardList.find { it.emotion == EmotionType.Affirmation }?.let { finalCardList.add(it) }
            cardList.forEach { finalCardList.add(it) }
        } else if (player!!.getMainBookShelf()!!.emotion == 0) {
            cardList.forEach { finalCardList.add(it) }
        } else if (player!!.getMainBookShelf()!!.emotion < -5) {
            cardList.find { it.emotion == EmotionType.Negative }?.let { finalCardList.add(it) }
            cardList.forEach { finalCardList.add(it) }
        } else {
            cardList.filter { it.emotion == EmotionType.Negative }.forEach { finalCardList.add(it) }
        }

        inventory.setItem(11, finalCardList[0].toItem())
        inventory.setItem(13, finalCardList[1].toItem())
        inventory.setItem(15, finalCardList[2].toItem())
        player!!.scoreboardTags.add("rewardChose")
        player!!.getMainBookShelf()?.loadToInventory(player!!)
        player!!.openInventory(inventory)
    }

    private fun fillInventoryWithNullPane(inventory: Inventory) {
        for (i in 0 until inventory.size) {
            inventory.setItem(i, Localization().nullPane)
        }
    }

    private fun handleGeneralWorks() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of("#FFFFFF")}총류의 층",
                "${ChatColor.BOLD}제 ${Info.game!!.act}막",
                40,
                80,
                40
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleHistory() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of("#ddcc55")}역사의 층",
                "${ChatColor.BOLD}제 ${Info.game!!.act}막",
                40,
                80,
                40
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleTechnologicalSciences() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of("#be90d5")}기술과학의 층",
                "${ChatColor.BOLD}제 ${Info.game!!.act}막",
                40,
                80,
                40
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleLiterature() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of("#dd8833")}문학의 층",
                "${ChatColor.BOLD}제 ${Info.game!!.act}막",
                40,
                80,
                40
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleArt() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of("#669944")}예술의 층",
                "${ChatColor.BOLD}제 ${Info.game!!.act}막",
                40,
                80,
                40
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleNaturalSciences() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of("#ffff00")}자연과학의 층",
                "${ChatColor.BOLD}제 ${Info.game!!.act}막",
                40,
                80,
                40
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleLanguage() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of("#e54d4d")}언어의 층",
                "${ChatColor.BOLD}제 ${Info.game!!.act}막",
                40,
                80,
                40
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleSciences() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of("#7198ff")}사회과학의 층",
                "${ChatColor.BOLD}제 ${Info.game!!.act}막",
                40,
                80,
                40
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handlePhilosophy() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${ChatColor.GOLD}철학의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleReligion() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of("#bbbbbb")}종교의 층",
                "${ChatColor.BOLD}제 ${Info.game!!.act}막",
                40,
                80,
                40
            )

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleKether() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}총류의 층", "${ChatColor.BOLD}Finale", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {
                        player.teleport(redTeamStartPoint)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.teleport(blueTeamStartPoint)
                    }
                }
            } else {
                when {
                    player.isTeam("RedTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(redTeamStartPoint)

                                if (distance <= 1.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }

                    player.isTeam("BlueTeam") -> {
                        player.gameMode = GameMode.SPECTATOR
                        object : BukkitRunnable() {
                            override fun run() {
                                val chargerLocation = player.location
                                val playerLocation = player.location
                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize().multiply(1.5)
                                player.velocity = direction
                                val distance = chargerLocation.distance(blueTeamStartPoint)

                                if (distance <= 2.0) {
                                    player.velocity = Vector(0, 0, 0)
                                    player.gameMode = GameMode.ADVENTURE
                                    this.cancel()
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                }
            }
        }
    }

    private fun handleEnd() {
        Info.game!!.players.forEach { player ->
            AbnormalStatusManager().run {
                player.addUnableAttack()
            }
        }
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 모든 무대가 종료되었습니다.")
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 현재 점수 계산중입니다...")
                object : BukkitRunnable() {
                    override fun run() {
                        if (Info.game!!.redTeamScore > Info.game!!.blueTeamScore) {
                            Info.game!!.players.forEach { player ->
                                player.sendTitle(
                                    "${ChatColor.BOLD}${ChatColor.DARK_RED}레드팀 승리",
                                    "${ChatColor.BOLD}${Info.game!!.redTeamScore - Info.game!!.blueTeamScore} 차이로 승리하였습니다.",
                                    20,
                                    40,
                                    20
                                )
                            }
                        } else if (Info.game!!.redTeamScore == Info.game!!.blueTeamScore) {
                            Info.game!!.players.forEach { player ->
                                player.sendTitle(
                                    "${ChatColor.BOLD}${ChatColor.DARK_BLUE}무승부",
                                    "${ChatColor.BOLD}양팀 모두 ${Info.game!!.blueTeamScore}점으로 무승부입니다.",
                                    20,
                                    40,
                                    20
                                )
                            }
                        } else {
                            Info.game!!.players.forEach { player ->
                                player.sendTitle(
                                    "${ChatColor.BOLD}${ChatColor.DARK_BLUE}블루팀 승리",
                                    "${ChatColor.BOLD}${Info.game!!.blueTeamScore - Info.game!!.redTeamScore} 차이로 승리하였습니다.",
                                    20,
                                    40,
                                    20
                                )
                            }
                        }

                        Info.game!!.stop()
                    }
                }.runTaskLater(ProjectLibrary.instance, 100L)
            }
        }.runTaskLater(ProjectLibrary.instance, 40L)
    }
}