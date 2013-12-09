/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.effects;

import com.google.common.collect.Maps;
import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.Trigger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author t7seven7t 
 */
public abstract class Effect {
	
	protected final EffectType effectType;
	
	public Effect(final EffectType effectType, Trigger trigger, ItemStack item) {
		this.effectType = effectType;
		this.trigger = trigger;
		this.item = item;
	}
		
	public final EffectType getEffectType() {
		return effectType;
	}
	
	protected boolean cancelsAction = false;
	protected boolean affectsDamager = false;
	
	protected long delay = 10L;
	
	protected ItemStack item;
	protected Trigger trigger;
	
	public boolean cancelsAction() {
		return cancelsAction;
	}
	
	public boolean isDamagerAffected() {
		return affectsDamager;
	}
	
	public long getDelay() {
		return delay;
	}
		
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
		
		all(runMethods, objects);
		
	}
	
	public final void endAll(Object... objects) {
		
		all(endMethods, objects);
		
	}
	
	private final void all(Map<Class<?>, Method> methods, Object... objects) {
		
		for (Entry<Class<?>, Method> entry : methods.entrySet()) {
			
			for (Object object : objects) {
								
				if (entry.getKey().isInstance(object))
					try {
						entry.getValue().invoke(this, object);
					} catch (InvocationTargetException e) { e.getTargetException().printStackTrace(); 
					} catch (Exception e) { e.printStackTrace();	}

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
