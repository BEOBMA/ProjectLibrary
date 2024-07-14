@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.text.TextManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SetUp {
    companion object {
        val rumorMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val urbanGhostMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val urbanLegendMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val urbanDiseaseMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val urbanNightmareMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val cityStarMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val ImpuritiesMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()

        val mainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
    }

    fun setUp() {
        mainBookShelfSetUp()
    }

    private fun mainBookShelfSetUp() {
        val textManager = TextManager()

        val assistantLibrarianBookshelf = MainBookShelf("보조사서 책장",
            Rating.Supply,
            30.0,
            30.0,
            15,
            15,
            mutableListOf(),
            ItemStack(Material.WOODEN_SWORD, 1).apply {
                itemMeta = itemMeta.apply {
                    setDisplayName("${ChatColor.BOLD}목검")
                    lore = arrayListOf(
                        "${ChatColor.GRAY}가장 기본적인 검."
                    )
                    isUnbreakable = true
                }
            }
        )

        rumorMainBookShelfList.run {
            assistantLibrarianBookshelf

        }

        mainBookShelfList.run {
            assistantLibrarianBookshelf

        }
    }
}