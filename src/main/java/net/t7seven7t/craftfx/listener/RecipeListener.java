package net.t7seven7t.craftfx.listener;

import net.t7seven7t.craftfx.item.ItemRegistry;
import net.t7seven7t.craftfx.recipe.FXFurnaceRecipe;
import net.t7seven7t.craftfx.recipe.FXShapedRecipe;
import net.t7seven7t.craftfx.recipe.FXShapelessRecipe;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

/**
 * Listens to events related to crafting items
 */
public class RecipeListener implements Listener {

    private final ItemRegistry registry;

    public RecipeListener(ItemRegistry registry) {
        this.registry = registry;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        // Check custom items aren't used in non-plugin related recipes
        if (registry.getRecipes(event.getRecipe().getResult()).isEmpty()) {
            for (ItemStack item : event.getInventory().getMatrix()) {
                // matrix item is registered checking:
                if (registry.getDefinition(item) != null) {
                    event.getInventory().setResult(null);
                    break;
                }
            }
        }
        // Check to ensure recipe ingredients match precisely the matrix
        else if (!recipeMatches(event.getRecipe().getResult(), event.getInventory().getMatrix())) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        // Block custom items being used as fuel in furnace (TODO: add fuel power to items?)
        if (registry.getDefinition(event.getFuel()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        List<Recipe> recipes = registry.getRecipes(event.getResult());
        // Check custom items not used in non-plugin recipes
        if (recipes.isEmpty()) {
            if (registry.getDefinition(event.getSource()) != null) {
                event.setCancelled(true);
            }
        }
        // Check if valid furnace recipe
        else {
            boolean hasRecipe = recipes.stream().filter(r -> r instanceof FXFurnaceRecipe)
                    .map(r -> (FXFurnaceRecipe) r)
                            // Check input matches with recipe
                    .filter(r -> registry.isSimilar(r.getInput(), event.getSource())).findAny()
                    .isPresent();

            if (!hasRecipe) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        // Block custom items being brewed into potions (TODO: add custom potion recipes?)
        if (registry.getDefinition(event.getContents().getIngredient()) != null) {
            event.setCancelled(true);
        }
    }

    private boolean recipeMatches(ItemStack result, ItemStack[] matrix) {
        List<Recipe> recipes = registry.getRecipes(result);
        for (Recipe recipe : recipes) {
            if (recipe == null) {
                continue; // Why would this ever occur?
            } else if (recipe instanceof FXShapedRecipe) {
                if (((FXShapedRecipe) recipe).matches(matrix)) {
                    return true;
                }
            } else if (recipe instanceof FXShapelessRecipe) {
                if (((FXShapelessRecipe) recipe).matches(matrix)) {
                    return true;
                }
            }
        }
        return false;
    }
}
