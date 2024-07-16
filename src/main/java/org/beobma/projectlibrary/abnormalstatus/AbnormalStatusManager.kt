package org.beobma.projectlibrary.abnormalstatus

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.util.Util.isParticipation
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

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
    fun Player.addDisheveled() {
        if (!this.isParticipation()) return

        this.scoreboardTags.add("Disheveled")
        this.addUnableMove()
        this.addUnableAttack()

        object : BukkitRunnable() {
            override fun run() {
                if (!player!!.isDisheveled()) {
                    this.cancel()
                }
            }
        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
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

        this.scoreboardTags.remove("Disheveled")
        this.removeUnableMove()
        this.removeUnableAttack()
    }
}