package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.manager.AbnormalStatusManager
import org.beobma.projectlibrary.util.Util.enemyTeamPlayer
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.getRandomNumberBetween
import org.beobma.projectlibrary.util.Util.getScore
import org.beobma.projectlibrary.util.Util.isTrueWithProbability
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.math.floor

class OnDamageEvent : Listener {

    @EventHandler
    fun onPlayerAttack(event: EntityDamageByEntityEvent) {
        val game = Info.game ?: return
        val abnormalStatusManager = AbnormalStatusManager()
        val unableAttackManager = abnormalStatusManager.createUnableAttackManager()
        val player = event.damager as? Player ?: return
        val entity = event.entity as? Player ?: return

        if (!Info.isGaming() && !Info.isStarting()) return

        if (unableAttackManager.run { player.isUnableAttack() }) {
            event.isCancelled = true
            return
        }

        if (entity.scoreboardTags.contains("봄의 탄생")) {
            event.isCancelled = true
            return
        }

        event.damage = damageHandle(player, entity, event.damage)

        if (event.damage <= 0) {
            event.isCancelled = true
            return
        }

        var disheveledDamage = event.damage / 4
        disheveledDamage = disheveledDamageHandle(player, entity, disheveledDamage)

        if (disheveledDamage <= 0) {
            return
        }
        val mainBookShelf = game.playerMainBookShelf[entity] ?: return
        mainBookShelf.removeDisheveled(disheveledDamage.toInt(), player)

        if (player.scoreboardTags.contains("유혹")) {
            player.enemyTeamPlayer().random().damage(event.damage, player)
            event.isCancelled = true
            return
        }
        if (entity.health - event.damage <= 0) {
            updateEmotionOnDeath(player, entity)
            entity.health = 0.0
        } else {
            updateEmotionOnDamage(player, entity, event.damage)
            game.playerMainBookShelf[entity]!!.health = entity.health - event.damage
        }
    }

    companion object {
        val paleHandCount = mutableMapOf<Player, MutableMap<Player, Int>>()
        val oldHugCount = mutableMapOf<Player, MutableMap<Player, Int>>()
        val happyMemoriesCount = mutableMapOf<Player, Player>()
        val patternRepeatRecognitionCount = mutableMapOf<Player, Int>()
        val requestMarker = mutableMapOf<Player, Player>()
        val surpriseGiftMarker = mutableMapOf<Player, Player>()
        val pebbleMarker = mutableMapOf<Player, Player>()
        val friendMarker = mutableMapOf<Player, Player>()
    }

    fun objectReset() {
        paleHandCount.clear()
        oldHugCount.clear()
        happyMemoriesCount.clear()
        patternRepeatRecognitionCount.clear()
        requestMarker.clear()
        surpriseGiftMarker.clear()
        pebbleMarker.clear()
        friendMarker.clear()
    }

    private fun damageHandle(
        player: Player, entity: Player, damage: Double
    ): Double {
        val game = Info.game ?: return 0.0
        val abnormalStatusManager = AbnormalStatusManager()
        val unableAttackManager = abnormalStatusManager.createUnableAttackManager()
        val disheveledManager = abnormalStatusManager.createDisheveledManager()
        val unableMoveManager = abnormalStatusManager.createUnableMoveManager()
        val attackDamageManager = abnormalStatusManager.createAttackDamageManager()
        val moveSpeedManager = abnormalStatusManager.createMoveSpeedManager()
        val attackSpeedManager = abnormalStatusManager.createAttackSpeedManager()
        val burnManager = abnormalStatusManager.createBurnManager()
        val bleedingManager = abnormalStatusManager.createBleedingManager()
        val maxHealthManager = abnormalStatusManager.createMaxHealthManager()
        val smokeManager = abnormalStatusManager.createSmokeManager()
        val chargingManager = abnormalStatusManager.createChargingManager()
        val playerBookShelf = player.getMainBookShelf() ?: return 0.0
        val entityBookShelf = entity.getMainBookShelf() ?: return 0.0
        val playerBookShelfList = playerBookShelf.abnormalityCards
        val entityBookShelfList = entityBookShelf.abnormalityCards
        val playerBookShelfUniqueAbilitiesList = playerBookShelf.uniqueAbilities
        val entityBookShelfUniqueAbilitiesList = entityBookShelf.uniqueAbilities

        var finalDamage = 0.0 + damage

        if (playerBookShelfList.isNotEmpty()) {
            playerBookShelfList.forEach { abnormalityCard ->
                val newDamage = abnormalityCard.attackDamageUnit?.invoke(game, unableAttackManager, disheveledManager, unableMoveManager, moveSpeedManager, attackSpeedManager, burnManager, bleedingManager, player, entity, playerBookShelf, entityBookShelf, finalDamage, maxHealthManager) ?: 0.0
                finalDamage += newDamage
            }
        }

        if (playerBookShelfUniqueAbilitiesList.isNotEmpty()) {
            playerBookShelfUniqueAbilitiesList.forEach { uniqueAbilities ->
                when (uniqueAbilities.name) {
                    "윤의 촉" -> {
                        if (50.isTrueWithProbability()) {
                            finalDamage += 1
                        }
                    }

                    "전기충격" -> {
                        if (25.isTrueWithProbability()) {
                            moveSpeedManager.run { entity.moveSpeedPercentage(20, 3, "전기충격", false) }
                        }
                    }

                    "침착" -> {
                        if (25.isTrueWithProbability()) {
                            finalDamage += 3
                        }
                    }

                    "불빠따" -> {
                        if (50.isTrueWithProbability()) {
                            burnManager.run {
                                entity.addBurn(1)
                            }
                        }
                    }

                    "츠바이 검술1" -> {
                        if (playerBookShelf.attackType == AttackType.Slashing) {
                            finalDamage += 1
                        }
                    }

                    "2단 차기" -> {
                        finalDamage += 1
                    }

                    "휴식" -> {
                        player.getScore("restCounter").score = 0
                    }

                    "스티그마 공방 무기" -> {
                        burnManager.run {
                            entity.addBurn(1)
                        }
                    }

                    "흑운도" -> {
                        if (playerBookShelf.attackType == AttackType.Slashing) {
                            finalDamage += 1
                        }
                    }

                    "예리한 일격" -> {
                        if (playerBookShelf.attackType == AttackType.Slashing) {
                            bleedingManager.run {
                                entity.addBleeding(1)
                            }
                        }
                    }

                    "즉흥난타" -> {
                        if (playerBookShelf.attackType == AttackType.Striking) {
                            moveSpeedManager.run {
                                entity.moveSpeedPercentage(10, 1, "즉흥난타", false)
                            }
                        }
                    }

                    "꿰뚫기" -> {
                        if (playerBookShelf.attackType == AttackType.Piercing) {
                            finalDamage += 1
                        }
                    }

                    "쐐기" -> {
                        if (playerBookShelf.attackType == AttackType.Piercing) {
                            finalDamage += 1
                        }
                    }

                    "체력 수거" -> {
                        playerBookShelf.heal(2.0, player)
                    }

                    "정신 수거" -> {
                        playerBookShelf.addDisheveled(2)
                    }

                    "사의 눈 / 탈력" -> {
                        finalDamage += 4
                        finalDamage -= 3
                    }

                    "연기" -> {
                        smokeManager.run {
                            player.addSmoke(1)
                            entity.addSmoke(1)
                        }
                    }

                    "이글거리는 검" -> {
                        burnManager.run {
                            entity.addBurn(getRandomNumberBetween(1, 2, playerBookShelf))
                        }
                    }

                    "불안정한 격정" -> {
                        if (entity.fireTicks > 0) {
                            finalDamage += 1
                        }
                    }

                    "양자 도약" -> {
                        if (player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue >= 1.2) {
                            if (10.isTrueWithProbability()) {
                                finalDamage += 13
                            }
                        }
                    }

                    "차원절단" -> {
                        if (10.isTrueWithProbability()) {
                            finalDamage += 10
                        }
                    }

                    "충전" -> {
                        chargingManager.run {
                            player.addCharging(1)
                        }
                    }

                    "과충전" -> {
                        chargingManager.run {
                            if (player.getCharging() >= 10) {
                                finalDamage += 5
                            }
                        }
                    }

                    "과호흡 / 탈진" -> {
                        if (damage >= 10) {
                            playerBookShelf.light(2)
                        }
                    }
                }
            }
        }

        bleedingManager.run {
            val bleeding = player.getBleeding().toDouble()
            if (bleeding > 0) {
                playerBookShelf.paleDamage(bleeding, player)
                player.removeBleeding(floor(bleeding / 2).toInt())
            }
        }

        smokeManager.run {
            val playerSmoke = player.getSmoke()
            val entitySmoke = entity.getSmoke()

            if (playerSmoke >= 9) {
                finalDamage += 3
            }
            if (entityBookShelf.uniqueAbilities.find { it.name == "뭉게뭉게" } == null) {
                finalDamage += finalDamage * (1.05 * entitySmoke)
            }
        }

        if (game.stageEvent.isNotEmpty()) {
            game.stageEvent.forEach { abnormalityCard ->
                when (abnormalityCard) {
                    "음악" -> {
                        finalDamage += getRandomNumberBetween(4, 8, playerBookShelf)
                    }

                    "흑염" -> {
                        finalDamage *= 2
                    }
                }
            }
        }

        if (player.scoreboardTags.contains("깜짝 선물")) {
            for (pair in surpriseGiftMarker) {
                if (pair.value == player) {
                    if (pair.key != entity) {
                        if (40.isTrueWithProbability()) {
                            playerBookShelf.paleDamage(getRandomNumberBetween(2, 7, pair.key.getMainBookShelf()!!).toDouble(), pair.key)
                            bleedingManager.run {
                                player.addBleeding(2)
                            }
                            player.scoreboardTags.remove("깜짝 선물")
                        }
                    }
                }
            }
        }
        if (player.scoreboardTags.contains("유혹")) {
            val playerT = player.enemyTeamPlayer()
            finalDamage += getRandomNumberBetween(2, 4, playerBookShelf)
            playerT.random().damage(damage + finalDamage, player)
            return -Double.MAX_VALUE
        }

        if (entity.scoreboardTags.contains("못")) {
            if (playerBookShelf.attackType == AttackType.Striking) {
                finalDamage += 5
                entity.scoreboardTags.remove("못")
            }
        }
        if (entity.scoreboardTags.contains("일벌 표식")) {
            player.scoreboardTags.remove("일벌 표식")
            finalDamage += 2
        }
        if (entity.scoreboardTags.contains("의뢰 대상")) {
            finalDamage += 3
        }
        if (entity.scoreboardTags.contains("수호자")) {
            finalDamage -= 2
        }

        if (requestMarker.values.contains(entity)) {
            finalDamage += 3
        }


        if (entityBookShelfList.isNotEmpty()) {
            entityBookShelfList.forEach { abnormalityCard ->
                val newDamage = abnormalityCard.takenDamageUnit?.invoke(game, unableAttackManager, disheveledManager, unableMoveManager, moveSpeedManager, attackSpeedManager, burnManager, bleedingManager, player, entity, playerBookShelf, entityBookShelf, finalDamage, maxHealthManager) ?: 0.0
                finalDamage += newDamage
            }
        }

        if (entityBookShelfUniqueAbilitiesList.isNotEmpty()) {
            entityBookShelfUniqueAbilitiesList.forEach { uniqueAbilities ->
                when (uniqueAbilities.name) {
                    "당신의 방패" -> {
                        finalDamage -= 1
                    }

                    "불굴" -> {
                        if (!entity.scoreboardTags.contains("불굴")) {
                            if (entity.health - (damage + finalDamage) <= 0) {
                                finalDamage -= Double.MAX_VALUE
                                entity.scoreboardTags.add("불굴")

                                game.stageEndBukkitScheduler.add {
                                    entity.scoreboardTags.remove("불굴")
                                }
                            }
                        }
                    }

                    "액화 육체" -> {
                        finalDamage -= 1
                    }

                    "받아내기" -> {
                        finalDamage -= getRandomNumberBetween(2, 6, entityBookShelf)
                    }

                    "깃털 방패" -> {
                        burnManager.run {
                            player.addBurn(getRandomNumberBetween(1, 2, entityBookShelf))
                        }
                    }
                }
            }
        }

        if (playerBookShelfList.find { it.name == "일곱 번째 탄환" } is AbnormalityCard) {
            player.getScore("seventhBulletCount").score++
            finalDamage += getRandomNumberBetween(1, 7, playerBookShelf)
            if (player.getScore("seventhBulletCount").score >= 7) {
                val playerT = game.players.random()
                playerT.damage(damage + finalDamage, player)
                player.getScore("seventhBulletCount").score = 0
                return -Double.MAX_VALUE
            }
        }

        if (playerBookShelfList.find { it.name == "열렬한 감동" } is AbnormalityCard) {
            val playerT = game.players
            finalDamage += 7

            playerT.random().damage(damage + finalDamage, player)
            return -Double.MAX_VALUE
        }

        if (entity.getScore("corrosion").score > 0) {
            finalDamage += entity.getScore("corrosion").score
            entity.getScore("corrosion").score--
        }
        return finalDamage
    }

    private fun disheveledDamageHandle(player: Player, entity: Player, damage: Double): Double {
        val game = Info.game ?: return 0.0
        val abnormalStatusManager = AbnormalStatusManager()
        val unableAttackManager = abnormalStatusManager.createUnableAttackManager()
        val disheveledManager = abnormalStatusManager.createDisheveledManager()
        val unableMoveManager = abnormalStatusManager.createUnableMoveManager()
        val moveSpeedManager = abnormalStatusManager.createMoveSpeedManager()
        val attackSpeedManager = abnormalStatusManager.createAttackSpeedManager()
        val burnManager = abnormalStatusManager.createBurnManager()
        val bleedingManager = abnormalStatusManager.createBleedingManager()
        val smokeManager = abnormalStatusManager.createSmokeManager()
        val playerBookShelf = player.getMainBookShelf() ?: return 0.0
        val entityBookShelf = entity.getMainBookShelf() ?: return 0.0
        val playerBookShelfList = playerBookShelf.abnormalityCards
        val entityBookShelfList = entityBookShelf.abnormalityCards
        val playerBookShelfUniqueAbilitiesList = playerBookShelf.uniqueAbilities
        val entityBookShelfUniqueAbilitiesList = entityBookShelf.uniqueAbilities
        val maxHealthManager = abnormalStatusManager.createMaxHealthManager()

        var finalDamage = 0.0 + damage

        if (playerBookShelfList.isNotEmpty()) {
            playerBookShelfList.forEach { abnormalityCard ->
                val newDamage = abnormalityCard.attackDisheveledDamageUnit?.invoke(game, unableAttackManager, disheveledManager, unableMoveManager, moveSpeedManager, attackSpeedManager, burnManager, bleedingManager, player, entity, playerBookShelf, entityBookShelf, finalDamage, maxHealthManager) ?: 0.0
                finalDamage += newDamage
            }
        }
        if (playerBookShelfUniqueAbilitiesList.isNotEmpty()) {
            playerBookShelfUniqueAbilitiesList.forEach { uniqueAbilities ->
                when (uniqueAbilities.name) {
                    "최소한의 공격" -> {
                        if (playerBookShelf.attackType == AttackType.Slashing) {
                            finalDamage += 1
                        }
                    }

                    "새벽불" -> {
                        if (entity.fireTicks > 0) {
                            finalDamage += getRandomNumberBetween(1, 2, playerBookShelf)
                        }
                    }

                    "꿰뚫어 흔들기" -> {
                        if (playerBookShelf.attackType == AttackType.Piercing) {
                            finalDamage += 1
                        }
                    }

                    "연기 후리기" -> {
                        smokeManager.run {
                            val smoke = player.getSmoke()
                            finalDamage += finalDamage * (1.1 * smoke)
                        }
                    }

                    "굴절" -> {
                        if (5.isTrueWithProbability()) {
                            finalDamage += 13
                        }
                    }
                }
            }
        }
        if (game.stageEvent.isNotEmpty()) {
            game.stageEvent.forEach { abnormalityCard ->
                when (abnormalityCard) {
                    "음악" -> {
                        finalDamage += getRandomNumberBetween(4, 8, playerBookShelf)
                    }
                }
            }
        }

        if (entityBookShelfList.isNotEmpty()) {
            entityBookShelfList.forEach { abnormalityCard ->
                val newDamage = abnormalityCard.takenDisheveledDamageUnit?.invoke(game, unableAttackManager, disheveledManager, unableMoveManager, moveSpeedManager, attackSpeedManager, burnManager, bleedingManager, player, entity, playerBookShelf, entityBookShelf, finalDamage, maxHealthManager) ?: 0.0
                finalDamage += newDamage
            }
        }
        if (entityBookShelfUniqueAbilitiesList.isNotEmpty()) {
            entityBookShelfUniqueAbilitiesList.forEach { uniqueAbilities ->
                when (uniqueAbilities.name) {
                    "테스트" -> {
                        TODO()
                    }
                }
            }
        }

        if (entity.getScore("reverberationStack").score > 1) {
            finalDamage += entity.getScore("reverberationStack").score
            entity.getScore("reverberationStack").score /= 2
        }
        return finalDamage
    }

    private fun updateEmotionOnDeath(player: Player, entity: Player) {
        val game = Info.game ?: return
        val playerBookShelf = game.playerMainBookShelf[player] ?: return
        val entityBookShelf = game.playerMainBookShelf[entity] ?: return

        playerBookShelf.emotion += 3
        entityBookShelf.emotion -= 3
    }

    private fun updateEmotionOnDamage(player: Player, entity: Player, damage: Double) {
        val game = Info.game ?: return
        val playerBookShelf = game.playerMainBookShelf[player] ?: return
        val entityBookShelf = game.playerMainBookShelf[entity] ?: return

        if (damage > 3) {
            playerBookShelf.emotion += 1
            entityBookShelf.emotion -= 1
        }
    }
}