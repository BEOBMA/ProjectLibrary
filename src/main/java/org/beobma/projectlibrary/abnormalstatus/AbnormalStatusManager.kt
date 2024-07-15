package org.beobma.projectlibrary.abnormalstatus

import org.beobma.projectlibrary.util.Util.isParticipation
import org.bukkit.entity.Player

class AbnormalStatusManager {
    /**
     * 플레이어에게 이동 불가를 부여함.
     */
    fun Player.addUnableMove() {
        if (!player!!.isParticipation()) return

        player!!.scoreboardTags.add("UnableMove")
    }

    /**
     * 플레이어가 이동 불가상태인지 여부
     */
    fun Player.isUnableMove(): Boolean {
        if (!player!!.isParticipation()) return false

        return player!!.scoreboardTags.contains("UnableMove")
    }

    /**
     * 플레이어에게 이동 불가를 제거함.
     */
    fun Player.removeUnableMove() {
        if (!player!!.isParticipation()) return

        player!!.scoreboardTags.remove("UnableMove")
    }

    /**
     * 플레이어에게 공격 불가를 부여함.
     */
    fun Player.addUnableAttack() {
        if (!player!!.isParticipation()) return

        player!!.scoreboardTags.add("UnableAttack")
    }

    /**
     * 플레이어가 공격 불가상태인지 여부
     */
    fun Player.isUnableAttack(): Boolean {
        if (!player!!.isParticipation()) return false

        return player!!.scoreboardTags.contains("UnableAttack")
    }

    /**
     * 플레이어에게 공격 불가를 제거함.
     */
    fun Player.removeUnableAttack() {
        if (!player!!.isParticipation()) return

        player!!.scoreboardTags.remove("UnableAttack")
    }

    /**
     * 플레이어에게 흐트러짐 상태를 부여함.
     */
    fun Player.addDisheveled() {
        if (!player!!.isParticipation()) return

        player!!.scoreboardTags.add("Disheveled")
        player!!.addUnableMove()
        player!!.addUnableAttack()

        object : BukkitRunnable() {
            override fun run() {
                if (!player!!.isDisheveled()) {
                    this.cancle()
                }

                
            }
        }.runTaskLater(ProjectLibrary.instance, 0L, 1L)
    }

    /**
     * 플레이어가 흐트러짐 상태인지 여부
     */
    fun Player.isDisheveled(): Boolean {
        if (!player!!.isParticipation()) return false

        return player!!.scoreboardTags.contains("Disheveled")
    }

    /**
     * 플레이어에게 흐트러짐 상태를 제거함.
     */
    fun Player.removeDisheveled(): Boolean {
        if (!player!!.isParticipation()) return false

        player!!.scoreboardTags.remove("Disheveled")
        player!!.removeUnableMove()
        player!!.removeUnableAttack()
    }
}