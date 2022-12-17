package com.roughlyunderscore.enchs.parsers.condition.events.armor;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.roughlyunderscore.enchs.UnderscoreEnchants;
import com.roughlyunderscore.enchs.parsers.condition.generic.player.PlayerConditionParser;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Locale;

@ToString
public class ArmorConditionParser extends PlayerConditionParser {
  private final ArmorEquipEvent event;

  public ArmorConditionParser(final Player arg, final String condition, final UnderscoreEnchants plugin, final ArmorEquipEvent event) {
    super(arg, condition, plugin);
    this.event = event;
  }

  @Override
  public boolean evaluate() {
    if (super.evaluate()) return true;

    final String[] args0 = getCondition().split(" "), args = Arrays.copyOfRange(args0, 1, args0.length);
    final ArmorCondition condition = ArmorCondition.lookup(args0[0].replace("!", ""));
    if (condition == null) return false;

    final boolean negate = getCondition().startsWith("!");

    final Material oldType = event.getOldArmorPiece() == null ? Material.AIR : event.getOldArmorPiece().getType();
    final Material newType = event.getNewArmorPiece() == null ? Material.AIR : event.getNewArmorPiece().getType();
    final ItemStack oldItem = event.getOldArmorPiece();
    final ItemStack newItem = event.getNewArmorPiece();
    final String oldName = oldItem == null ? "" : (oldItem.getItemMeta() == null ? "" : oldItem.getItemMeta().getDisplayName().toLowerCase(Locale.ROOT));
    final String newName = newItem == null ? "" : (newItem.getItemMeta() == null ? "" : newItem.getItemMeta().getDisplayName().toLowerCase(Locale.ROOT));

    final boolean result = switch (condition) {
      case EQUIPPED_HELMET -> newItem != null && newName.contains("helmet");
      case EQUIPPED_CHESTPLATE -> newItem != null && newName.contains("chestplate");
      case EQUIPPED_LEGGINGS -> newItem != null && newName.contains("leggings");
      case EQUIPPED_BOOTS -> newItem != null && newName.contains("boots");
      case EQUIPPED_ANY -> newItem != null && newType != Material.AIR;
      case EQUIPPED_IS -> newName.equals(args[0].toLowerCase(Locale.ROOT));

      case UNEQUIPPED_HELMET -> oldItem != null && oldName.contains("helmet");
      case UNEQUIPPED_CHESTPLATE -> oldItem != null && oldName.contains("chestplate");
      case UNEQUIPPED_LEGGINGS -> oldItem != null && oldName.contains("leggings");
      case UNEQUIPPED_BOOTS -> oldItem != null && oldName.contains("boots");
      case UNEQUIPPED_ANY -> oldItem != null && oldType != Material.AIR;
      case UNEQUIPPED_IS -> oldName.equals(args[0].toLowerCase(Locale.ROOT));
    };

    if (negate) return !result;
    return result;

  }
}
