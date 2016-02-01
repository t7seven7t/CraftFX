package net.t7seven7t.craftfx.recipe;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemDefinition;
import net.t7seven7t.util.EnumUtil;
import net.t7seven7t.util.MaterialDataUtil;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class RecipeLoader {

    public Recipe load(ItemDefinition item, ConfigurationSection config) throws Exception {
        final ItemStack result = item.getItem(); // cloned
        // amount the recipe should produce
        final int amount = config.getInt("amount", 1);
        result.setAmount(amount);

        final RecipeType type = EnumUtil.matchEnumValue(RecipeType.class, config.getString("type"));
        final List<Ingredient> ingredients = getIngredients(config.getStringList("ingredients"));

        Recipe recipe;

        switch (type) {
            case SHAPED:
                recipe = getShapedRecipe(ingredients, result, config);
                break;
            case SHAPELESS:
                recipe = getShapelessRecipe(ingredients, result);
                break;
            case FURNACE:
                recipe = getFurnaceRecipe(ingredients, result);
                break;
            default:
                throw new Exception("Recipe type not specified for " + config.getName());
        }

        return recipe;
    }

    private ShapedRecipe getShapedRecipe(List<Ingredient> ingredients, ItemStack item,
                                         ConfigurationSection config) {
        FXShapedRecipe recipe = new FXShapedRecipe(item);
        List<String> shape = config.getStringList("shape");
        recipe.shape(shape.toArray(new String[0]));
        ingredients.forEach(recipe::setIngredient);
        return recipe;
    }

    private ShapelessRecipe getShapelessRecipe(List<Ingredient> ingredients, ItemStack item) {
        FXShapelessRecipe recipe = new FXShapelessRecipe(item);
        ingredients.forEach(recipe::addIngredient);
        return recipe;
    }

    private FurnaceRecipe getFurnaceRecipe(List<Ingredient> ingredients, ItemStack item) {
        return new FXFurnaceRecipe(item, ingredients.get(0).item);
    }

    private List<Ingredient> getIngredients(List<String> stringList) throws Exception {
        List<Ingredient> ingredients = new ArrayList<>();
        for (String ingredientString : stringList) {
            // Split into material & amount/char
            String[] split = ingredientString.split(",");

            // checks name against material data and then registered item names
            MaterialData data = MaterialDataUtil.getMaterialData(split[0]);
            ItemStack item;
            if (data == null) {
                Optional<ItemDefinition> opt = CraftFX.instance().getItemRegistry()
                        .getDefinition(split[0]);
                if (!opt.isPresent()) {
                    throw new Exception("Material name '" + split[0] + "' is invalid.");
                }
                item = opt.get().getItem();
            } else {
                item = data.toItemStack(1);
            }

            int amount = 0;
            char key = '\u0000';

            try {
                amount = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                key = split[1].charAt(0);
            } catch (IndexOutOfBoundsException e) {
                amount = 1;
            }

            ingredients.add(new Ingredient(item, amount, key));
        }

        return ingredients;
    }

    public enum RecipeType {
        SHAPED, SHAPELESS, FURNACE
    }

}
