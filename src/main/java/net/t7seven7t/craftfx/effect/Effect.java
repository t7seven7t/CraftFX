/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.Trigger;
import net.t7seven7t.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author t7seven7t 
 */
public abstract class Effect {
	
	private static final String CANCEL_EVENT_PATH = "cancel-event";
	private static final String AFFECTS_DAMAGER_PATH = "affects-damager";
	private static final String AREA_EFFECT_PATH = "area-effect";
	private static final String AREA_EFFECT_RADIUS_PATH = "area-effect-radius";
	private static final String DELAY_PATH = "delay";
	private static final String EFFECTS_PATH = "effects";
	
	private EffectType effectType;
	private ConfigurationSection config;
	private ItemStack item;
	private Trigger trigger;
		
	final void initialize(EffectType effectType, Trigger trigger, ItemStack item, ConfigurationSection config) throws Exception {
		this.effectType = effectType;
		this.trigger = trigger;
		this.item = item;
		this.config = config;
		
		if (config.contains(CANCEL_EVENT_PATH))
			cancelsAction = config.getBoolean(CANCEL_EVENT_PATH);
		
		if (config.contains(AFFECTS_DAMAGER_PATH))
			affectsDamager = config.getBoolean(AFFECTS_DAMAGER_PATH);
		
		if (config.contains(AREA_EFFECT_PATH))
			areaEffect = config.getBoolean(AREA_EFFECT_PATH);
		
		if (config.contains(AREA_EFFECT_RADIUS_PATH))
			areaEffectDistanceSquared = Math.pow(config.getDouble(AREA_EFFECT_RADIUS_PATH), 2);
		
		if (config.contains(DELAY_PATH))
			delay = config.getLong(DELAY_PATH);
		
		if (config.contains(EFFECTS_PATH)) {
			
			subEffects = Lists.newArrayList();
			
			ConfigurationSection section = config.getConfigurationSection(EFFECTS_PATH);
			
			for (String key : section.getKeys(false)) {
				
				EffectType type = EffectType.get(section.getConfigurationSection(key).getString("type"));
				
				if (type == null)
					throw new Exception(FormatUtil.format("{0} is an invalid effect type.", section.getConfigurationSection(key).getString("type")));
				
				subEffects.add(EffectFactory.newEffect(type, trigger, item, section.getConfigurationSection(key)));
				
			}
			
		}
		
		initialize();
		
	}
	
	public final EffectType getEffectType() { return effectType; }
	public final ItemStack getItem() { return item; }
	public final Trigger getTrigger() { return trigger; }
	public final ConfigurationSection getConfig() { return config; }
	
	protected boolean cancelled = false;
	protected boolean cancelsAction = false;
	protected boolean affectsDamager = false;
	protected boolean areaEffect = false;
	protected double areaEffectDistanceSquared = 4.0;
	protected long delay = 10L;
	protected List<Effect> subEffects;
	
	public boolean isCancelled() { return cancelled; }
	public boolean cancelsAction() { return cancelsAction; }
	public boolean isDamagerAffected() { return affectsDamager; }
	public boolean isAreaEffect() { return areaEffect; }
	public double getAreaEffectDistanceSquared() { return areaEffectDistanceSquared; }
	public long getDelay() { return delay; }
	
	public void initialize() throws Exception { }
		
	public void begin(Player player) { }
	public void begin(LivingEntity entity) { }
		
	public void run(Location location) { }
	public void run(Player player) { }
	public void run(LivingEntity entity) { }
	public void run(Block block) { }
		
	public void end(Player player) { }
	public void end(LivingEntity entity) { }
	
	/**
	 *  Apply multiple objects at once 
	 * @param Activator Activator type that triggered the effect
	 * @param objects Objects to apply the effect to
	 */
	public final void beginAll(Object... objects) {

		all(beginMethods, objects);

	}
	
	public final void runAll(Object... objects) {
		
		for (Object object : objects) {
			if (areaEffect && Location.class.isInstance(object)) {
				
				Location loc = (Location) object;
				
				for (LivingEntity e : loc.getWorld().getLivingEntities()) {
					
					if (e.getLocation().distanceSquared(loc) <= areaEffectDistanceSquared)
						all(runMethods, e);						
					
				}
				
			}
		}
		
		all(runMethods, objects);
		
	}
	
	public final void endAll(Object... objects) {
		
		all(endMethods, objects);
		
	}
	
	private final void all(Map<Class<?>, Method> methods, Object... objects) {
		
		cancelled = false;
		
		for (Object object : objects) {
			
			for (Entry<Class<?>, Method> entry : methods.entrySet()) {
				
				if (entry.getKey().isInstance(object)) {
					try {						
						entry.getValue().invoke(this, object);
						
						if (!this.isCancelled() && subEffects != null) {
							for (Effect effect : subEffects) 
								entry.getValue().invoke(effect, object);
						}

					} catch (InvocationTargetException e) { e.getTargetException().printStackTrace(); 
					} catch (Exception e) { e.printStackTrace();	}
				}
				
			}
			
		}
		
	}
	
	private static Map<Class<?>, Method> beginMethods = Maps.newHashMap();
	private static Map<Class<?>, Method> runMethods = Maps.newHashMap();
	private static Map<Class<?>, Method> endMethods = Maps.newHashMap();

	static {
		
		for (Method method : Effect.class.getMethods()) {
			
			if (method.getName().equals("begin"))
				beginMethods.put(method.getParameterTypes()[0], method);
			else if (method.getName().equals("end"))
				endMethods.put(method.getParameterTypes()[0], method);
			else if (method.getName().equals("run"))
				runMethods.put(method.getParameterTypes()[0], method);
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	protected void updateInventory(Player player) {
		
		player.updateInventory();
		
	}
	
	protected Map<Integer, ItemStack> getSimilarItems(Inventory inventory) {
		
		Map<Integer, ItemStack> items = Maps.newHashMap();
		
		for (int i = 0; i < inventory.getSize(); i++) {
			
			ItemStack stack = inventory.getItem(i);

			if (CraftFX.isSimilar(stack, item))
				items.put(i, stack);
			
		}
		
		return items;
		
	}

}
