package net.t7seven7t.craftfx.item;

import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.recipe.RecipeLoader;
import net.t7seven7t.craftfx.trigger.TriggerLoader;
import net.t7seven7t.craftfx.util.MessageUtil;
import net.t7seven7t.util.MaterialDataUtil;
import net.t7seven7t.util.PotionEffectUtil;

import org.bukkit.Bukkit;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.AUTHOR;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.DURABILITY;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.ENCHANTMENTS;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.LEATHER_ARMOR;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.LORE;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.MATERIAL;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.NAME;
import static net.t7seven7t.craftfx.item.ItemLoader.ConfigPath.NBT;
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

    private final RecipeLoader recipeLoader = new RecipeLoader();
    private final TriggerLoader triggerLoader = new TriggerLoader();
    private final CraftFX fx = CraftFX.instance();

    public void loadItems() {
        List<ConfigurationSection> roots = getRootConfigurationSections();
        List<ItemDefinition> items = new ArrayList<>();
        roots.forEach(c -> items.addAll(loadItems(c)));
        items.forEach(i -> fx.getItemRegistry().register(i));
        int recipes = 0;
        for (ItemDefinition i : items) {
            try {
                postLoad(i);
            } catch (Exception e) {
                logException(i.getName(), e);
                continue;
            }
            recipes += i.getRecipes().size();
        }
        CraftFX.log().info("%s items loaded. %s recipes added.", items.size(), recipes);
    }

    private void logException(String name, Throwable t) {
        CraftFX.log().severe("Item '%s' encountered the problem: %s", name, t.getMessage(), t);
    }

    /**
     * Gets a list of ConfigurationSections for all the places where CraftFX may expect to load
     * items from
     *
     * @return List of ConfigurationSections
     */
    private List<ConfigurationSection> getRootConfigurationSections() {
        final List<ConfigurationSection> ret = new ArrayList<>();
        if (CraftFX.plugin().getConfig().contains("items")) {
            ret.add(CraftFX.plugin().getConfig().getConfigurationSection("items"));
        }

        File itemsFolder = new File(CraftFX.plugin().getDataFolder(), "items");
        if (!itemsFolder.exists()) {
            itemsFolder.mkdir();
            return ret;
        }

        final Pattern p = Pattern.compile("\\.jar$");
        for (File file : itemsFolder.listFiles()) {
            if (p.matcher(file.getName()).find()) {
                ret.add(YamlConfiguration.loadConfiguration(file));
            }
        }

        return ret;
    }

    /**
     * Loads a list of ItemDefinitions from a configuration. Recipes and triggers for items have not
     * yet been created
     *
     * @param config config section to load from
     * @return a list of ItemDefinitions
     */
    private List<ItemDefinition> loadItems(ConfigurationSection config) {
        List<ItemDefinition> items = new ArrayList<>();
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
     * config can be found in the readme and wiki documentation.
     *
     * @param config config section to load from
     * @return an ItemStack
     * @throws Exception any exception thrown while attempting to load the item
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
        if (!fx.getNmsInterface().isValidItem(item)) {
            throw new Exception("Material '" + item.getType() + "' is no longer a valid " +
                    "material in Minecraft 1.8+ and will not show in inventories.");
        }

        if (config.contains(DURABILITY)) {
            item.setDurability((short) config.getInt(DURABILITY));
        }

        ItemMeta meta = item.getItemMeta();

        // Set custom name of item
        String name = MessageUtil.format(config.getString(NAME, config.getName()));
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

        if (config.contains(NBT)) {
            String nbt = config.getString(NBT);
            if (!nbt.matches("\\{.+\\}")) {
                nbt = "{" + nbt + "}";
            }
            item = fx.getNmsInterface().applyNBT(item, nbt);
        }

        item = fx.getNmsInterface().applyNBT(item,
                "{AttributeModifiers:[{AttributeName:\"craftfx.item\",Name:\"craftfx.item\",Amount:0,Operation:0,UUIDLeast:1337,UUIDMost:39681}]}");

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
        meta.setLore(lore.stream().map(l -> MessageUtil.format(l)).collect(Collectors.toList()));
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
     * Signalises that all item definitions have been created and can be used in recipes, triggers,
     * etc
     */
    private void postLoad(ItemDefinition item) throws Exception {
        loadRecipes(item);
        loadTriggers(item);
        item.getRecipes().forEach(Bukkit::addRecipe);
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
     * Load triggers for this item from the config
     */
    private void loadTriggers(ItemDefinition item) throws Exception {
        if (item.config.contains(TRIGGER)) {
            triggerLoader.loadTriggers(item, item.config.getConfigurationSection(TRIGGER));
        }

        if (item.config.contains(TRIGGERS_SECTION)) {
            for (String key : item.config.getConfigurationSection(TRIGGERS_SECTION)
                    .getKeys(false)) {
                triggerLoader.loadTriggers(item,
                        item.config.getConfigurationSection(TRIGGERS_SECTION + "." + key));
            }
        }
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
        public static final String NBT = "nbt";
    }

}
