package com.codingforcookies.armorequip;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Arnah
 * @since Jul 30, 2015
 */
public class ArmorListener implements Listener {

  //private final List<String> blockedMaterials;

  public ArmorListener(){ // List<String> blockedMaterials) {
    // this.blockedMaterials = blockedMaterials;
  }
  // Event Priority is highest because other plugins might cancel the events before we check.

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public final void inventoryClick(final InventoryClickEvent ev) {
    if (ev.getAction() == InventoryAction.NOTHING) return;// Why does this get called if nothing happens??
    if (ev.getSlotType() != SlotType.ARMOR && ev.getSlotType() != SlotType.QUICKBAR && ev.getSlotType() != SlotType.CONTAINER) return;
    if (ev.getClickedInventory() != null && !ev.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
    if (!ev.getInventory().getType().equals(InventoryType.CRAFTING) && !ev.getInventory().getType().equals(InventoryType.PLAYER)) return;
    if (!(ev.getWhoClicked() instanceof Player)) return;

    boolean shift = false, numberKey = false;

    if (ev.getClick().equals(ClickType.SHIFT_LEFT) || ev.getClick().equals(ClickType.SHIFT_RIGHT)) shift = true;
    if (ev.getClick().equals(ClickType.NUMBER_KEY)) numberKey = true;

    // if (ev.isCancelled()) return;

    ArmorType newArmorType = ArmorType.matchType(shift ? ev.getCurrentItem() : ev.getCursor());
    if (!shift && newArmorType != null && ev.getRawSlot() != newArmorType.getSlot()){
      // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots slot.
      return;
    }
    if (shift && (newArmorType = ArmorType.matchType(ev.getCurrentItem())) != null) {
      boolean equipping = ev.getRawSlot() != newArmorType.getSlot();
      if (
        newArmorType.equals(ArmorType.HELMET)
          &&
          (
            equipping == isAirOrNull(ev.getWhoClicked().getInventory().getHelmet())
          )
          ||
          newArmorType.equals(ArmorType.CHESTPLATE)
            &&
            (
              equipping == isAirOrNull(ev.getWhoClicked().getInventory().getChestplate())
            )
          ||
          newArmorType.equals(ArmorType.LEGGINGS)
            &&
            (
              equipping == isAirOrNull(ev.getWhoClicked().getInventory().getLeggings())
            )
          ||
          newArmorType.equals(ArmorType.BOOTS)
            &&
            (
              equipping == isAirOrNull(ev.getWhoClicked().getInventory().getBoots())
            )
      ) {
        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) ev.getWhoClicked(), EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : ev.getCurrentItem(), equipping ? ev.getCurrentItem() : null);
        Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
        if (armorEquipEvent.isCancelled()) ev.setCancelled(true);
      }
    } else {
      ItemStack newArmorPiece = ev.getCursor();
      ItemStack oldArmorPiece = ev.getCurrentItem();
      if (numberKey){
        if (ev.getClickedInventory().getType().equals(InventoryType.PLAYER)){// Prevents shit in the 2by2 crafting
          // e.getClickedInventory() == The players inventory
          // e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
          // e.getRawSlot() == The slot the item is going to.
          // e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
          ItemStack hotbarItem = ev.getClickedInventory().getItem(ev.getHotbarButton());
          if (!isAirOrNull(hotbarItem)){// Equipping
            newArmorType = ArmorType.matchType(hotbarItem);
            newArmorPiece = hotbarItem;
            oldArmorPiece = ev.getClickedInventory().getItem(ev.getSlot());
          } else {// Unequipping
            newArmorType = ArmorType.matchType(!isAirOrNull(ev.getCurrentItem()) ? ev.getCurrentItem() : ev.getCursor());
          }
        }
      } else {
        if (isAirOrNull(ev.getCursor()) && !isAirOrNull(ev.getCurrentItem())){// unequip with no new item going into the slot.
          newArmorType = ArmorType.matchType(ev.getCurrentItem());
        }
        // e.getCurrentItem() == Unequip
        // e.getCursor() == Equip
        // newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
      }
      if (newArmorType != null && ev.getRawSlot() == newArmorType.getSlot()){
        EquipMethod method = EquipMethod.PICK_DROP;
        if(ev.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberKey) method = EquipMethod.HOTBAR_SWAP;
        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) ev.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
        Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
        if(armorEquipEvent.isCancelled()){
          ev.setCancelled(true);
        }
      }
    }
  }

  @EventHandler(priority =  EventPriority.HIGHEST)
  public void playerInteractEvent(PlayerInteractEvent ev){
    if (ev.useItemInHand().equals(Result.DENY))return;
    //
    if (ev.getAction() == Action.PHYSICAL) return;
    if (ev.getAction() == Action.RIGHT_CLICK_AIR || ev.getAction() == Action.RIGHT_CLICK_BLOCK){
      Player player = ev.getPlayer();
      if (!ev.useInteractedBlock().equals(Result.DENY)){
        if (ev.getClickedBlock() != null && ev.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()){// Having both of these checks is useless, might as well do it though.
          // Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
          Material mat = ev.getClickedBlock().getType();
          for (String s : new String[] {"LOL", "XD"}){
            if(mat.name().equalsIgnoreCase(s)) return;
          }
        }
      }
      ArmorType newArmorType = ArmorType.matchType(ev.getItem());
      if (newArmorType != null){
        if (newArmorType.equals(ArmorType.HELMET) && isAirOrNull(ev.getPlayer().getInventory().getHelmet()) || newArmorType.equals(ArmorType.CHESTPLATE) && isAirOrNull(ev.getPlayer().getInventory().getChestplate()) || newArmorType.equals(ArmorType.LEGGINGS) && isAirOrNull(ev.getPlayer().getInventory().getLeggings()) || newArmorType.equals(ArmorType.BOOTS) && isAirOrNull(ev.getPlayer().getInventory().getBoots())){
          ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(ev.getPlayer(), EquipMethod.HOTBAR, ArmorType.matchType(ev.getItem()), null, ev.getItem());
          Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
          if (armorEquipEvent.isCancelled()){
            ev.setCancelled(true);
            player.updateInventory();
          }
        }
      }
    }
  }

  @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
  public void inventoryDrag(InventoryDragEvent event){
    // getType() seems to always be even.
    // Old Cursor gives the item you are equipping
    // Raw slot is the ArmorType slot
    // Can't replace armor using this method making getCursor() useless.
    ArmorType type = ArmorType.matchType(event.getOldCursor());
    if (event.getRawSlots().isEmpty()) return;// Idk if this will ever happen
    if (type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)){
      ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(), EquipMethod.DRAG, type, null, event.getOldCursor());
      Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
      if (armorEquipEvent.isCancelled()){
        event.setResult(Result.DENY);
        event.setCancelled(true);
      }
    }
    // Debug shit
    /*System.out.println("Slots: " + event.getInventorySlots().toString());
		System.out.println("Raw Slots: " + event.getRawSlots().toString());
		if(event.getCursor() != null){
			System.out.println("Cursor: " + event.getCursor().getType().name());
		}
		if(event.getOldCursor() != null){
			System.out.println("OldCursor: " + event.getOldCursor().getType().name());
		}
		System.out.println("Type: " + event.getType().name());*/
  }

  @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
  public void itemBreakEvent(PlayerItemBreakEvent ev){
    ArmorType type = ArmorType.matchType(ev.getBrokenItem());
    if (type != null){
      Player p = ev.getPlayer();
      ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.BROKE, type, ev.getBrokenItem(), null);
      Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
      if (armorEquipEvent.isCancelled()){
        ItemStack i = ev.getBrokenItem().clone();
        i.setAmount(1);
        i.setDurability((short) (i.getDurability() - 1));
        if (type.equals(ArmorType.HELMET)){
          p.getInventory().setHelmet(i);
        } else if (type.equals(ArmorType.CHESTPLATE)){
          p.getInventory().setChestplate(i);
        } else if (type.equals(ArmorType.LEGGINGS)){
          p.getInventory().setLeggings(i);
        } else if (type.equals(ArmorType.BOOTS)){
          p.getInventory().setBoots(i);
        }
      }
    }
  }

  @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
  public void playerDeathEvent(PlayerDeathEvent ev){
    Player p = ev.getEntity();
    if (ev.getKeepInventory()) return;
    for (ItemStack i : p.getInventory().getArmorContents()){
      if (!isAirOrNull(i)){
        Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, EquipMethod.DEATH, ArmorType.matchType(i), i, null));
        // No way to cancel a death event.
      }
    }
  }

  /**
   * A utility method to support versions that use null or air ItemStacks.
   */
  public static boolean isAirOrNull(ItemStack item){
    return item == null || item.getType().equals(Material.AIR);
  }
}