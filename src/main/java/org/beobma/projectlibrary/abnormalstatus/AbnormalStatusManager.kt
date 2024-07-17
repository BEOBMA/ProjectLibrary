@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.abnormalstatus

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.isParticipation
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class AbnormalStatusManager {
    /**
     * 플레이어에게 이동 불가를 부여함.
     */
    fun Player.addUnableMove() {
        if (!this.isParticipation()) return

        this.scoreboardTags.add("UnableMove")
    }

    /**
     * 플레이어가 이동 불가상태인지 여부
     */
    fun Player.isUnableMove(): Boolean {
        if (!this.isParticipation()) return false

        return this.scoreboardTags.contains("UnableMove")
    }

    /**
     * 플레이어에게 이동 불가를 제거함.
     */
    fun Player.removeUnableMove() {
        if (!this.isParticipation()) return

        this.scoreboardTags.remove("UnableMove")
    }

    /**
     * 플레이어에게 공격 불가를 부여함.
     */
    fun Player.addUnableAttack() {
        if (!this.isParticipation()) return

        this.scoreboardTags.add("UnableAttack")
    }

    /**
     * 플레이어가 공격 불가상태인지 여부
     */
    fun Player.isUnableAttack(): Boolean {
        if (!this.isParticipation()) return false

        return this.scoreboardTags.contains("UnableAttack")
    }

    /**
     * 플레이어에게 공격 불가를 제거함.
     */
    fun Player.removeUnableAttack() {
        if (!this.isParticipation()) return

        this.scoreboardTags.remove("UnableAttack")
    }

    /**
     * 플레이어에게 흐트러짐 상태를 부여함.
     */
    fun Player.addDisheveled(duration: Int) {
        if (!this.isParticipation()) return

        this.scoreboardTags.add("Disheveled")
        this.addUnableMove()
        this.addUnableAttack()
        this.sendTitle(
            "${ChatColor.BOLD}${ChatColor.GOLD}흐트러짐!",
            "",
            10,
            (duration * 20) -20,
            10
        )


        object : BukkitRunnable() {
            override fun run() {
                if (!Info.isGaming()) return
                if (this@addDisheveled.gameMode == GameMode.SPECTATOR) return
                this@addDisheveled.removeDisheveled()
            }
        }.runTaskLater(ProjectLibrary.instance, (duration * 20).toLong())
    }

    /**
     * 플레이어가 흐트러짐 상태인지 여부
     */
    fun Player.isDisheveled(): Boolean {
        if (!this.isParticipation()) return false

        return this.scoreboardTags.contains("Disheveled")
    }

    /**
     * 플레이어에게 흐트러짐 상태를 제거함.
     */
    fun Player.removeDisheveled() {
        if (!this.isParticipation()) return
        this.getMainBookShelf()!!.disheveled = this.getMainBookShelf()!!.maxDisheveled
        this.scoreboardTags.remove("Disheveled")
        this.removeUnableMove()
        this.removeUnableAttack()
    }

    /**
     * @param duration 지속시간.
     * 플레이어에게 화상 부여함.
     */
    fun Player.addBurn(duration: Int) {
        if (!this.isParticipation()) return
        player.fireTick += duration * 20
    }

    /*
     * 플레이어의 화상을 반환함
     */
    fun Player.getBurn(): Int {
        if (!this.isParticipation()) return 0
        return player.fireTick / 20
    }

    /**
     * @param power 부여할 위력
     * 플레이어에게 출혈을 부여함.
     */
    fun Player.addBleeding(power: Int) {
        if (!this.isParticipation()) return
        this.scoreboard.getObjective("bleeding")!!.getScore(entity.name).score += power
    }

    /*
     * 플레이어의 출혈을 반환함.
     */
    fun Player.getBleeding(): Int {
        if (!this.isParticipation()) return 0
        return entity.scoreboard.getObjective("bleeding")!!.getScore(entity.name).score
    }

    /*
     * @param power 제거할 위력
     * 플레이어의 출혈을 제거함.
     */
    fun Player.removeBleeding(power: Int) {
        if (!this.isParticipation()) return
         entity.scoreboard.getObjective("bleeding")!!.getScore(entity.name).score -= power

        if ( entity.scoreboard.getObjective("bleeding")!!.getScore(entity.name).score <= 0) {
        entity.scoreboard.getObjective("bleeding")!!.getScore(entity.name).score = 0
        }
    }
}