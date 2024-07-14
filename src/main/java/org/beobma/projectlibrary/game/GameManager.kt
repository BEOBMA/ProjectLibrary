@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.game

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalstatus.AbnormalStatusManager
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.isTeam
import org.bukkit.*
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team

class GameManager {
    companion object {
        var teams = mutableMapOf(
            "RedTeam" to Bukkit.getScoreboardManager().mainScoreboard.getTeam("RedTeam"),
            "BlueTeam" to Bukkit.getScoreboardManager().mainScoreboard.getTeam("BlueTeam"),
            "SpectatorTeam" to Bukkit.getScoreboardManager().mainScoreboard.getTeam("SpectatorTeam")
        )
    }

    fun teamPick() {
        for (explayer in Info.game!!.players) {
            explayer.teleport(Location(Bukkit.getWorld("world"), 1.0, -60.0, -16.0, 0F, 0F))
            explayer.playSound(explayer.location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 2.0F)
        }

        Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}10초 후, 플레이어가 위치한 바닥의 색에 따라 팀이 등록됩니다.")
        Bukkit.getServer().broadcastMessage("${ChatColor.YELLOW}흰색의 경우 관전자입니다.")
        ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
            for (explayer in Info.game!!.players) {
                if (isInsideRegion(
                        explayer.location, Location(
                            Bukkit.getWorld("world"), 2.0, -61.0, -12.0
                        ), Location(
                            Bukkit.getWorld("world"), 7.0, -55.0, -7.0
                        )
                    )
                ) {
                    //레드팀
                    teams["RedTeam"]?.addEntry(explayer.name)
                    val nametag = explayer.playerListName
                    explayer.setPlayerListName("${ChatColor.DARK_RED}$nametag")


                } else if (isInsideRegion(
                        explayer.location, Location(
                            Bukkit.getWorld("world"), -4.0, -61.0, -12.0
                        ), Location(
                            Bukkit.getWorld("world"), 1.0, -55.0, -7.0
                        )
                    )
                ) {
                    //블루팀
                    teams["BlueTeam"]?.addEntry(explayer.name)
                    val nametag = explayer.playerListName
                    explayer.setPlayerListName("${ChatColor.DARK_BLUE}$nametag")

                } else if (isInsideRegion(
                        explayer.location, Location(
                            Bukkit.getWorld("world"), -5.0, -61.0, -19.0
                        ), Location(
                            Bukkit.getWorld("world"), 7.0, -54.0, -8.0
                        )
                    )
                ) {
                    //관전자팀
                    teams["SpectatorTeam"]?.addEntry(explayer.name)
                }
            }

            if (teams["RedTeam"]?.players?.isEmpty() == true) {
                Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}상대 팀이 존재하지 않아 게임을 진행할 수 없습니다.")
                Info.game!!.stop()
                return@Runnable
            } else if (teams["BlueTeam"]?.players?.isEmpty() == true) {
                Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}상대 팀이 존재하지 않아 게임을 진행할 수 없습니다.")
                Info.game!!.stop()
                return@Runnable
            }

            Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}팀 등록이 완료되었습니다.")
            broadcastTeamPlayers(teams["RedTeam"], "레드")
            broadcastTeamPlayers(teams["BlueTeam"], "블루")
            broadcastTeamPlayers(teams["SpectatorTeam"], "관전자")

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
                }
            )
            assistantLibrarianBookshelf.set(player)
        }

        preparationGame()
    }

    private fun preparationGame() {
        Info.game!!.act++
        Info.game!!.players.forEach { player ->
            AbnormalStatusManager().run {
                player.addUnableMove()
                player.addUnableAttack()
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
                        }
                    )
                    assistantLibrarianBookshelf.set(player)
                }
                player.maxHealth = playerMainBookShelf!!.maxHealth
                player.health = player.maxHealth
            }
        }
        if (Info.game!!.act >= 4) {
            Info.game!!.act = 1
            when (Info.game!!.floor) {
                LibraryFloor.GeneralWorks -> handleHistory()
                LibraryFloor.History -> handleTechnologicalSciences()
                LibraryFloor.TechnologicalSciences -> handleLiterature()
                LibraryFloor.Literature -> handleArt()
                LibraryFloor.Art -> handleNaturalSciences()
                LibraryFloor.NaturalSciences -> handleLanguage()
                LibraryFloor.Language -> handleSciences()
                LibraryFloor.Sciences -> handlePhilosophy()
                LibraryFloor.Philosophy -> handleReligion()
                LibraryFloor.Religion -> handleKether()
                LibraryFloor.Kether -> handleEnd()
            }
        }
        else {
            when (Info.game!!.floor) {
                LibraryFloor.GeneralWorks -> handleGeneralWorks()
                LibraryFloor.History -> handleHistory()
                LibraryFloor.TechnologicalSciences -> handleTechnologicalSciences()
                LibraryFloor.Literature -> handleLiterature()
                LibraryFloor.Art -> handleArt()
                LibraryFloor.NaturalSciences -> handleNaturalSciences()
                LibraryFloor.Language -> handleLanguage()
                LibraryFloor.Sciences -> handleSciences()
                LibraryFloor.Philosophy -> handlePhilosophy()
                LibraryFloor.Religion -> handleReligion()
                LibraryFloor.Kether -> handleKether()
            }
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
        Info.game!!.players.forEach { player ->
            AbnormalStatusManager().run {
                player.removeUnableMove()
                player.removeUnableAttack()
            }
        }
    }

    private fun handleGeneralWorks() {
        val redTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val blueTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.valueOf("#FFFFFF")}총류의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.valueOf("#ddcc55")}역사의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.valueOf("#be90d5")}기술과학의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.valueOf("#dd8833")}문학의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.valueOf("#669944")}예술의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.valueOf("#ffff00")}자연과학의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.valueOf("#e54d4d")}언어의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.valueOf("#7198ff")}사회과학의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.GOLD}철학의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}${ChatColor.valueOf("#bbbbbb")}종교의 층", "${ChatColor.BOLD}제 ${Info.game!!.act}막", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
        val spectatorTeamStartPoint = Location(Info.world, 0.0, 0.0, 0.0, 0F, 0F)

        Info.game!!.players.forEach { player ->
            player.sendTitle("${ChatColor.BOLD}총류의 층", "${ChatColor.BOLD}Finale", 10, 40, 10)

            //좌표 구현
            if (Info.game!!.act == 1) {
                when {
                    player.isTeam("RedTeam") -> {player.teleport(redTeamStartPoint)}
                    player.isTeam("BlueTeam") -> {player.teleport(blueTeamStartPoint)}
                    player.isTeam("SpectatorTeam") -> {player.teleport(spectatorTeamStartPoint)}
                }
            }
            else {
                when {
                    player.isTeam("RedTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(redTeamStartPoint) < 5) {
                                    player.teleport(redTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = redTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("BlueTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(blueTeamStartPoint) < 5) {
                                    player.teleport(blueTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = blueTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    }
                    player.isTeam("SpectatorTeam") -> {
                        object : BukkitRunnable() {
                            override fun run() {
                                val playerLocation = player.location

                                if (playerLocation.distance(spectatorTeamStartPoint) < 5) {
                                    player.teleport(spectatorTeamStartPoint)
                                    cancel()
                                    return
                                }

                                val direction = spectatorTeamStartPoint.clone().subtract(playerLocation).toVector().normalize()
                                val newLocation = playerLocation.add(direction.multiply(5))
                                player.teleport(newLocation)
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
                        }
                        else if (Info.game!!.redTeamScore == Info.game!!.blueTeamScore) {
                            Info.game!!.players.forEach { player ->
                                player.sendTitle(
                                    "${ChatColor.BOLD}${ChatColor.DARK_BLUE}무승부",
                                    "${ChatColor.BOLD}양팀 모두 ${Info.game!!.blueTeamScore}점으로 무승부입니다.",
                                    20,
                                    40,
                                    20
                                )
                            }
                        }
                        else {
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