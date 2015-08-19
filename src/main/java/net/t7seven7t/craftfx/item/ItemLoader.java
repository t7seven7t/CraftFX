package net.t7seven7t.craftfx.item;

import com.google.common.collect.Lists;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.recipe.RecipeLoader;
import net.t7seven7t.craftfx.trigger.TriggerLoader;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.MaterialDataUtil;
import net.t7seven7t.util.PotionEffectUtil;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.AUTHOR;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.DURABILITY;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.ENCHANTMENTS;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.LEATHER_ARMOR;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.LORE;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.MATERIAL;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.NAME;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.OWNER;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.PAGES;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.POTION_EFFECTS;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.RECIPE;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.RECIPES_SECTION;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.TITLE;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.TRIGGER;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.TRIGGERS_SECTION;

/**
 *
 */
public class ItemLoader {

    private final TriggerLoader triggerLoader;
    private final RecipeLoader recipeLoader;
    private final CraftFX plugin;

    public ItemLoader(final CraftFX plugin) {
        this.triggerLoader = new TriggerLoader();
        this.recipeLoader = new RecipeLoader();
        this.plugin = plugin;
    }

    /**
     * Loads all ItemDefinitions from known places (config.items, all .yml files in items dir)
     */
    public void loadItems() {
        List<ConfigurationSection> roots = getRootConfigurationSections();
        List<ItemDefinition> items = Lists.newArrayList();
        roots.forEach(c -> items.addAll(loadItems(c)));
        int recipes = 0;
        int triggers = 0;
        int effects = 0;
        for (ItemDefinition i : items) {
            try {
                postLoad(i);
                plugin.getItemRegistry().register(i);
            } catch (Exception e) {
                logException(i.getName(), e);
                break;
            }

            triggers += i.getTriggers().size();
            recipes += i.getRecipes().size();
            effects += i.getTriggers().stream().mapToInt(t -> t.getEffects().size()).sum();
        }

        plugin.getLogger().info(items.size() + " items loaded with " + recipes + " recipes, " +
                triggers + " triggers and " + effects + " effects.");
    }

    /**
     * Handles logging of an exception
     */
    private void logException(String name, Exception e) {
        plugin.getLogger().log(Level.SEVERE,
                "Item '" + name + "' encountered the problem: " + e.getMessage());

        if (plugin.getConfig().getBoolean("debug", false)) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a list of ConfigurationSections from places where CraftFX may expect items to load from
     */
    private List<ConfigurationSection> getRootConfigurationSections() {
        List<ConfigurationSection> list = Lists.newArrayList();
        if (plugin.getConfig().contains("items")) {
            list.add(plugin.getConfig().getConfigurationSection("items"));
        }

        File itemsFolder = new File(plugin.getDataFolder(), "items");
        if (!itemsFolder.exists()) {
            itemsFolder.mkdir();
            return list;
        }

        Pattern p = Pattern.compile("\\.jar$");
        for (File file : itemsFolder.listFiles()) {
            if (p.matcher(file.getName()).find()) {
                list.add(YamlConfiguration.loadConfiguration(file));
            }
        }

        return list;
    }

    /**
     * Loads a list of ItemDefinitions from a configuration. Recipes and triggers for items have not
     * yet been created
     */
    private List<ItemDefinition> loadItems(ConfigurationSection config) {
        List<ItemDefinition> items = Lists.newArrayList();
        config.getKeys(false).forEach(k -> {
            try {
                ConfigurationSection section = config.getConfigurationSection(k);
                ItemStack item = loadItem(section);
                items.add(new ItemDefinition(item, section));
            } catch (Exception e) {
                logException(k, e);
            }
        });
        return items;
    }

    /**
     * Creates an ItemStack from a configuration. More information regarding the format of the
     * config can be found in README.MD
     */
    public ItemStack loadItem(ConfigurationSection config) throws Exception {
        if (!config.contains(MATERIAL)) {
            throw new Exception("Config does not contain material path '" + MATERIAL + "'");
        }

        MaterialData data = MaterialDataUtil.getMaterialData(config.getString(MATERIAL));
        if (data == null) {
            throw new Exception("Material '" + config.getString(MATERIAL) + "' is invalid.");
        }
        ItemStack item = data.toItemStack(1);

        if (config.contains(DURABILITY)) {
            item.setDurability((short) config.getInt(DURABILITY));
        }

        ItemMeta meta = item.getItemMeta();

        // Set custom name of item
        String name = FormatUtil.format(config.getString(NAME, config.getName()));
        meta.setDisplayName(name);

        // Add lore
        addLore(meta, config);

        // Add enchantments
        addEnchantments(meta, config);

        // Add other meta types if they exist:
        // LeatherArmorMeta
        if (meta instanceof LeatherArmorMeta) {
            addLeatherArmorMeta(meta, config);
        }
        // BookMeta
        if (meta instanceof BookMeta) {
            addBookMeta(meta, config);
        }
        // SkullMeta
        if (meta instanceof SkullMeta) {
            addSkullMeta(meta, config);
        }
        // PotionMeta
        if (meta instanceof PotionMeta) {
            addPotionMeta(meta, config);
        }

        // Update item meta
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Adds potion effects to PotionMeta
     *
     * @param meta   ItemMeta to change
     * @param config Config to read from
     * @throws Exception if an error occurs
     */
    private void addPotionMeta(ItemMeta meta, ConfigurationSection config) throws Exception {
        if (config.contains(POTION_EFFECTS)) {
            List<PotionEffect> potionEffectList = PotionEffectUtil
                    .getPotionEffects(config.getStringList(POTION_EFFECTS));

            // Add each effect to the meta
            potionEffectList.forEach(e -> ((PotionMeta) meta).addCustomEffect(e, true));
        }
    }

    /**
     * Adds ownership data to SkullMeta
     *
     * @param meta   ItemMeta to change
     * @param config Config to read from
     */
    private void addSkullMeta(ItemMeta meta, ConfigurationSection config) {
        if (config.contains(OWNER)) {
            ((SkullMeta) meta).setOwner(config.getString(OWNER));
        }
    }

    /**
     * Adds pages, title, and author to BookMeta
     *
     * @param meta   ItemMeta to change
     * @param config Config to read from
     */
    private void addBookMeta(ItemMeta meta, ConfigurationSection config) {
        BookMeta bMeta = (BookMeta) meta;
        if (config.contains(PAGES)) {
            bMeta.setPages(config.getStringList(PAGES));
        }

        if (config.contains(AUTHOR)) {
            bMeta.setAuthor(config.getString(AUTHOR));
        }

        if (config.contains(TITLE)) {
            bMeta.setTitle(config.getString(TITLE));
        }
    }

    /**
     * Adds lore to ItemMeta
     *
     * @param meta   meta ItemMeta to change
     * @param config Config to read from
     */
    private void addLore(ItemMeta meta, ConfigurationSection config) {
        List<String> lore;
        if (config.isList(LORE)) {
            lore = config.getStringList(LORE);
        } else if (config.contains(LORE)) {
            lore = Arrays.asList(config.getString(LORE).split("\\|"));
        } else {
            return;
        }
        // Replace color codes: as well as set lore
        meta.setLore(lore.stream().map(l -> FormatUtil.format(l)).collect(Collectors.toList()));
    }

    /**
     * Adds color to LeatherArmorMeta
     *
     * @param meta   ItemMeta to change
     * @param config Config to read from
     * @throws Exception if there was an error
     */
    private void addLeatherArmorMeta(ItemMeta meta, ConfigurationSection config) throws Exception {
        if (!config.contains(LEATHER_ARMOR)) {
            return;
        }

        String color = config.getString(LEATHER_ARMOR);
        String[] rgb = color.split(",");
        if (rgb.length < 3) {
            throw new Exception("Color tag '" + color + "' is invalid");
        }

        ((LeatherArmorMeta) meta).setColor(
                Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]),
                        Integer.parseInt(rgb[2])));
    }

    /**
     * Adds enchantments to an ItemMeta from config
     *
     * @param meta   ItemMeta to change
     * @param config Config to read from
     * @throws Exception If an error occurs
     */
    private void addEnchantments(ItemMeta meta, ConfigurationSection config) throws Exception {
        if (!config.contains(ENCHANTMENTS)) {
            return;
        }

        List<String> values = config.getStringList(ENCHANTMENTS);
        for (String value : values) {
            // Split into name and result
            String[] split = value.split(":");

            String enchantString = split[0].toUpperCase().replaceAll("\\s+", "_")
                    .replaceAll("\\W", "");
            Enchantment enchantment = Enchantment.getByName(enchantString);

            if (enchantment == null) {
                throw new Exception("Enchantment '" + enchantString + "' is invalid.");
            }

            meta.addEnchant(enchantment, Integer.parseInt(split[1]), true);
        }
    }

    /**
     * Signalises that all item definitions have been created and can be used in recipes, etc
     */
    private void postLoad(ItemDefinition item) throws Exception {
        loadRecipes(item); // TODO: Register recipes
        loadTriggers(item); // TODO: add trigger types to quick search map
    }

    /**
     * Load recipes for this item from the config but doesn't yet register them to the server
     */
    private void loadRecipes(ItemDefinition item) throws Exception {
        if (item.config.contains(RECIPE)) {
            item.recipeList.add(
                    recipeLoader.load(item, item.config.getConfigurationSection(RECIPE)));
        }

        if (item.config.contains(RECIPES_SECTION)) {
            for (String key : item.config.getConfigurationSection(RECIPES_SECTION).getKeys(false)) {
                item.recipeList.add(recipeLoader.load(item,
                        item.config.getConfigurationSection(RECIPES_SECTION + "." + key)));
            }
        }
    }

    /**
     * Loads all triggers and their effects from the config but doesn't yet register anything to
     * CraftFX
     */
    private void loadTriggers(ItemDefinition item) throws Exception {
        if (item.config.contains(TRIGGER)) {
            loadTriggers(item, item.config.getConfigurationSection(TRIGGER));
        }

        if (item.config.contains(TRIGGERS_SECTION)) {
            for (String key : item.config.getConfigurationSection(TRIGGERS_SECTION)
                    .getKeys(false)) {
                loadTriggers(item,
                        item.config.getConfigurationSection(TRIGGERS_SECTION + "." + key));
            }
        }
    }

    /**
     * Loads a trigger and its effects
     *
     * @param config config section to read from
     */
    private void loadTriggers(ItemDefinition item, ConfigurationSection config) throws Exception {
        item.triggerList.addAll(triggerLoader.loadTriggers(config, item));
    }

    public static final class ConfigPath {
        public static final String NAME = "name";
        public static final String MATERIAL = "id";
        public static final String DURABILITY = "durability";
        public static final String LORE = "lore"; // Lore meta
        public static final String LEATHER_ARMOR = "color"; // Colorable: Leather Armor
        public static final String ENCHANTMENTS = "enchants"; // Enchantment meta
        public static final String PAGES = "pages"; // Book meta
        public static final String AUTHOR = "author"; //Book meta
        public static final String TITLE = "title"; // Book meta
        public static final String POTION_EFFECTS = "potion-effects"; // Potion meta
        public static final String OWNER = "owner"; // Skull meta
        public static final String RECIPE = "recipe";
        public static final String RECIPES_SECTION = "recipes";
        public static final String TRIGGER = "trigger";
        public static final String TRIGGERS_SECTION = "triggers";
    }
}
