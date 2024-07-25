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

    val magicMarksmanWeapon = ItemStack(Material.SPYGLASS, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}마탄")
            lore = listOf(
                "${ChatColor.GRAY}우클릭 시 조준한다.",
                "${ChatColor.GRAY}양손들기 시 발사한다.",
                "${ChatColor.GRAY}탄속이 상당히 빠르며, 적중 시 8의 피해를 입힌다.",
                "",
                "${ChatColor.DARK_GRAY}${ChatColor.ITALIC}역시 네 말대로 어떤 사람이라도 맞출 수 있는 마법의 탄환이로구나."
            )
        }
    }

    val gunWeapon = ItemStack(Material.SPYGLASS, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}총")
            lore = listOf(
                "${ChatColor.GRAY}우클릭 시 조준한다.",
                "${ChatColor.GRAY}양손들기 시 발사한다.",
                "${ChatColor.GRAY}탄속이 상당히 빠르며, 적중 시 5의 피해를 입힌다."
            )
        }
    }
}