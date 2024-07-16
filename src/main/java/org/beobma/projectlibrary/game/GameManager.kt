@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.game

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.abnormalstatus.AbnormalStatusManager
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.event.OnStageStartEvent
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
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] team pick time")
        Info.game!!.players.forEach { player ->
            player.teleport(Location(Bukkit.getWorld("world"), -21.5, -47.0, 23.0, -90F, 0F))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 2.0F)
        }

        Bukkit.broadcastMessage("\n${ChatColor.YELLOW}10초 후, 정면 문을 기준으로 로비를 반으로 나눠 왼쪽에 있는 플레이어는 레드팀, 오른쪽은 블루팀으로 결정됩니다.")
        ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
            Info.game!!.players.forEach { player ->
                if (isInsideRegion(
                        player.location,
                        Location(Bukkit.getWorld("world"), -22.0, -47.0, 23.0),
                        Location(Bukkit.getWorld("world"), -4.0, -40.0, 26.0)
                    )
                ) {
                    assignTeam(player, "BlueTeam", ChatColor.DARK_BLUE)
                } else if (isInsideRegion(
                        player.location,
                        Location(Bukkit.getWorld("world"), -22.0, -47.0, 19.0),
                        Location(Bukkit.getWorld("world"), -4.0, -40.0, 22.0)
                    )
                ) {
                    assignTeam(player, "RedTeam", ChatColor.DARK_RED)
                }
            }

            /*
            * 테스트 용도 주석 처리
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

            Bukkit.broadcastMessage("\n${ChatColor.YELLOW}팀 등록이 완료되었습니다.")
            broadcastTeamPlayers(teams["RedTeam"], "레드")
            broadcastTeamPlayers(teams["BlueTeam"], "블루")
            ProjectLibrary.instance.loggerInfo("[ProjectLibrary] team pick end")

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
            Info.game!!.mapBukkitScheduler[player] = mutableMapOf()
        }
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] game reset end")
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

    fun preparationGame() {
        Info.game!!.act++
        if (Info.game!!.act >= 4) {
            Info.game!!.act = 1
            if (Info.game!!.floor != Kether) {
                Info.game!!.players.forEach { it.mainBookShelfCompensation() }
                return
            }
        } else {
            Info.game!!.players.forEach { player ->
                AbnormalStatusManager().apply {
                    player.scoreboardTags.remove("isDeath")
                    player.getMainBookShelf()!!.emotion = 0
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.attack_speed base set 4.0")
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.movement_speed base set 0.10000000149011612")
                    player.gameMode = GameMode.ADVENTURE
                    player.addUnableMove()
                    player.addUnableAttack()
                    player.getMainBookShelf()?.set(player) ?: run {
                        createAssistantLibrarianBookshelf().set(player)
                    }
                }
            }
            handleCurrentFloor()
        }
        preparationGameText()
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] ${Info.game!!.floor} floor ${Info.game!!.act} act")
    }

    fun advanceFloor() {
        Info.game!!.musicBukkitScheduler?.cancel()
        Info.game!!.musicBukkitScheduler = null
        Info.game!!.players.forEach { player ->
            player.stopAllSounds()
            AbnormalStatusManager().apply {
                player.scoreboardTags.remove("isDeath")
                player.getMainBookShelf()!!.emotion = 0
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.attack_speed base set 4.0")
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.movement_speed base set 0.10000000149011612")
                player.addUnableMove()
                player.addUnableAttack()
                player.gameMode = GameMode.ADVENTURE
                player.getMainBookShelf()?.set(player) ?: run {
                    createAssistantLibrarianBookshelf().set(player)
                }
            }
        }
        when (Info.game!!.floor) {
            GeneralWorks -> handleHistory()
            History -> handleTechnologicalSciences()
            TechnologicalSciences -> handleLiterature()
            Literature -> handleArt()
            LibraryFloor.Art -> handleNaturalSciences()
            NaturalSciences -> handleLanguage()
            Language -> handleSocialSciences()
            SocialSciences -> handlePhilosophy()
            Philosophy -> handleReligion()
            Religion -> handleKether()
            Kether -> handleEnd()
        }
        preparationGameText()
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
            SocialSciences -> handleSocialSciences()
            Philosophy -> handlePhilosophy()
            Religion -> handleReligion()
            Kether -> handleKether()
        }
    }

    private fun preparationGameText() {
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

    private fun stageStart() {
        musicDisk()
        Info.game!!.players.forEach { player ->
            player.sendTitle(
                "${ChatColor.BOLD}막의 시작", "${ChatColor.BOLD}Fight", 20, 40, 20
            )
            AbnormalStatusManager().apply {
                player.removeUnableMove()
                player.removeUnableAttack()
            }
        }

        ProjectLibrary.instance.server.pluginManager.callEvent(OnStageStartEvent())
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] act start")
    }

    private fun musicDisk() {
        val game = Info.game ?: return
        Info.game!!.players.forEach { player ->
            if (game.floor != Kether) {
                game.musicBukkitScheduler?.cancel()
                game.musicBukkitScheduler = null
                player.stopAllSounds()
            } else {
                if (game.act == 1) {
                    game.musicBukkitScheduler?.cancel()
                    game.musicBukkitScheduler = null
                    player.stopAllSounds()
                }
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
        val type = when (floor) {
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
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] ${type.first} music play")
        return type
    }

    fun actEndCheck() {
        val redTeamList = teams["RedTeam"]?.players?.filter { it.player?.gameMode == GameMode.ADVENTURE }
        val blueTeamList = teams["BlueTeam"]?.players?.filter { it.player?.gameMode == GameMode.ADVENTURE }

        if (redTeamList.isNullOrEmpty() || blueTeamList.isNullOrEmpty()) {
            actEnd(if (redTeamList.isNullOrEmpty()) teams["RedTeam"] else teams["BlueTeam"])
            ProjectLibrary.instance.loggerInfo("[ProjectLibray] act end")
        }
    }

    private fun actEnd(team: Team?) {
        Info.game!!.stageBukkitScheduler.forEach {
            it.cancel()
        }
        Info.game!!.stageBukkitScheduler.clear()
        Info.game!!.stageEndBukkitScheduler.forEach {
            it.invoke()
        }
        Info.game!!.players.forEach { player ->
            Info.game!!.mapBukkitScheduler[player]?.forEach { (_, u) ->
                u.cancel()
            }
        }
        Info.game!!.mapBukkitScheduler.clear()
        Info.game!!.stageEndBukkitScheduler.clear()
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
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] ${Info.game!!.redTeamScore} red score")
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] ${Info.game!!.blueTeamScore} blue score")
        compensationCheck()
    }

    private fun compensationCheck() {
        if (Info.game!!.floor == Kether && Info.game!!.act == 3) {
            ProjectLibrary.instance.loggerInfo("[ProjectLibrary] compensation cancle")
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
                            ProjectLibrary.instance.loggerInfo("[ProjectLibrary] compensation ready")
                            player.abnormalityCardCompensation()
                        }
                    }
                }.runTaskLater(ProjectLibrary.instance, 60L)
            }
        }.runTaskLater(ProjectLibrary.instance, 40L)
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
        val cardList = SetUp.abnormalityCardList.filter { it.floor == game.floor && it.act == game.act }.toMutableList()

        cardList.shuffle()

        val finalCardSet = mutableSetOf<AbnormalityCard>().apply {
            if (player!!.getMainBookShelf()!!.emotion > 5) {
                cardList.filter { it.emotion == EmotionType.Affirmation }.forEach { add(it) }
            } else if (player!!.getMainBookShelf()!!.emotion > 0) {
                cardList.find { it.emotion == EmotionType.Affirmation }?.let { add(it) }
                cardList.forEach { add(it) }
            } else if (player!!.getMainBookShelf()!!.emotion == 0) {
                cardList.forEach { add(it) }
            } else if (player!!.getMainBookShelf()!!.emotion < -5) {
                cardList.find { it.emotion == EmotionType.Negative }?.let { add(it) }
                cardList.forEach { add(it) }
            } else {
                cardList.filter { it.emotion == EmotionType.Negative }.forEach { add(it) }
            }
        }

        val finalCardList = finalCardSet.toList()

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
        Info.game!!.floor = GeneralWorks
        handleFloorChange("총류의 층", "#FFFFFF", Location(Info.world, 0.0, 0.0, 0.0), Location(Info.world, 0.0, 0.0, 0.0))
    }

    private fun handleHistory() {
        Info.game!!.floor = History
        handleFloorChange("역사의 층", "#ddcc55", Location(Info.world, 0.0, 0.0, 0.0), Location(Info.world, 0.0, 0.0, 0.0))
    }

    private fun handleTechnologicalSciences() {
        Info.game!!.floor = TechnologicalSciences
        handleFloorChange(
            "기술과학의 층",
            "#be90d5",
            Location(Info.world, 0.0, 0.0, 0.0),
            Location(Info.world, 0.0, 0.0, 0.0)
        )
    }

    private fun handleLiterature() {
        Info.game!!.floor = Literature
        handleFloorChange("문학의 층", "#dd8833", Location(Info.world, 0.0, 0.0, 0.0), Location(Info.world, 0.0, 0.0, 0.0))
    }

    private fun handleArt() {
        Info.game!!.floor = LibraryFloor.Art
        handleFloorChange("예술의 층", "#669944", Location(Info.world, 0.0, 0.0, 0.0), Location(Info.world, 0.0, 0.0, 0.0))
    }

    private fun handleNaturalSciences() {
        Info.game!!.floor = NaturalSciences
        handleFloorChange(
            "자연과학의 층",
            "#ffff00",
            Location(Info.world, 0.0, 0.0, 0.0),
            Location(Info.world, 0.0, 0.0, 0.0)
        )
    }

    private fun handleLanguage() {
        Info.game!!.floor = Language
        handleFloorChange("언어의 층", "#e54d4d", Location(Info.world, 0.0, 0.0, 0.0), Location(Info.world, 0.0, 0.0, 0.0))
    }

    private fun handleSocialSciences() {
        Info.game!!.floor = SocialSciences
        handleFloorChange(
            "사회과학의 층",
            "#7198ff",
            Location(Info.world, 0.0, 0.0, 0.0),
            Location(Info.world, 0.0, 0.0, 0.0)
        )
    }

    private fun handlePhilosophy() {
        Info.game!!.floor = Philosophy
        handleFloorChange("철학의 층", "#D4AF37", Location(Info.world, 0.0, 0.0, 0.0), Location(Info.world, 0.0, 0.0, 0.0))
    }

    private fun handleReligion() {
        Info.game!!.floor = Religion
        handleFloorChange("종교의 층", "#bbbbbb", Location(Info.world, 0.0, 0.0, 0.0), Location(Info.world, 0.0, 0.0, 0.0))
    }

    private fun handleKether() {
        Info.game!!.floor = Kether
        handleFloorChange(
            "총류의 층", "#FFFFFF", Location(Info.world, 0.0, 0.0, 0.0), Location(Info.world, 0.0, 0.0, 0.0), "Finale"
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
}