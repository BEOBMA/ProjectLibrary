package org.beobma.projectlibrary

import org.beobma.projectlibrary.command.Commando
import org.beobma.projectlibrary.info.*
import org.beobma.projectlibrary.listener.*
import org.bukkit.plugin.java.JavaPlugin

class ProjectLibrary : JavaPlugin() {
    companion object {
        lateinit var instance: ProjectLibrary
    }

    override fun onEnable() {
        instance = this
        pluginRegister()
        setUp()
        loggerInfo("[ProjectLibrary] Enabling ProjectLibrary")
    }

    override fun onDisable() {
        loggerInfo("[ProjectLibrary] Disabling ProjectLibrary")
    }

    private fun setUp() {
        GeneralWorksAbnormalityCardSetUp()
        HistoryAbnormalityCardSetUp()
        TechnologicalSciencesAbnormalityCardSetUp()
        LiteratureAbnormalityCardSetUp()
        ArtAbnormalityCardSetUp()
        NaturalSciencesAbnormalityCardSetUp()

        RumorMainBookShelfSetUp()
        UrbanGhostMainBookShelfSetUp()
        UrbanLegendMainBookShelfSetUp()
        UrbanDiseaseMainBookShelfSetUp()
        UrbanNightmareMainBookShelfSetUp()
    }

    private fun pluginRegister() {
        server.getPluginCommand("lor")?.setExecutor(Commando())
        server.pluginManager.registerEvents(Commando(), this)
        server.pluginManager.registerEvents(OnDamageEvent(), this)
        server.pluginManager.registerEvents(OnMoveEvent(), this)
        server.pluginManager.registerEvents(OnClickItem(), this)
        server.pluginManager.registerEvents(OnInventoryClose(), this)
        server.pluginManager.registerEvents(OnSwapHands(), this)
        server.pluginManager.registerEvents(OnPlayerDeathEvet(), this)
        server.pluginManager.registerEvents(OnPlayerQuickEvent(), this)
        server.pluginManager.registerEvents(OnStageStart(), this)
        server.pluginManager.registerEvents(OnPlayerJump(), this)
        server.pluginManager.registerEvents(OnPlayerDropItem(), this)
    }

    fun loggerInfo(msg: String) {
        logger.info(msg)
    }
}