@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.manager

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.text.TextManager
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.isParticipation
import org.beobma.projectlibrary.util.Util.percentageOf
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

/**
 * 플레이어의 이동과 관련된 상태이상 인터페이스
 */
interface UnableMoveHandler {
    fun Player.addUnableMove()
    fun Player.isUnableMove(): Boolean
    fun Player.removeUnableMove()
}

/**
 * UnableMove 상태 관리 기본 구현
 */
class DefaultUnableMoveHandler : UnableMoveHandler {
    override fun Player.addUnableMove() {
        if (!this.isParticipation()) return
        this.scoreboardTags.add("UnableMove") // 플레이어에게 UnableMove 태그 추가
    }

    override fun Player.isUnableMove(): Boolean {
        if (!this.isParticipation()) return false
        return this.scoreboardTags.contains("UnableMove") // 플레이어가 UnableMove 상태인지 확인
    }

    override fun Player.removeUnableMove() {
        if (!this.isParticipation()) return
        this.scoreboardTags.remove("UnableMove") // 플레이어에게서 UnableMove 태그 제거
    }
}

/**
 * UnableMove 상태 매니저
 */
class UnableMoveManager(private val converter: UnableMoveHandler) {
    fun Player.addUnableMove() {
        converter.run { this@addUnableMove.addUnableMove() }
    }

    fun Player.isUnableMove(): Boolean {
        return converter.run { this@isUnableMove.isUnableMove() }
    }

    fun Player.removeUnableMove() {
        converter.run { this@removeUnableMove.removeUnableMove() }
    }
}

/**
 * 플레이어의 공격과 관련된 상태이상 인터페이스
 */
interface UnableAttackHandler {
    fun Player.addUnableAttack()
    fun Player.isUnableAttack(): Boolean
    fun Player.removeUnableAttack()
}

/**
 * UnableAttack 상태 관리 기본 구현
 */
class DefaultUnableAttackHandler : UnableAttackHandler {
    override fun Player.addUnableAttack() {
        if (!this.isParticipation()) return
        this.scoreboardTags.add("UnableAttack") // 플레이어에게 UnableAttack 태그 추가
    }

    override fun Player.isUnableAttack(): Boolean {
        if (!this.isParticipation()) return false
        return this.scoreboardTags.contains("UnableAttack") // 플레이어가 UnableAttack 상태인지 확인
    }

    override fun Player.removeUnableAttack() {
        if (!this.isParticipation()) return
        this.scoreboardTags.remove("UnableAttack") // 플레이어에게서 UnableAttack 태그 제거
    }
}

/**
 * UnableAttack 상태 매니저
 */
class UnableAttackManager(private val converter: UnableAttackHandler) {
    fun Player.addUnableAttack() {
        converter.run { this@addUnableAttack.addUnableAttack() }
    }

    fun Player.isUnableAttack(): Boolean {
        return converter.run { this@isUnableAttack.isUnableAttack() }
    }

    fun Player.removeUnableAttack() {
        converter.run { this@removeUnableAttack.removeUnableAttack() }
    }
}

/**
 * 플레이어의 흐트러짐과 관련된 상태이상 인터페이스
 */
interface DisheveledHandler {
    fun Player.addDisheveled()
    fun Player.isDisheveled(): Boolean
    fun Player.removeDisheveled()
}

/**
 * Disheveled 상태 관리 기본 구현
 */
class DefaultDisheveledHandler(private val factory: AbnormalStatusManager) : DisheveledHandler {
    override fun Player.addDisheveled() {
        if (!this.isParticipation()) return
        val mainBookShelf =  this.getMainBookShelf() ?: return
        val unableMoveManager = factory.createUnableMoveManager()
        val unableAttackManager = factory.createUnableAttackManager()

        mainBookShelf.disheveled = 0
        unableMoveManager.run { this@addDisheveled.addUnableMove() }
        unableAttackManager.run { this@addDisheveled.addUnableAttack() }
        this.scoreboardTags.add("Disheveled") // 플레이어에게 Disheveled 태그 추가
        this.sendTitle(
            "${ChatColor.BOLD}${ChatColor.GOLD}흐트러짐!", "", 10, (mainBookShelf.disheveledTime * 20) - 20, 10
        )
        object : BukkitRunnable() {
            override fun run() {
                if (!Info.isGaming()) return
                if (this@addDisheveled.gameMode == GameMode.SPECTATOR) return
                this@addDisheveled.removeDisheveled()
            }
        }.runTaskLater(ProjectLibrary.instance, (mainBookShelf.disheveledTime * 20).toLong())
    }

    override fun Player.isDisheveled(): Boolean {
        if (!this.isParticipation()) return false
        return this.scoreboardTags.contains("Disheveled") // 플레이어가 Disheveled 상태인지 확인
    }

    override fun Player.removeDisheveled() {
        if (!this.isParticipation()) return
        this.getMainBookShelf()!!.disheveled = this.getMainBookShelf()!!.maxDisheveled // 흐트러짐 수치 복원
        this.scoreboardTags.remove("Disheveled") // 플레이어에게서 Disheveled 태그 제거
        val unableMoveManager = factory.createUnableMoveManager()
        val unableAttackManager = factory.createUnableAttackManager()
        unableMoveManager.run { this@removeDisheveled.removeUnableMove() } // UnableMove 상태 제거
        unableAttackManager.run { this@removeDisheveled.removeUnableAttack() } // UnableAttack 상태 제거
    }
}

/**
 * Disheveled 상태 매니저
 */
class UnableDisheveledManager(private val converter: DisheveledHandler) {
    fun Player.addDisheveled() {
        converter.run { this@addDisheveled.addDisheveled() }
    }

    fun Player.isDisheveled(): Boolean {
        return converter.run { this@isDisheveled.isDisheveled() }
    }

    fun Player.removeDisheveled() {
        converter.run { this@removeDisheveled.removeDisheveled() }
    }
}

/**
 * 플레이어의 화상과 관련된 상태이상 인터페이스
 */
interface BurnHandler {
    fun Player.addBurn(duration: Int)
}

/**
 * Burn 상태 관리 기본 구현
 */
class DefaultBurnHandler : BurnHandler {
    override fun Player.addBurn(duration: Int) {
        if (!this.isParticipation()) return
        this.fireTicks += duration * 20 // 플레이어에게 화상 상태 추가
    }
}

/**
 * Burn 상태 매니저
 */
class BurnManager(private val converter: BurnHandler) {
    fun Player.addBurn(duration: Int) {
        converter.run { this@addBurn.addBurn(duration) }
    }
}

/**
 * 플레이어의 출혈과 관련된 상태이상 인터페이스
 */
interface BleedingHandler {
    fun Player.addBleeding(power: Int)
    fun Player.getBleeding(): Int
    fun Player.removeBleeding(power: Int)
}

/**
 * Bleeding 상태 관리 기본 구현
 */
class DefaultBleedingHandler : BleedingHandler {
    override fun Player.addBleeding(power: Int) {
        if (!this.isParticipation()) return
        val entity = Info.game!!.players.find { it.getMainBookShelf()!!.abnormalityCards.contains(
            AbnormalityCard(
                "집착", listOf(
                    "${ChatColor.GRAY}모든 대상이 어떤 경로로든, ${TextManager().darkRedMessagetoGray("출혈")}을 얻을 때 2배로 얻음."
                ), EmotionType.Negative, LibraryFloor.Literature, 2)) }

        if (entity != null) {
            this.scoreboard.getObjective("bleeding")!!.getScore(this.name).score += (power * 2) // 플레이어에게 출혈 상태 추가
        }
        else {
            this.scoreboard.getObjective("bleeding")!!.getScore(this.name).score += power // 플레이어에게 출혈 상태 추가
        }
    }

    override fun Player.getBleeding(): Int {
        if (!this.isParticipation()) return 0
        return this.scoreboard.getObjective("bleeding")!!.getScore(this.name).score // 플레이어의 출혈 상태 확인
    }

    override fun Player.removeBleeding(power: Int) {
        if (!this.isParticipation()) return
        this.scoreboard.getObjective("bleeding")!!.getScore(this.name).score -= power // 플레이어의 출혈 상태 제거
        if (this.scoreboard.getObjective("bleeding")!!.getScore(this.name).score <= 0) {
            this.scoreboard.getObjective("bleeding")!!.getScore(this.name).score = 0
        }
    }
}

/**
 * Bleeding 상태 매니저
 */
class BleedingManager(private val converter: BleedingHandler) {
    fun Player.addBleeding(power: Int) {
        converter.run { this@addBleeding.addBleeding(power) }
    }

    fun Player.getBleeding(): Int {
        return converter.run { this@getBleeding.getBleeding() }
    }

    fun Player.removeBleeding(power: Int) {
        converter.run { this@removeBleeding.removeBleeding(power) }
    }
}

/**
 * 플레이어의 연기와 관련된 상태이상 인터페이스
 */
interface SmokeHandler {
    fun Player.addSmoke(power: Int)
    fun Player.getSmoke(): Int
    fun Player.removeSmoke(power: Int)
}

/**
 * Smoke 상태 관리 기본 구현
 */
class DefaultSmokeHandler : SmokeHandler {
    override fun Player.addSmoke(power: Int) {
        if (!this.isParticipation()) return

        this.scoreboard.getObjective("smoke")!!.getScore(this.name).score += power // 플레이어에게 연기 상태 추가
    }

    override fun Player.getSmoke(): Int {
        if (!this.isParticipation()) return 0
        return this.scoreboard.getObjective("smoke")!!.getScore(this.name).score // 플레이어의 연기 상태 확인
    }

    override fun Player.removeSmoke(power: Int) {
        if (!this.isParticipation()) return
        this.scoreboard.getObjective("smoke")!!.getScore(this.name).score -= power // 플레이어의 연기 상태 제거
        if (this.scoreboard.getObjective("smoke")!!.getScore(this.name).score <= 0) {
            this.scoreboard.getObjective("smoke")!!.getScore(this.name).score = 0
        }
    }
}

/**
 * Smoke 상태 매니저
 */
class SmokeManager(private val converter: SmokeHandler) {
    fun Player.addSmoke(power: Int) {
        converter.run { this@addSmoke.addSmoke(power) }
    }

    fun Player.getSmoke(): Int {
        return converter.run { this@getSmoke.getSmoke() }
    }

    fun Player.removeSmoke(power: Int) {
        converter.run { this@removeSmoke.removeSmoke(power) }
    }
}

/**
 * 플레이어의 충전과 관련된 상태이상 인터페이스
 */
interface ChargingHandler {
    fun Player.addCharging(power: Int)
    fun Player.getCharging(): Int
    fun Player.removeCharging(power: Int)
}

/**
 * Charging 상태 관리 기본 구현
 */
class DefaultChargingHandler : ChargingHandler {
    override fun Player.addCharging(power: Int) {
        if (!this.isParticipation()) return

        this.scoreboard.getObjective("charging")!!.getScore(this.name).score += power // 플레이어에게 충전 상태 추가
    }

    override fun Player.getCharging(): Int {
        if (!this.isParticipation()) return 0
        return this.scoreboard.getObjective("charging")!!.getScore(this.name).score // 플레이어의 충전 상태 확인
    }

    override fun Player.removeCharging(power: Int) {
        if (!this.isParticipation()) return
        this.scoreboard.getObjective("charging")!!.getScore(this.name).score -= power // 플레이어의 충전 상태 제거
        if (this.scoreboard.getObjective("charging")!!.getScore(this.name).score <= 0) {
            this.scoreboard.getObjective("charging")!!.getScore(this.name).score = 0
        }
    }
}

/**
 * Smoke 상태 매니저
 */
class ChargingManager(private val converter: SmokeHandler) {
    fun Player.addCharging(power: Int) {
        converter.run { this@addCharging.addSmoke(power) }
    }

    fun Player.getCharging(): Int {
        return converter.run { this@getCharging.getSmoke() }
    }

    fun Player.removeCharging(power: Int) {
        converter.run { this@removeCharging.removeSmoke(power) }
    }
}

/**
 * 플레이어의 공격 속도와 관련된 상태이상 인터페이스
 */
interface AttackSpeedHandler {
    fun Player.attackSpeedPercentage(percent: Int, duration: Int, name: String, increase: Boolean)
}

/**
 * AttackSpeed 상태 관리 기본 구현
 */
class DefaultAttackSpeedHandler : AttackSpeedHandler {
    override fun Player.attackSpeedPercentage(percent: Int, duration: Int, name: String, increase: Boolean) {
        val original = this.getAttribute(Attribute.GENERIC_ATTACK_SPEED)!!.baseValue
        val int = original.percentageOf(percent)
        val final = if (increase) {
            original + int
        } else {
            original - int
        }

        if (Info.game!!.mapBukkitScheduler[this]?.containsKey(name) == true) {
            return
        }

        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(), "attribute ${this.name} minecraft:generic.attack_speed base set $final"
        )

        if (duration != Int.MAX_VALUE) {
            Info.game!!.mapBukkitScheduler[this]?.get(name)?.cancel()
            Info.game!!.mapBukkitScheduler[this]?.put(name, object : BukkitRunnable() {
                override fun run() {
                    Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "attribute ${this@attackSpeedPercentage.name} minecraft:generic.attack_speed base set $original"
                    )
                }
            }.runTaskLater(ProjectLibrary.instance, (duration * 20).toLong()))
        }
    }
}

/**
 * AttackSpeed 상태 매니저
 */
class AttackSpeedManager(private val converter: AttackSpeedHandler) {
    fun Player.attackSpeedPercentage(percent: Int, duration: Int, name: String, increase: Boolean) {
        converter.run { this@attackSpeedPercentage.attackSpeedPercentage(percent, duration, name, increase) }
    }
}

/**
 * 플레이어의 이동 속도와 관련된 상태이상 인터페이스
 */
interface MoveSpeedHandler {
    fun Player.moveSpeedPercentage(percent: Int, duration: Int, name: String, increase: Boolean)
}

/**
 * MoveSpeed 상태 관리 기본 구현
 */
class DefaultMoveSpeedHandler : MoveSpeedHandler {
    override fun Player.moveSpeedPercentage(percent: Int, duration: Int, name: String, increase: Boolean) {
        val original = this.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue
        val int = original.percentageOf(percent)
        val final = if (increase) {
            original + int // 이동 속도 증가
        } else {
            original - int // 이동 속도 감소
        }

        if (Info.game!!.mapBukkitScheduler[this]?.containsKey(name) == true) {
            return
        }

        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(), "attribute ${this.name} minecraft:generic.movement_speed base set $final"
        )

        if (duration != Int.MAX_VALUE) {
            Info.game!!.mapBukkitScheduler[this]?.get(name)?.cancel()
            Info.game!!.mapBukkitScheduler[this] = mutableMapOf(
                Pair(name, object : BukkitRunnable() {
                    override fun run() {
                        Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "attribute ${this@moveSpeedPercentage.name} minecraft:generic.movement_speed base set $original"
                        )
                    }
                }.runTaskLater(ProjectLibrary.instance, (duration * 20).toLong()))
            )
        }
    }
}

/**
 * MoveSpeed 상태 매니저
 */
class MoveSpeedManager(private val converter: MoveSpeedHandler) {
    fun Player.moveSpeedPercentage(percent: Int, duration: Int, name: String, increase: Boolean) {
        converter.run { this@moveSpeedPercentage.moveSpeedPercentage(percent, duration, name, increase) }
    }
}

/**
 * 플레이어의 공격 피해량과 관련된 상태이상 인터페이스
 */
interface AttackDamageHandler {
    fun Player.attackDamage(power: Int, duration: Int, name: String, increase: Boolean)
}

/**
 * AttackDamage 상태 관리 기본 구현
 */
class DefaultAttackDamageHandler : AttackDamageHandler {
    override fun Player.attackDamage(power: Int, duration: Int, name: String, increase: Boolean) {
        val original = this.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue
        val final = if (increase) {
            original + power // 공격 피해
        } else {
            original - power // 공격 피해 감소
        }

        if (Info.game!!.mapBukkitScheduler[this]?.containsKey(name) == true) {
            return
        }

        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(), "attribute ${this.name} minecraft:generic.attack_damage base set $final"
        )

        if (duration != Int.MAX_VALUE) {
            Info.game!!.mapBukkitScheduler[this]?.get(name)?.cancel()
            Info.game!!.mapBukkitScheduler[this] = mutableMapOf(
                Pair(name, object : BukkitRunnable() {
                    override fun run() {
                        Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "attribute ${this@attackDamage.name} minecraft:generic.attack_damage base set $original"
                        )
                    }
                }.runTaskLater(ProjectLibrary.instance, (duration * 20).toLong()))
            )
        }
    }
}

/**
 * AttackDamage 상태 매니저
 */
class AttackDamageManager(private val converter: AttackDamageHandler) {
    fun Player.attackDamage(power: Int, duration: Int, name: String, increase: Boolean) {
        converter.run { this@attackDamage.attackDamage(power, duration, name, increase) }
    }
}

/**
 * 플레이어의 최대 체력과 관련된 상태이상 인터페이스
 */
interface MaxHealthHandler {
    fun Player.maxHealthPercentage(percent: Int, increase: Boolean)
}

/**
 * MaxHealth 상태 관리 기본 구현
 */
class DefaultMaxHealthHandler : MaxHealthHandler {
    override fun Player.maxHealthPercentage(percent: Int, increase: Boolean) {
        if (this.getMainBookShelf() !is MainBookShelf) {
            return
        }
        val healthInt = this.getMainBookShelf()!!.maxHealth.percentageOf(percent)
        if (increase) {
            this.maxHealth += healthInt // 최대 체력 증가
            this.health += healthInt // 현재 체력 증가
        } else {
            this.maxHealth -= healthInt // 최대 체력 감소
        }
    }
}

/**
 * MaxHealth 상태 매니저
 */
class MaxHealthManager(private val converter: MaxHealthHandler) {
    fun Player.maxHealthPercentage(percent: Int, increase: Boolean) {
        converter.run { this@maxHealthPercentage.maxHealthPercentage(percent, increase) }
    }
}

/**
 * 상태 매니저들을 생성하는 팩토리 클래스
 */
class AbnormalStatusManager {
    fun createUnableMoveManager(): UnableMoveManager {
        return UnableMoveManager(DefaultUnableMoveHandler())
    }

    fun createUnableAttackManager(): UnableAttackManager {
        return UnableAttackManager(DefaultUnableAttackHandler())
    }

    fun createDisheveledManager(): UnableDisheveledManager {
        return UnableDisheveledManager(DefaultDisheveledHandler(this))
    }

    fun createBurnManager(): BurnManager {
        return BurnManager(DefaultBurnHandler())
    }

    fun createBleedingManager(): BleedingManager {
        return BleedingManager(DefaultBleedingHandler())
    }

    fun createSmokeManager(): SmokeManager {
        return SmokeManager(DefaultSmokeHandler())
    }

    fun createChargingManager(): ChargingManager {
        return ChargingManager(DefaultSmokeHandler())
    }

    fun createAttackSpeedManager(): AttackSpeedManager {
        return AttackSpeedManager(DefaultAttackSpeedHandler())
    }

    fun createAttackDamageManager(): AttackDamageManager {
        return AttackDamageManager(DefaultAttackDamageHandler())
    }

    fun createMoveSpeedManager(): MoveSpeedManager {
        return MoveSpeedManager(DefaultMoveSpeedHandler())
    }

    fun createMaxHealthManager(): MaxHealthManager {
        return MaxHealthManager(DefaultMaxHealthHandler())
    }
}