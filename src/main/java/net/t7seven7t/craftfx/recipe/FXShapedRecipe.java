package net.t7seven7t.craftfx.recipe;

import com.google.common.collect.Maps;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.item.ItemRegistry;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;

/**
 * Wrapper for Bukkit's ShapedRecipe that allows setting ingredients with ItemStack info
 */
public class FXShapedRecipe extends ShapedRecipe {

    private Map<Character, ItemStack> ingredients = Maps.newHashMap();
    private ItemStack[] items;
    private int width, height;

    /**
     * {@inheritDoc}
     */
    public FXShapedRecipe(ItemStack result) {
        super(result);
    }

    public FXShapedRecipe setIngredient(char key, ItemStack ingredient) {
        Validate.isTrue(super.getIngredientMap().containsKey(key),
                "Symbol does not appear in the shape: " + key);
        ingredients.put(key, ingredient);
        return this;
    }

    FXShapedRecipe setIngredient(Ingredient ingredient) {
        return setIngredient(ingredient.key, ingredient.item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShapedRecipe shape(String... shape) {
        width = 0;
        height = 0;
        for (String row : shape) {
            height++;
            int w = row.toCharArray().length;
            if (w > width) {
                width = w;
            }
        }

        return super.shape(shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Character, ItemStack> getIngredientMap() {
        Map<Character, ItemStack> result = Maps.newHashMap();
        for (Map.Entry<Character, ItemStack> entry : ingredients.entrySet()) {
            result.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().clone());
        }
        return result;
    }

    /**
     * Returns whether this recipe is correct for the ingredients of a specified crafting matrix
     */
    public boolean matches(ItemStack[] matrix) {
        String shape = "";
        if (items == null) {
            items = new ItemStack[width * height];
            for (int j = 0; j < height; j++) {
                shape = shape + getShape()[j];
            }

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    items[i+j] = ingredients.get(shape.toCharArray()[i+j]);
                }
            }
        }

        for (int i = 0; i <= 3 - width; i++) {
            for (int j = 0; j <= 3 - height; j++) {
                if (matches(matrix, i, j, true) || matches(matrix, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(ItemStack[] matrix, int iOff, int jOff, boolean reverse) {
        ItemStack item1, item2;
        ItemRegistry registry = CraftFX.getInstance().getItemRegistry();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int i1 = i - iOff;
                int j1 = j - jOff;

                item1 = null;
                if (i1 >= 0 && j1 >= 0 && i1 < width && j1 < height) {
                    if (reverse) {
                        item1 = items[width - i1 - 1 + j1 * width];
                    } else {
                        item1 = items[i1 + j1 * width];
                    }
                }

                int rowLen = matrix.length == 5 ? 2 : 3;
                item2 = matrix[i + j * rowLen];

                if (!registry.isSimilar(item1, item2)) {
                    return false;
                }
            }
        }

        return true;
    }

}
