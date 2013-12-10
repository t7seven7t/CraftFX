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

import org.bukkit.ChatColor;
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
		
		// Statistics
		int recipeCount = 0;
		int effectCount = 0;
		int triggerCount = 0;
		
		List<ConfigurationSection> parents = getParentConfigurationSections();
		parents.add(plugin.getConfig().getConfigurationSection("items"));
		
		Map<String, ConfigurationSection> keys = Maps.newHashMap();
		Map<String, ConfigurationSection> orderedKeys = Maps.newLinkedHashMap();
		
		for (ConfigurationSection parent : parents) {
			for (String key : parent.getKeys(false)) {
				keys.put(key, parent.getConfigurationSection(key));
			}
		}
		
		while (keys.size() != orderedKeys.size()) {
			
			for (Entry<String, ConfigurationSection> entry : keys.entrySet()) {
				
				if (orderedKeys.containsKey(entry.getKey()))
					continue;
				
				boolean containsCustomIngredient = false;
				
				ConfigurationSection section = null;
				
				if (entry.getValue().contains("recipe"))
					section = entry.getValue().getConfigurationSection("recipe");
				else if (entry.getValue().contains("recipes"))
					section = entry.getValue().getConfigurationSection("recipes");
				
				// Has no ingredients
				if (section == null) {
					containsCustomIngredient = true;
				} else {
					for (String ingredient : section.getStringList("ingredients")) {
						
						String[] results = ingredient.split(",");
						
						for (Entry<String, ConfigurationSection> entry1 : keys.entrySet()) {
							if (((entry1.getValue().contains("name") 
									&& ChatColor.stripColor(entry1.getValue().getString("name")).equals(ChatColor.stripColor(results[0])))
									|| ChatColor.stripColor(entry1.getKey()).equals(ChatColor.stripColor(results[0])))
									&& !orderedKeys.containsKey(entry1.getKey())) {								
								
								containsCustomIngredient = true;
								break;
							}
						}
						
						if (containsCustomIngredient)
							break;
								
					}
				}
				
				if (!containsCustomIngredient)
					orderedKeys.put(entry.getKey(), entry.getValue());
			
			}
			
		}
		
		ConfigurationSection section;	
		String name;
		
		for (Entry<String, ConfigurationSection> entry : orderedKeys.entrySet()) {
			section = entry.getValue();
			name = entry.getKey();

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
				
				ItemData data = plugin.newItem(item);
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
				if (section.contains("potion-effects")) {
					
					List<PotionEffect> potionEffectsList = getPotionEffects(section.getStringList(Potion.POTION_EFFECTS_PATH));
					
					// Item is a potion
					if (meta instanceof PotionMeta) {
						
						for (PotionEffect effect : potionEffectsList)
							((PotionMeta) meta).addCustomEffect(effect, true);
						
					}
					// Item is not a potion - register on-use listening
					else if (section.contains("potion-effects-triggers")) {
						
						List<Trigger> triggers = getTriggers(section.getString("potion-effects-triggers"), true);
						
						triggerCount += triggers.size();
						effectCount++;
						
						for (Trigger trigger : triggers)
							data.addTriggerEffect(trigger, EffectFactory.newEffect(EffectType.get("POTION"), trigger, item, section));
						
					}
					
				}
				
				// Update item meta data
				item.setItemMeta(meta);			
													
				/**
				 * 
				 * BEGIN Effect Registrations
				 * 
				 */
				
				Map<Trigger, List<Effect>> effectMap = null;
				
				if (section.contains("effect")) {
					
					effectMap = getEffect(section.getConfigurationSection("effect"), item);
											
				} else if (section.contains("effects")) {
					
					effectMap = getEffects(section.getConfigurationSection("effects"), item);
					
				}					
				
				if (effectMap != null) {
					
					triggerCount += effectMap.size();
					effectCount += registerTriggerMap(effectMap, data);						
					
				}
				
				if (section.contains("cooldown")) {
					
					data.setCooldown((long) (section.getDouble("cooldown") * 20L));
					
				}
				
				if (section.getBoolean("show-cooldown-message", true)) {
					
					data.displayCooldownMessage();
					
				}
				
				/**
				 * 
				 * BEGIN Recipe registrations
				 * 
				 */
							
				// Set recipe if specified
				if (section.contains("recipe")) {
					
					registerRecipe(section.getConfigurationSection("recipe"), item, data);
					recipeCount++;
					
				} else if (section.contains("recipes")) {
					
					recipeCount += registerRecipes(section.getConfigurationSection("recipes"), item, data);
					
				}
				
			} catch (Exception e) {
				
				plugin.getLogHandler().log(Level.SEVERE, "Item {0} encountered the problem: {1}", name, e.getMessage());
				
				if (plugin.getConfig().getBoolean("debug"))
					e.printStackTrace();
				
			}
			
		}
		
		plugin.getLogHandler().log("{0} items loaded with {1} recipes, {2} triggers and {3} effects.", plugin.getItemDataList().size(), recipeCount, triggerCount, effectCount);
				
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
	private int registerTriggerMap(Map<Trigger, List<Effect>> triggerMap, ItemData data) {
		
		int effectCount = 0;
		
		for (Entry<Trigger, List<Effect>> entry : triggerMap.entrySet()) {
			
			effectCount += entry.getValue().size();
			
			data.addTriggerEffects(entry.getKey(), entry.getValue());
			
		}
		
		return effectCount;
		
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
		
		List<Trigger> triggers = getTriggers(section.getString("triggers"), false);
		
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
	
	private List<Trigger> getTriggers(String triggersString, boolean isPotion) throws Exception {
		
		if (triggersString == null)
			exception("No triggers specified.");
		
		List<Trigger> triggers = Lists.newArrayList();
		
		for (String triggerString : triggersString.split(",")) {
										
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
	
	private int registerRecipes(ConfigurationSection section, ItemStack item, ItemData data) throws Exception {
		
		int recipeCount = 0;
		
		/**
		 * Key names can be anything
		 */
		
		for (String key : section.getKeys(false)) {
			
			registerRecipe(section.getConfigurationSection(key), item, data); 
			recipeCount++;

		}
		
		return recipeCount;
		
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
	
	public ItemStack getCustomItem(String displayName) {
		
		for (ItemData data : plugin.getItemDataList()) {
			
			ItemStack item = data.getItem();
			
			if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(displayName)))
				return item;
			
		}
		
		return null;
		
	}

	private List<Ingredient> getIngredients(List<String> ingredientsStringList) throws Exception {
		
		List<Ingredient> ingredientsList = Lists.newArrayList();
		
		for (String ingredientString : ingredientsStringList) {
			
			ItemStack ingredient;
			
			// Split each ingredient into its material and amount/char
			String[] results = ingredientString.split(",");
			
			Material ingredientMaterial = Material.matchMaterial(results[0]);
			
			if (ingredientMaterial == null) {
				
				ingredient = getCustomItem(results[0]);
				
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
