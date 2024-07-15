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
        Info.game!!.players.forEach { player ->
            player.teleport(Location(Bukkit.getWorld("world"), -21.5, -47.0, 23.0, -90F, 0F))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 2.0F)
        }

        Bukkit.broadcastMessage("\n${ChatColor.YELLOW}10초 후, 정면 문을 기준으로 로비를 반으로 나눠 왼쪽에 있는 플레이어는 레드팀, 오른쪽은 블루팀으로 결정됩니다.")
        ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
            Info.game!!.players.forEach { player ->
                if (isInsideRegion(player.location, Location(Bukkit.getWorld("world"), -22.0, -47.0, 23.0), Location(Bukkit.getWorld("world"), -4.0, -40.0, 26.0))) {
                    assignTeam(player, "BlueTeam", ChatColor.DARK_BLUE)
                } else if (isInsideRegion(player.location, Location(Bukkit.getWorld("world"), -22.0, -47.0, 19.0), Location(Bukkit.getWorld("world"), -4.0, -40.0, 22.0))) {
                    assignTeam(player, "RedTeam", ChatColor.DARK_RED)
                }
            }

            Bukkit.broadcastMessage("\n${ChatColor.YELLOW}팀 등록이 완료되었습니다.")
            broadcastTeamPlayers(teams["RedTeam"], "레드")
            broadcastTeamPlayers(teams["BlueTeam"], "블루")

            ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
                Bukkit.broadcastMessage("\n${ChatColor.YELLOW}잠시 후 게임이 시작됩니다.")
                ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
                    firstStart()
                }, 30L)
            }, 30L)
        }, 200L)
    }

    private fun assignTeam(player: Player, teamName: String, color: ChatColor) {
        teams[teamName]?.addEntry(player.name)
        player.setPlayerListName("${color}${player.playerListName}")
    }

    private fun broadcastTeamPlayers(team: Team?, teamName: String) {
        val playerNames = team?.entries?.joinToString(", ") ?: "없음"
        Bukkit.broadcastMessage("\n${ChatColor.WHITE}$teamName 팀: ${ChatColor.RESET}$playerNames")
    }

    private fun isInsideRegion(location: Location, minPoint: Location, maxPoint: Location): Boolean {
        return (location.x in minPoint.x..maxPoint.x && location.y in minPoint.y..maxPoint.y && location.z in minPoint.z..maxPoint.z)
    }

    private fun firstStart() {
        Info.starting = false
        Info.gaming = true
        val assistantLibrarianBookshelf = createAssistantLibrarianBookshelf()
        Info.game!!.players.forEach { player ->
            assistantLibrarianBookshelf.set(player)
        }
        preparationGame()
    }

    private fun createAssistantLibrarianBookshelf() = MainBookShelf("보조사서 책장",
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

    private fun preparationGame() {
        Info.game!!.act++
        Info.game!!.players.forEach { player ->
            AbnormalStatusManager().apply {
                player.scoreboardTags.remove("isDeath")
                player.getMainBookShelf()!!.emotion = 0
                player.addUnableMove()
                player.addUnableAttack()
                if (Info.game!!.floor != Kether || Info.game!!.act <= 1) {
                    player.stopAllSounds()
                }
                player.gameMode = GameMode.ADVENTURE
                player.getMainBookShelf()?.let {
                    player.maxHealth = it.maxHealth
                    player.health = player.maxHealth
                } ?: run {
                    createAssistantLibrarianBookshelf().set(player)
                }
            }
        }
        handleFloorChange()
        preparationGameText()
    }

    private fun handleFloorChange() {
        if (Info.game!!.act >= 4) {
            Info.game!!.act = 1
            if (Info.game!!.floor != Kether) {
                Info.game!!.players.forEach { it.mainBookShelfCompensation() }
                return
            }
            advanceFloor()
        } else {
            handleCurrentFloor()
        }
    }

    private fun advanceFloor() {
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
    }

    private fun handleCurrentFloor() {
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

    private fun preparationGameText() {
        Bukkit.getScheduler().runTaskLater(ProjectLibrary.instance, {
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 20초 뒤 무대를 시작합니다.")
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] /pl info 명령어를 통해 자신의 핵심 책장 능력을 확인할 수 있습니다.")
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] (이 무대가 첫 번째 무대라면 핵심 책장 능력이 존재하지 않습니다.)")
            Bukkit.getScheduler().runTaskLater(ProjectLibrary.instance, {
                stageStart()
            }, 400L)
        }, 40L)
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
            AbnormalStatusManager().apply {
                player.removeUnableMove()
                player.removeUnableAttack()
            }
        }
    }

    private fun musicDisk() {
        val game = Info.game ?: return
        Info.game!!.players.forEach { player ->
            if (game.floor != Kether || game.act <= 1) {
                game.musicBukkitScheduler?.cancel()
                game.musicBukkitScheduler = null
            }
            val soundData = getSoundData(game.floor, game.act)
            player.playSound(player.location, soundData.first, 0.1f, 1.0f)
            game.musicBukkitScheduler = object : BukkitRunnable() {
                override fun run() {
                    game.players.forEach {
                        it.playSound(it.location, soundData.first, 0.1f, 1.0f)
                    }
                }
            }.runTaskTimer(ProjectLibrary.instance, 0L, soundData.second)
        }
    }

    private fun getSoundData(floor: LibraryFloor, act: Int): Pair<String, Long> {
        return when (floor) {
            GeneralWorks -> when (act) {
                1 -> "custom.music.keterbattle01_01" to 768L
                2 -> "custom.music.keterbattle02_01" to 678L
                3 -> "custom.music.keterbattle03_01" to 904L
                else -> "" to 0L
            }
            History -> when (act) {
                1 -> "custom.music.malkuthbattle01_01" to 873L
                2 -> "custom.music.malkuthbattle02_01" to 668L
                3 -> "custom.music.malkuthbattle03_01" to 600L
                else -> "" to 0L
            }
            TechnologicalSciences -> when (act) {
                1 -> "custom.music.yesodbattle01_01" to 784L
                2 -> "custom.music.yesodbattle02_01" to 738L
                3 -> "custom.music.yesodbattle03_01" to 814L
                else -> "" to 0L
            }
            Literature -> when (act) {
                1 -> "custom.music.hodbattle01_01" to 660L
                2 -> "custom.music.hodbattle02_01" to 621L
                3 -> "custom.music.hodbattle03_01" to 768L
                else -> "" to 0L
            }
            LibraryFloor.Art -> when (act) {
                1 -> "custom.music.netzachbattle01_01" to 840L
                2 -> "custom.music.netzachbattle02_01" to 864L
                3 -> "custom.music.netzachbattle03_01" to 751L
                else -> "" to 0L
            }
            NaturalSciences -> when (act) {
                1 -> "custom.music.tipherethbattle01_01" to 1956L
                2 -> "custom.music.tipherethbattle02_01" to 1822L
                3 -> "custom.music.tipherethbattle03_01" to 1866L
                else -> "" to 0L
            }
            Language -> when (act) {
                1 -> "custom.music.geburabattle01_01" to 1833L
                2 -> "custom.music.geburabattle02_01" to 1833L
                3 -> "custom.music.geburabattle03_01" to 1853L
                else -> "" to 0L
            }
            SocialSciences -> when (act) {
                1 -> "custom.music.chesedbattle01_01" to 1778L
                2 -> "custom.music.chesedbattle02_01" to 1796L
                3 -> "custom.music.chesedbattle03_01" to 1920L
                else -> "" to 0L
            }
            Philosophy -> when (act) {
                1 -> "custom.music.binahbattle01_01" to 2010L
                2 -> "custom.music.binahbattle02_01" to 1846L
                3 -> "custom.music.binahbattle03_01" to 1800L
                else -> "" to 0L
            }
            Religion -> when (act) {
                1 -> "custom.music.hokmabattle01_01" to 1801L
                2 -> "custom.music.hokmabattle02_01" to 1872L
                3 -> "custom.music.hokmabattle03_01" to 1883L
                else -> "" to 0L
            }
            Kether -> "custom.music.geadeyeclaw_01" to 876L
        }
    }

    fun actEndCheck() {
        val redTeamList = teams["RedTeam"]?.players?.filter { it.player?.gameMode == GameMode.ADVENTURE }
        val blueTeamList = teams["BlueTeam"]?.players?.filter { it.player?.gameMode == GameMode.ADVENTURE }

        if (redTeamList.isNullOrEmpty() || blueTeamList.isNullOrEmpty()) {
            actEnd(if (redTeamList.isNullOrEmpty()) teams["RedTeam"] else teams["BlueTeam"])
        }
    }

    private fun actEnd(team: Team?) {
        if (team == null) {
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 무승부, 양팀 점수 없음.")
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
        }
        Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 현재 점수:")
        Bukkit.broadcastMessage("${ChatColor.DARK_RED}${ChatColor.BOLD}[!] 레드팀 ${Info.game!!.redTeamScore}점")
        Bukkit.broadcastMessage("${ChatColor.DARK_BLUE}${ChatColor.BOLD}[!] 블루팀 ${Info.game!!.blueTeamScore}점")
        compensationCheck()
    }

    private fun compensationCheck() {
        if (Info.game!!.floor == Kether && Info.game!!.act == 3) {
            preparationGame()
            return
        }
        Bukkit.getScheduler().runTaskLater(ProjectLibrary.instance, {
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 잠시 후, 보상을 획득합니다.")
            Bukkit.broadcastMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 보상 획득 창을 끄면 보상을 포기합니다.")
            Bukkit.getScheduler().runTaskLater(ProjectLibrary.instance, {
                Info.game!!.players.forEach { player ->
                    player.abnormalityCardCompensation()
                }
            }, 60L)
        }, 60L)
    }

    private fun Player.mainBookShelfCompensation() {
        val inventory = Bukkit.createInventory(null, 27, "핵심 책장을 교체하세요.")
        fillInventoryWithNullPane(inventory)

        val mainBookShelfList = when (Info.game!!.floor) {
            GeneralWorks, History -> SetUp.rumorMainBookShelfList
            TechnologicalSciences, Literature -> SetUp.urbanGhostMainBookShelfList
            LibraryFloor.Art -> SetUp.urbanLegendMainBookShelfList
            NaturalSciences -> SetUp.urbanDiseaseMainBookShelfList
            Language -> SetUp.urbanNightmareMainBookShelfList
            SocialSciences, Philosophy -> SetUp.cityStarMainBookShelfList
            Religion, Kether -> SetUp.ImpuritiesMainBookShelfList
        }

        mainBookShelfList.shuffled().take(3).forEachIndexed { index, bookShelf ->
            inventory.setItem(11 + index * 2, bookShelf.toItem())
        }

        this.scoreboardTags.add("rewardChose")
        this.getMainBookShelf()?.loadToInventory(this)
        this.openInventory(inventory)
    }

    private fun Player.abnormalityCardCompensation() {
        val inventory = Bukkit.createInventory(null, 27, "환상체 책장을 선택하세요.")
        val game = Info.game ?: return

        fillInventoryWithNullPane(inventory)
        val cardList = SetUp.abnormalityCardList.filter { it.floor == game.floor && it.act == game.act }.shuffled().toMutableList()

        val finalCardList = mutableListOf<AbnormalityCard>().apply {
            when {
                getMainBookShelf()!!.emotion > 5 -> addAll(cardList.filter { it.emotion == EmotionType.Affirmation })
                getMainBookShelf()!!.emotion > 0 -> {
                    cardList.find { it.emotion == EmotionType.Affirmation }?.let { add(it) }
                    addAll(cardList)
                }
                getMainBookShelf()!!.emotion == 0 -> addAll(cardList)
                getMainBookShelf()!!.emotion < -5 -> {
                    cardList.find { it.emotion == EmotionType.Negative }?.let { add(it) }
                    addAll(cardList)
                }
                else -> addAll(cardList.filter { it.emotion == EmotionType.Negative })
            }
        }

        finalCardList.take(3).forEachIndexed { index, card ->
            inventory.setItem(11 + index * 2, card.toItem())
        }

        this.scoreboardTags.add("rewardChose")
        this.getMainBookShelf()?.loadToInventory(this)
        this.openInventory(inventory)
    }

    private fun fillInventoryWithNullPane(inventory: Inventory) {
        for (i in 0 until inventory.size) {
            inventory.setItem(i, Localization().nullPane)
        }
    }

    private fun handleGeneralWorks() {
        handleFloorChange("총류의 층", "#FFFFFF", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handleHistory() {
        handleFloorChange("역사의 층", "#ddcc55", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handleTechnologicalSciences() {
        handleFloorChange("기술과학의 층", "#be90d5", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handleLiterature() {
        handleFloorChange("문학의 층", "#dd8833", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handleArt() {
        handleFloorChange("예술의 층", "#669944", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handleNaturalSciences() {
        handleFloorChange("자연과학의 층", "#ffff00", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handleLanguage() {
        handleFloorChange("언어의 층", "#e54d4d", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handleSciences() {
        handleFloorChange("사회과학의 층", "#7198ff", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handlePhilosophy() {
        handleFloorChange("철학의 층", "#D4AF37", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handleReligion() {
        handleFloorChange("종교의 층", "#bbbbbb", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2))
    }

    private fun handleKether() {
        handleFloorChange("총류의 층", "#FFFFFF", Location(Info.world, x1, y1, z1), Location(Info.world, x2, y2, z2), "Finale")
    }

    private fun handleFloorChange(title: String, color: String, redTeamStartPoint: Location, blueTeamStartPoint: Location, actTitle: String = "제 ${Info.game!!.act}막") {
        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${net.md_5.bungee.api.ChatColor.of(color)}$title", "${ChatColor.BOLD}$actTitle", 40, 80, 40)

            if (Info.game!!.act == 1) {
                player.teleport(if (player.isTeam("RedTeam")) redTeamStartPoint else blueTeamStartPoint)
            } else {
                val targetPoint = if (player.isTeam("RedTeam")) redTeamStartPoint else blueTeamStartPoint
                moveToStartPoint(player, targetPoint)
            }
        }
    }

    private fun moveToStartPoint(player: Player, startPoint: Location) {
        player.gameMode = GameMode.SPECTATOR
        object : BukkitRunnable() {
            override fun run() {
                val direction = startPoint.clone().subtract(player.location).toVector().normalize().multiply(1.5)
                player.velocity = direction
                if (player.location.distance(startPoint) <= 1.0) {
                    player.velocity = Vector(0, 0, 0)
                    player.gameMode = GameMode.ADVENTURE
                    this.cancel()
                }
            }
        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
    }

    private fun handleEnd() {
        Info.game!!.players.forEach { player ->
            AbnormalStatusManager().apply {
                player.addUnableAttack()
            }
        }
        Bukkit.getScheduler().runTaskLater(ProjectLibrary.instance, {
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 모든 무대가 종료되었습니다.")
            Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 현재 점수 계산중입니다...")
            Bukkit.getScheduler().runTaskLater(ProjectLibrary.instance, {
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
            }, 100L)
        }, 40L)
    }
}
