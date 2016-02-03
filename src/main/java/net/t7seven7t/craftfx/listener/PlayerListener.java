package net.t7seven7t.craftfx.listener;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.nms.AttributeModifier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

/**
 *
 */
public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (!CraftFX.instance().getConfig().getBoolean("remove-unregistered-items", false)) return;
        if (!(event.getDestination() instanceof PlayerInventory)
                && !(event.getSource() instanceof PlayerInventory)) return;
        List<AttributeModifier> modifierList = CraftFX.instance().getNmsInterface()
                .getAttributeModifiers(event.getItem());
        if (modifierList.stream().anyMatch(m -> m.getName().equals("craftfx.item"))) {
            event.setCancelled(true);
            final ItemStack item = event.getItem().clone();
            item.setAmount(Integer.MAX_VALUE);
            final Inventory source = event.getSource();
            Bukkit.getScheduler().runTask(CraftFX.plugin(), () -> source.removeItem(item));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!CraftFX.instance().getConfig().getBoolean("remove-unregistered-items", false)) return;
        List<AttributeModifier> modifierList = CraftFX.instance().getNmsInterface()
                .getAttributeModifiers(event.getItem().getItemStack());
        if (modifierList.stream().anyMatch(m -> m.getName().equals("craftfx.item"))) {
            event.setCancelled(true);
            final Item item = event.getItem();
            Bukkit.getScheduler().runTask(CraftFX.plugin(), item::remove);
        }
    }

}
