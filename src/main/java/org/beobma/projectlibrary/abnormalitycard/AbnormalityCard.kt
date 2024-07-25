@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.abnormalitycard

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.game.Game
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.info.SetUp
import org.beobma.projectlibrary.manager.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

data class AbnormalityCard(
    val name: String,
    val description: List<String>,
    val emotion: EmotionType,
    val floor: LibraryFloor,
    val act: Int,
    val attackDamageUnit: ((
        game: Game,
        unableAttackManager: UnableAttackManager,
        disheveledManager: UnableDisheveledManager,
        unableMoveManager: UnableMoveManager,
        moveSpeedManager: MoveSpeedManager,
        attackSpeedManager: AttackSpeedManager,
        burnManager: BurnManager,
        bleedingManager: BleedingManager,
        player: Player,
        entity: Player,
        playerBookShelfList: MainBookShelf,
        entityBookShelfList: MainBookShelf,
        finalDamage: Double,
        maxHealthManager: MaxHealthManager,
            ) -> Double)? = null,
    val takenDamageUnit: ((
        game: Game,
        unableAttackManager: UnableAttackManager,
        disheveledManager: UnableDisheveledManager,
        unableMoveManager: UnableMoveManager,
        moveSpeedManager: MoveSpeedManager,
        attackSpeedManager: AttackSpeedManager,
        burnManager: BurnManager,
        bleedingManager: BleedingManager,
        player: Player,
        entity: Player,
        playerBookShelfList: MainBookShelf,
        entityBookShelfList: MainBookShelf,
        finalDamage: Double,
        maxHealthManager: MaxHealthManager
    ) -> Double)? = null,
    val attackDisheveledDamageUnit: ((
        game: Game,
        unableAttackManager: UnableAttackManager,
        disheveledManager: UnableDisheveledManager,
        unableMoveManager: UnableMoveManager,
        moveSpeedManager: MoveSpeedManager,
        attackSpeedManager: AttackSpeedManager,
        burnManager: BurnManager,
        bleedingManager: BleedingManager,
        player: Player,
        entity: Player,
        playerBookShelfList: MainBookShelf,
        entityBookShelfList: MainBookShelf,
        finalDamage: Double,
        maxHealthManager: MaxHealthManager
    ) -> Double)? = null,
    val takenDisheveledDamageUnit: ((
        game: Game,
        unableAttackManager: UnableAttackManager,
        disheveledManager: UnableDisheveledManager,
        unableMoveManager: UnableMoveManager,
        moveSpeedManager: MoveSpeedManager,
        attackSpeedManager: AttackSpeedManager,
        burnManager: BurnManager,
        bleedingManager: BleedingManager,
        player: Player,
        entity: Player,
        playerBookShelfList: MainBookShelf,
        entityBookShelfList: MainBookShelf,
        finalDamage: Double,
        maxHealthManager: MaxHealthManager
    ) -> Double)? = null,
    val killUnit: ((
        game: Game,
        unableAttackManager: UnableAttackManager,
        disheveledManager: UnableDisheveledManager,
        unableMoveManager: UnableMoveManager,
        moveSpeedManager: MoveSpeedManager,
        attackSpeedManager: AttackSpeedManager,
        burnManager: BurnManager,
        bleedingManager: BleedingManager,
        player: Player,
        entity: Player,
        playerBookShelfList: MainBookShelf,
        entityBookShelfList: MainBookShelf,
        maxHealthManager: MaxHealthManager,
        attackDamageManager: AttackDamageManager
    ) -> Unit)? = null,
    val deathUnit: ((
        game: Game,
        unableAttackManager: UnableAttackManager,
        disheveledManager: UnableDisheveledManager,
        unableMoveManager: UnableMoveManager,
        moveSpeedManager: MoveSpeedManager,
        attackSpeedManager: AttackSpeedManager,
        burnManager: BurnManager,
        bleedingManager: BleedingManager,
        player: Player,
        entity: Player,
        playerBookShelfList: MainBookShelf,
        entityBookShelfList: MainBookShelf,
        maxHealthManager: MaxHealthManager,
        attackDamageManager: AttackDamageManager
    ) -> Unit)? = null,
    val stageStartUnit: ((
        game: Game,
        unableAttackManager: UnableAttackManager,
        disheveledManager: UnableDisheveledManager,
        unableMoveManager: UnableMoveManager,
        moveSpeedManager: MoveSpeedManager,
        attackSpeedManager: AttackSpeedManager,
        burnManager: BurnManager,
        bleedingManager: BleedingManager,
        player: Player,
        playerBookShelfList: MainBookShelf,
        maxHealthManager: MaxHealthManager,
        attackDamageManager: AttackDamageManager
            ) -> Unit)? = null
)

interface AbnormalityCardConverter {
    fun toItem(abnormalityCard: AbnormalityCard): ItemStack
    fun toAbnormalityCard(itemStack: ItemStack): AbnormalityCard
}

class DefaultAbnormalityCardConverter : AbnormalityCardConverter {
    override fun toItem(abnormalityCard: AbnormalityCard): ItemStack {
        val displayName = when (abnormalityCard.emotion) {
            EmotionType.Negative -> "${ChatColor.DARK_RED}${ChatColor.BOLD}${abnormalityCard.name}"
            EmotionType.Affirmation -> "${ChatColor.DARK_GREEN}${ChatColor.BOLD}${abnormalityCard.name}"
        }

        val cardItem = ItemStack(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
        val meta = cardItem.itemMeta.apply {
            setDisplayName(displayName)
            lore = abnormalityCard.description
            addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        }
        cardItem.itemMeta = meta

        return cardItem
    }

    override fun toAbnormalityCard(itemStack: ItemStack): AbnormalityCard {
        // ItemStack의 displayName과 일치하는 AbnormalityCard를 찾아 반환
        var abnormalityCard = SetUp.abnormalityCardList.find {
            "${ChatColor.DARK_GREEN}${ChatColor.BOLD}${it.name}" == itemStack.itemMeta.displayName
        }

        if (abnormalityCard == null) {
            abnormalityCard = SetUp.abnormalityCardList.find {
                "${ChatColor.DARK_RED}${ChatColor.BOLD}${it.name}" == itemStack.itemMeta.displayName
            }
        }

        return abnormalityCard ?: AbnormalityCard(
            "오류",
            listOf("이 환상체는 찾을 수 없는 환상체입니다. 개발자에게 문의해주세요."),
            EmotionType.Negative,
            LibraryFloor.Kether,
            3
        )
    }
}

// AbnormalityCardConverter를 사용하는 매니저 클래스
class AbnormalityCardManager(private val converter: AbnormalityCardConverter) {
    fun toItem(abnormalityCard: AbnormalityCard): ItemStack {
        return converter.toItem(abnormalityCard)
    }

    fun toAbnormalityCard(itemStack: ItemStack): AbnormalityCard {
        return converter.toAbnormalityCard(itemStack)
    }
}

// 감정 타입을 나타내는 열거형
enum class EmotionType {
    Affirmation, Negative
}