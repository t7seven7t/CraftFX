/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.Trigger;
import net.t7seven7t.craftfx.effect.Effect;
import net.t7seven7t.craftfx.effect.EffectFactory;
import net.t7seven7t.craftfx.effect.EffectType;
import net.t7seven7t.craftfx.effect.Potion;
import net.t7seven7t.craftfx.recipe.FXFurnaceRecipe;
import net.t7seven7t.craftfx.recipe.FXShapedRecipe;
import net.t7seven7t.craftfx.recipe.FXShapelessRecipe;
import net.t7seven7t.craftfx.recipe.RecipeType;
import net.t7seven7t.util.FormatUtil;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * @author t7seven7t
 */
public class ItemLoader {
	
	private CraftFX plugin;
	
	public ItemLoader(CraftFX plugin) {
		
		this.plugin = plugin;
		
	}
	
	public void loadItems() {
		
		// Get config value for whether enchantments higher than max level should be allowed
		boolean ignoreLevelRestriction = plugin.getConfig().getBoolean("enchants-ignore-level-restriction");

		List<ConfigurationSection> parents = getParentConfigurationSections();
		parents.add(plugin.getConfig().getConfigurationSection("items"));
		
		ConfigurationSection section;	
		
		for (ConfigurationSection parent : parents) {
			
			for (String name : parent.getKeys(false)) {
				
				section = parent.getConfigurationSection(name);
				
				try {
					
					String[] materialResults = section.getString("id").split(":");
					
					Material material = Material.matchMaterial(materialResults[0]);				
					
					if (material == null)
						exception("No material specified or material name did not match.");
					
					ItemStack item = new ItemStack(material);
					
					if (materialResults.length > 1)
						item.setDurability(Short.valueOf(materialResults[1]));
					
					/**
					 * 
					 * BEGIN ItemMeta changes
					 * 
					 */
					
					plugin.addItem(new ItemData(name, item, section));
					
					ItemMeta meta = item.getItemMeta();
								
					// Set the custom name of the item
					meta.setDisplayName(name);
					
					// Set alternate display name if used, to allow special characters that cannot be in YAML keys
					if (section.contains("name")) {
						
						meta.setDisplayName(section.getString("name"));
						
					}
					
					// Set lore if specified
					if (section.contains("lore")) {
						
						meta.setLore(Arrays.asList(section.getString("lore").split("\\|")));
						
					}
					
					// Set enchantments if specified
					if (section.contains("enchants")) {
						
						setMetaEnchantments(meta, section.getStringList("enchants"), ignoreLevelRestriction);
						
					}
					
					// Set other meta types if they exist
					// LeatherArmorMeta
					if (section.contains("color") && meta instanceof LeatherArmorMeta) {
						
						setLeatherArmorMeta(meta, section.getString("color"));		
						
					} 
					// BookMeta
					else if (meta instanceof BookMeta) {
						
						setBookMeta(meta, section);
						
					} 
					// SkullMeta
					else if (section.contains("owner") && meta instanceof SkullMeta) {
						
						// Sets skull type to player
						item.setDurability((short) 3);
						
						((SkullMeta) meta).setOwner(section.getString("owner"));
						
					}					
					// PotionMeta
					else if (section.contains("potion-effects") && meta instanceof PotionMeta) {
						
						List<PotionEffect> potionEffectsList = getPotionEffects(section.getStringList(Potion.POTION_EFFECTS_PATH));
						
						for (PotionEffect effect : potionEffectsList)
							((PotionMeta) meta).addCustomEffect(effect, true);
						
					}
					
					// Update item meta data
					item.setItemMeta(meta);
				} catch (Exception e) {
					
					plugin.getLogHandler().log(Level.SEVERE, "Item {0} encountered the problem: {1}", name, e.getMessage());
					
					if (plugin.getConfig().getBoolean("debug"))
						e.printStackTrace();
					
				}
			}
		}		
		
		int recipeCount = 0;
		int triggerCount = 0;
		int effectCount = 0;
		
		for (ItemData data : plugin.getItemDataList()) {
			
			load(data);
			
			recipeCount += data.getRecipes().size();
			triggerCount += data.getTriggerCount();
			effectCount += data.getEffectCount();
				
		}
		
        plugin.getLogHandler().log("{0} items loaded with {1} recipes, {2} triggers and {3} effects.", plugin.getItemDataList().size(), recipeCount, triggerCount, effectCount);
		
	}
	
	private void load(ItemData data) {
						
		ItemStack item = data.getItem();
				
		try {
			
			/**
			 * 
			 * BEGIN Effect Registrations
			 * 
			 */
			
			ConfigurationSection config = data.getConfig();
			
			Map<Trigger, List<Effect>> effectMap = null;
			
			if (config.contains("effect")) {
				
				effectMap = getEffect(config.getConfigurationSection("effect"), item);
										
			} else if (config.contains("effects")) {
				
				effectMap = getEffects(config.getConfigurationSection("effects"), item);
				
			}					
			
			if (effectMap != null) {
				
				registerTriggerMap(effectMap, data);						
				
			}
			
			if (config.contains("cooldown")) {
				
				if (config.isList("cooldown") && config.getList("cooldown").size() >= 2) {
					
					List<Double> cooldownBounds = config.getDoubleList("cooldown");
					
					data.setCooldown((long) (cooldownBounds.get(0) * 20L), (long) (cooldownBounds.get(1) * 20L));
					
				} else {
					
					data.setCooldown((long) (config.getDouble("cooldown") * 20L));
		
				}
				
			}
			
			if (config.getBoolean("show-cooldown-message", true)) {
				
				data.displayCooldownMessage();
				
			}
					
			/**
			 * 
			 * BEGIN Recipe registrations
			 * 
			 */
						
			// Set recipe if specified
			if (config.contains("recipe")) {
				
				registerRecipe(config.getConfigurationSection("recipe"), item, data);
				
			} else if (config.contains("recipes")) {
				
				 registerRecipes(config.getConfigurationSection("recipes"), item, data);
				
			}
				
		} catch (Exception e) {
			
			plugin.getLogHandler().log(Level.SEVERE, "Item {0} encountered the problem: {1}", data.getName(), e.getMessage());
			
			if (plugin.getConfig().getBoolean("debug"))
				e.printStackTrace();
			
		}
						
	}
		
	private void setMetaEnchantments(ItemMeta meta, List<String> enchantmentsStringList, boolean ignoreLevelRestriction) throws Exception {
		
		for (String enchantmentString : enchantmentsStringList) {
			
			// Split each enchantment string into its name and level
			String[] results = enchantmentString.split(":");
			
			// Attempt to match with bukkit enums by stripping spaces/making uppercase
			String enchantmentName = results[0].toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", "");
			
			Enchantment enchantment = Enchantment.getByName(enchantmentName);
			
			if (enchantment == null)
				exception("Enchantment \"{0}\" is invalid.", results[0]);
			
			meta.addEnchant(enchantment, Integer.parseInt(results[1]), ignoreLevelRestriction);
			
		}
		
	}
	
	private void setLeatherArmorMeta(ItemMeta meta, String colorString) throws Exception {
		
		String[] rgb = colorString.split(",");
		
		// Assert that three colors are given
		if (rgb.length < 3)
			exception("Color tag \"{0}\" is invalid.", colorString);
		
		int 	red = Integer.valueOf(rgb[0]), 
				green = Integer.valueOf(rgb[1]), 
				blue = Integer.valueOf(rgb[2]);
		
		((LeatherArmorMeta) meta).setColor(Color.fromRGB(red, green, blue));
		
	}
	
	private void setBookMeta(ItemMeta meta, ConfigurationSection section) {
		
		if (section.contains("pages")) {
			
			((BookMeta) meta).setPages(section.getStringList("pages"));
			
		}
		
		if (section.contains("author")) {
			
			((BookMeta) meta).setAuthor(section.getString("author"));
			
		}
		
		if (section.contains("title")) {
			
			((BookMeta) meta).setTitle(section.getString("title"));
			
		}
		
	}
	
	/**
	 * Adds effects to the trigger map in CraftFX class
	 * @param triggerMap Map of triggers and effects
	 * @param ItemStack that map effects are bound to
	 * @return number of effects in map
	 */
	private void registerTriggerMap(Map<Trigger, List<Effect>> triggerMap, ItemData data) {
		
		for (Entry<Trigger, List<Effect>> entry : triggerMap.entrySet()) {
						
			data.addTriggerEffects(entry.getKey(), entry.getValue());
			
		}
				
	}
	
	private Map<Trigger, List<Effect>> getEffects(ConfigurationSection section, ItemStack item) throws Exception {
		
		Map<Trigger, List<Effect>> triggerMap = Maps.newHashMap();
				
		/**
		 * Keys can be anything
		 */
		
		for (String key : section.getKeys(false)) {
			
			Map<Trigger, List<Effect>> result = getEffect(section.getConfigurationSection(key), item);
			
			for (Entry<Trigger, List<Effect>> entry : result.entrySet()) {
				
				List<Effect> effects = triggerMap.get(entry.getKey());
				
				// Check for existing effects
				if (effects == null) {
					
					// No existing effects
					triggerMap.put(entry.getKey(), entry.getValue());
					continue;
					
				}
				
				// Add new effects to existing effects
				effects.addAll(entry.getValue());

				// Re sync list in trigger map
				triggerMap.put(entry.getKey(), effects);
				
			}
			
		}		
		
		return triggerMap;
		
	}
	
	private Map<Trigger, List<Effect>> getEffect(ConfigurationSection section, ItemStack item) throws Exception {
		
		Map<Trigger, List<Effect>> triggerMap = Maps.newHashMap();
		
		List<Trigger> triggers = getTriggers(section.getStringList("triggers"), false);
		
		EffectType type = EffectType.get(section.getString("type"));
		
		if (type == null)
			exception("{0} is an invalid effect type.", section.getString("type"));
		

		for (Trigger trigger : triggers) {
			
			/**
			 * Deserialization of effect is left up to the relevant effect class
			 */	
			try {
				
				triggerMap.put(trigger, Lists.newArrayList(EffectFactory.newEffect(type, trigger, item, section)));
				
			} catch (InvocationTargetException e) {
				
				e.getTargetException().printStackTrace();
				
			}

		}
		
		return triggerMap;
		
	}
	
	private List<Trigger> getTriggers(List<String> triggersStringList, boolean isPotion) throws Exception {
		
		if (triggersStringList == null)
			exception("No triggers specified.");
		
		List<Trigger> triggers = Lists.newArrayList();
		
		for (String triggerString : triggersStringList) {
										
			Trigger trigger = Trigger.matches(triggerString);
			
			if (trigger == null)
				exception("{0} is an invalid trigger.", triggerString);
										
			if (isPotion && !trigger.isPotion())
				exception("{0} is an invalid trigger for potions.", triggerString);

			triggers.add(trigger);
			
		}
		
		return triggers;
		
	}
	
	public static List<PotionEffect> getPotionEffects(List<String> potionEffectsStringList) throws Exception {
		
		List<PotionEffect> potionEffectsList = Lists.newArrayList();
		
		for (String effectString : potionEffectsStringList) {
			
			String[] results = effectString.split(","); 
			
			if (results.length < 3)
				exception("Missing argument: potion effect, duration or amplifier.");
			
			// Attempt to match with bukkit enums by stripping spaces/making uppercase
			String effectName = results[0].toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", "");
			
			PotionEffectType effectType = PotionEffectType.getByName(effectName);
			
			if (effectType == null) 
				exception("Potion effect \"{0}\" is invalid.", results[0]);

			int 	duration = Integer.valueOf(results[1]) * 20,
					amplifier = Integer.valueOf(results[2]);
			
			potionEffectsList.add(new PotionEffect(effectType, duration, amplifier));		
			
		}
		
		return potionEffectsList;
		
	}
	
	private void registerRecipes(ConfigurationSection section, ItemStack item, ItemData data) throws Exception {
				
		/**
		 * Key names can be anything
		 */
		
		for (String key : section.getKeys(false)) {
			
			registerRecipe(section.getConfigurationSection(key), item, data); 
			
		}
				
	}
	
	private void registerRecipe(ConfigurationSection section, ItemStack item, ItemData data) throws Exception {
				
		// Set amount of item to produce when recipe is used
		int amount = section.getInt("amount", 1);
		item.setAmount(amount);
		
		RecipeType type = RecipeType.matches(section.getString("type"));
				
		List<Ingredient> ingredients = getIngredients(section.getStringList("ingredients"));
		
		Recipe recipe;
		
		// Shaped Recipe
		if (type == RecipeType.SHAPED) {
			
			recipe = getShapedRecipe(ingredients, item, section);
			
		} 
		// Shapeless Recipe
		else if (type == RecipeType.SHAPELESS) {
			
			recipe = getShapelessRecipe(ingredients, item);
			
		} 
		// Furnace Recipe
		else if (type == RecipeType.FURNACE) {
			
			recipe = getFurnaceRecipe(ingredients, item);
			
		} else {
			
			exception("Recipe type is either invalid or not specified");
			return;
			
		}
		
		data.addRecipe(recipe);
		plugin.getServer().addRecipe(recipe);
				
	}

	private List<Ingredient> getIngredients(List<String> ingredientsStringList) throws Exception {
		
		List<Ingredient> ingredientsList = Lists.newArrayList();
		
		for (String ingredientString : ingredientsStringList) {
			
			ItemStack ingredient;
			
			// Split each ingredient into its material and amount/char
			String[] results = ingredientString.split(",");
			
			Material ingredientMaterial = Material.matchMaterial(results[0]);
			
			if (ingredientMaterial == null) {
				
				ingredient = CraftFX.getCustomItem(results[0]);
				
				if (ingredient == null)
					exception("Material name \"{0}\" is invalid.", results[0]);
								
			} else {
				
				ingredient = new ItemStack(ingredientMaterial, 1);
				
			}
						
			int amount = 0;
			char key = '\u0000';
			
			try {
				
				amount = Integer.valueOf(results[1]);
				
			} catch (NumberFormatException e) {
								
				key = results[1].charAt(0);
				
			} catch (IndexOutOfBoundsException e) {
				
				amount = 1;
				
			}
			
			ingredientsList.add(new Ingredient(ingredient, amount, key));
			
		}
		
		return ingredientsList;
		
	}
	
	private Recipe getShapedRecipe(List<Ingredient> ingredients, ItemStack item, ConfigurationSection section) {
		
		FXShapedRecipe recipe = new FXShapedRecipe(item);
		
		List<String> shape = section.getStringList("shape");
		recipe.shape(shape.toArray(new String[0]));
		
		for (Ingredient ingredient : ingredients)
			recipe.setIngredient(ingredient.key, ingredient.item);
		
		return recipe;
		
	}
	
	private Recipe getShapelessRecipe(List<Ingredient> ingredients, ItemStack item) {
		
		FXShapelessRecipe recipe = new FXShapelessRecipe(item);
		
		for (Ingredient ingredient : ingredients)
			recipe.addIngredient(ingredient.amount, ingredient.item);
		
		return recipe;
		
	}
	
	private Recipe getFurnaceRecipe(List<Ingredient> ingredients, ItemStack item) {
		
		return new FXFurnaceRecipe(item, ingredients.get(0).item);
		
	}
	
	private static void exception(String message, Object... objects) throws Exception {
		throw new Exception(FormatUtil.format(message, objects));
	}
	
	private List<ConfigurationSection> getParentConfigurationSections() {
		
		File itemsFolder = new File(plugin.getDataFolder(), "items");
		
		if (!itemsFolder.exists()) {
			itemsFolder.mkdir();
			return Lists.newArrayList();
		}
		
		List<ConfigurationSection> sections = Lists.newArrayList();
		
		for (File configFile : itemsFolder.listFiles())
			sections.add(YamlConfiguration.loadConfiguration(configFile));
		
		return sections;
		
	}
	
}
