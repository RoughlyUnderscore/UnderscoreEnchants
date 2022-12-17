package com.roughlyunderscore.enchs.util

import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class ArmorPiece(private val slot: EquipmentSlot, private val itemStack: ItemStack? = null) {
    fun getSlot(): EquipmentSlot {
        return slot
    }

    fun getItem(): ItemStack? {
        return itemStack
    }

  override fun toString() : String {
    return "ArmorPiece(slot=$slot, item=$itemStack)"
  }

  override fun hashCode(): Int {
    val result = slot.hashCode()
    return 31 * result + itemStack.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ArmorPiece) return false

    if (slot != other.slot) return false
    if (itemStack != other.itemStack) return false

    return true
  }

  companion object {
    @JvmStatic
    fun of(slot: EquipmentSlot, item: ItemStack?): ArmorPiece {
      return ArmorPiece(slot, item)
    }
  }
}