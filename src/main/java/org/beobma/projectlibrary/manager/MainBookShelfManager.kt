@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.manager

import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

interface MainBookShelfHandler {
    fun createAssistantLibrarianBookshelf(): MainBookShelf
}

class DefaultMainBookShelfHandler : MainBookShelfHandler {
    override fun createAssistantLibrarianBookshelf() = MainBookShelf(
        "보조사서 책장",
        Rating.Supply,
        30.0,
        30.0,
        15,
        15,
        0,
        3,
        mutableListOf(),
        ItemStack(Material.WOODEN_SWORD, 1).apply {
            itemMeta = itemMeta.apply {
                setDisplayName("${ChatColor.BOLD}목검")
                lore = arrayListOf(
                    "${ChatColor.GRAY}가장 기본적인 검."
                )
                isUnbreakable = true
            }
        },
        AttackType.Slashing
    )
}

class MainBookShelfManager(private val converter: MainBookShelfHandler) {
    fun createAssistantLibrarianBookshelf(): MainBookShelf {
        return converter.createAssistantLibrarianBookshelf()
    }
}