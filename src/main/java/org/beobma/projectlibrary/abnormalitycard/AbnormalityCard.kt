@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.abnormalitycard

import org.beobma.projectlibrary.game.LibraryFloor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

data class AbnormalityCard(
    val name: String,
    val description: List<String>,
    val emotion: EmotionType,
    val floor: LibraryFloor,
    val act: Int

) {

    fun toItem(): ItemStack {
        val displayName = when (emotion) {
            EmotionType.Negative -> "${ChatColor.DARK_RED}${ChatColor.BOLD}$name"
            EmotionType.Affirmation -> "${ChatColor.DARK_GREEN}${ChatColor.BOLD}$name"
        }

        val cardItem = ItemStack(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
        val meta = cardItem.itemMeta.apply {
            setDisplayName(displayName)
            lore = description
            addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        }
        cardItem.itemMeta = meta

        return cardItem
    }

}


enum class EmotionType {
    Affirmation, Negative
}