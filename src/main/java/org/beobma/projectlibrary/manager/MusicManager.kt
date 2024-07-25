package org.beobma.projectlibrary.manager

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.game.LibraryFloor.*
import org.beobma.projectlibrary.info.Info
import org.bukkit.scheduler.BukkitRunnable

interface MusicHandler {
    fun musicDisk()
}

class DefaultMusicHandler : MusicHandler {
    override fun musicDisk() {
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
            player.playSound(player.location, soundData.first, 0.05f, 1.0f)
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

            Art -> when (act) {
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
}

class MusicManager(private val converter: MusicHandler) {
    fun musicDisk() {
        converter.musicDisk()
    }
}