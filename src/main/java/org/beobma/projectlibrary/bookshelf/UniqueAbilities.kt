@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.bookshelf

import org.beobma.projectlibrary.bookshelf.Rating.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

data class UniqueAbilities (
    val name: String,
    val description: List<String>,
    val rating: Rating
) {
    fun toItem(): ItemStack {
        val displayName = when (this.rating) {
            Supply -> "${ChatColor.GREEN}${ChatColor.BOLD}$name"
            Advanced -> "${ChatColor.BLUE}${ChatColor.BOLD}$name"
            Limit -> "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}$name"
            Art -> "${ChatColor.YELLOW}${ChatColor.BOLD}$name"
        }

        val cardItem = ItemStack(Material.PAPER, 1)
        val meta = cardItem.itemMeta.apply {
            setDisplayName(displayName)
            lore = description
            addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        }
        cardItem.itemMeta = meta

        return cardItem
    }
}