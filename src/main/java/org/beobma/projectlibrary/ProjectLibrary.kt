package org.beobma.projectlibrary

import org.beobma.projectlibrary.command.Commando
import org.beobma.projectlibrary.info.SetUp
import org.beobma.projectlibrary.listener.OnDamageEvent
import org.beobma.projectlibrary.listener.OnMoveEvent
import org.bukkit.plugin.java.JavaPlugin

class ProjectLibrary : JavaPlugin() {
    companion object {
        lateinit var instance: ProjectLibrary
    }

    override fun onEnable() {
        instance = this
        pluginRegister()
        SetUp().setUp()
        loggerInfo("[ProjectLibrary] Enabling ProjectLibrary")
    }

    override fun onDisable() {
        loggerInfo("[ProjectLibrary] Disabling ProjectLibrary")
    }

    private fun pluginRegister() {
        server.getPluginCommand("pt")?.setExecutor(Commando())
        server.pluginManager.registerEvents(Commando(), this)
        server.pluginManager.registerEvents(OnDamageEvent(), this)
        server.pluginManager.registerEvents(OnMoveEvent(), this)
    }

    fun loggerInfo(msg: String) {
        logger.info(msg)
    }
}
