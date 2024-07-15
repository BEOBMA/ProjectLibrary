@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.localization

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Localization {
    val nullPane = ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}비어 있음")
        }
    }
}