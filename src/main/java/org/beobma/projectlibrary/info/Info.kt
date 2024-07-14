package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.game.Game
import org.bukkit.Bukkit
import org.bukkit.World

object Info {
    var game: Game? = null
    var starting: Boolean = false
    var gaming: Boolean = false
    val world: World = Bukkit.getWorld("world")!!

    fun isStarting(): Boolean {
        return starting
    }

    fun isGaming(): Boolean {
        return gaming
    }
}