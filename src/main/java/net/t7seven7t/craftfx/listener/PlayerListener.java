package net.t7seven7t.craftfx.listener;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemDefinition;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ListIterator;
import java.util.Optional;

/**
 *
 */
public class PlayerListener implements Listener {

    private final CraftFX fx = CraftFX.instance();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!fx.getConfig().getBoolean("remove-unregistered-items")
                && !fx.getConfig().getBoolean("update-items")) return;
        try {
            final ItemStack item = event.getItem().getItemStack();
            if (item == null || item.getTypeId() == 0) return;
            final String id = fx.getNmsInterface().getCraftFXId(item);
            if (id == null || id.isEmpty()) return;
            final Optional<ItemDefinition> opt = fx.getItemRegistry().getDefinition(id);
            if (opt.isPresent()) {
                if (fx.getConfig().getBoolean("update-items")) {
                    // update item
                    final ItemStack i = opt.get().getItem();
                    i.setAmount(item.getAmount());
                    i.setDurability(item.getDurability());
                    event.getItem().setItemStack(i);
                }
            } else if (fx.getConfig().getBoolean("remove-unregistered-items")) {
                // remove item
                event.setCancelled(true);
                final Item i = event.getItem();
                Bukkit.getScheduler().runTask(CraftFX.plugin(), i::remove);
            }
        } catch (UnsupportedOperationException e) {
            // ignore
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        final InventoryView view = event.getView();
        if (view.getTopInventory() == null
                || view.getTopInventory() instanceof PlayerInventory) return;
        updateItems(view.getTopInventory());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateItems(event.getPlayer().getInventory());
    }

    private void updateItems(Inventory inventory) {
        if (!fx.getConfig().getBoolean("remove-unregistered-items")
                && !fx.getConfig().getBoolean("update-items")) return;
        final boolean updateItems = fx.getConfig().getBoolean("update-items");
        final boolean removeItems = fx.getConfig().getBoolean("remove-unregistered-items");
        try {
            ListIterator<ItemStack> it = inventory.iterator();
            while (it.hasNext()) {
                final ItemStack item = it.next();
                if (item == null || item.getTypeId() == 0) continue;
                final String id = fx.getNmsInterface().getCraftFXId(item);
                if (id == null || id.isEmpty()) continue;
                final Optional<ItemDefinition> opt = fx.getItemRegistry().getDefinition(id);
                if (opt.isPresent()) {
                    if (updateItems) {
                        // update item
                        final ItemStack i = opt.get().getItem();
                        i.setAmount(item.getAmount());
                        i.setDurability(item.getDurability());
                        it.set(i);
                    }
                } else if (removeItems) {
                    // remove item
                    it.set(null);
                }
            }
        } catch (UnsupportedOperationException e) {
            // ignore
        }
    }

}
