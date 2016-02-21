package net.t7seven7t.craftfx.listener;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemRegistry;
import net.t7seven7t.craftfx.recipe.FXFurnaceRecipe;
import net.t7seven7t.craftfx.recipe.FXRecipe;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Listens to events related to item crafting
 */
public class RecipeListener implements Listener {

    private final CraftFX fx = CraftFX.instance();
    private final Supplier<ItemRegistry> registry = fx::getItemRegistry;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        // Check custom items aren't used in non-plugin related recipes
        final Iterator<FXRecipe> it = registry.get().getRecipes().iterator();
        final ItemStack[] matrix = event.getInventory().getMatrix();
        FXRecipe recipe = null;
        while (it.hasNext()) {
            recipe = it.next();
            if (recipe instanceof FXFurnaceRecipe) continue;
            if (recipe.matches(matrix)) break;
            else recipe = null;
        }

        // recipe == null if matrix didn't match precisely
        if (recipe == null) {
            // make sure if this is a custom item it matches the recipe
            if (registry.get().getDefinition(event.getRecipe().getResult()).isPresent()) {
                event.getInventory().setResult(null);
                return;
            }
            for (ItemStack item : event.getInventory().getMatrix()) {
                // If any of the items in matrix are registered they are custom
                if (registry.get().getDefinition(item).isPresent()) {
                    event.getInventory().setResult(null);
                    return;
                }
            }
        } else {
            // recipe result may not be what the event says since MC compresses down to material
            event.getInventory().setResult(recipe.getResult());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        // Block custom items being used as fuel in furnace (todo: add fuel power to items?)
        if (registry.get().getDefinition(event.getFuel()).isPresent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        final List<FXRecipe> recipes = registry.get().getRecipes(event.getResult());
        // Check custom items not used in non-plugin recipes
        if (recipes.isEmpty()) {
            if (registry.get().getDefinition(event.getSource()).isPresent()) {
                event.setCancelled(true);
            }
        }
        // Check if valid furnace recipe
        else if (!recipes.stream().filter(r -> r instanceof FXFurnaceRecipe)
                .map(r -> (FXFurnaceRecipe) r)
                        // Check input matches the recipe
                .filter(r -> registry.get().isSimilar(r.getInput(), event.getSource()))
                .findAny().isPresent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        // Block custom items being brewed into potions (todo: add custom potion recipes?)
        if (registry.get().getDefinition(event.getContents().getIngredient()).isPresent()) {
            event.setCancelled(true);
        }
    }
}
