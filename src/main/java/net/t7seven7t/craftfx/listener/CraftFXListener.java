/**
 * Copyright (C) 2013 t7seven7t
 */
package net.t7seven7t.craftfx.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.t7seven7t.craftfx.CraftFX;
import net.t7seven7t.craftfx.Trigger;
import net.t7seven7t.craftfx.effect.Effect;
import net.t7seven7t.craftfx.item.ItemData;
import net.t7seven7t.craftfx.recipe.FXFurnaceRecipe;
import net.t7seven7t.craftfx.recipe.FXShapedRecipe;
import net.t7seven7t.craftfx.recipe.FXShapelessRecipe;
import net.t7seven7t.util.FormatUtil;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author t7seven7t
 */
public class CraftFXListener implements Listener {

	private final CraftFX plugin;
	
	public CraftFXListener(final CraftFX plugin) {
		this.plugin = plugin;
		this.playerCooldowns = CraftFX.newPlayerMap();
		
		new BukkitRunnable() {

			public void run() {
				
				Iterator<Entry<Player, Map<ItemStack, Long>>> it = playerCooldowns.entrySet().iterator();
				
				while (it.hasNext()) {
					
					Entry<Player, Map<ItemStack, Long>> entry = it.next();
					
					if (entry.getValue() == null || entry.getValue().isEmpty()) {
						it.remove();
						continue;
					}
					
					Iterator<Entry<ItemStack, Long>> it2 = entry.getValue().entrySet().iterator();
					
					while (it2.hasNext()) {
						
						Entry<ItemStack, Long> entry2 = it2.next();
						
						entry2.setValue(entry2.getValue() - 1L);
						
						if (entry2.getValue() < 0L)
							it2.remove();
						
					}
					
				}
				
			}
			
		}.runTaskTimer(plugin, 0L, 1L);
	}
	
	private static Action[] monitoredInteractionActions = new Action[] {
			Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK,
			Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK };
	
	private Map<Integer, ItemStack> livingProjectiles = Maps.newHashMap();
	private Map<Player, Map<ItemStack, Integer>> equippedPlayers = Maps.newHashMap();
	private Map<Player, Map<ItemStack, Long>> playerCooldowns;
	private List<String> noCooldownPlayers = Lists.newArrayList();
	private List<String> noCooldownMessagePlayers = Lists.newArrayList();
	
	/**
	 * Player performs an action in the air or on a block
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		
		if (!event.hasItem())
			return;
		
		Action eventAction = event.getAction();
		
		List<Effect> effects;
		
		for (Action action : monitoredInteractionActions) {
						
			if (eventAction != action)
				continue;

			Trigger trigger = Trigger.getMatchingTrigger(action);
			
			if ((effects = plugin.getEffects(event.getItem(), trigger)) != null) {
					
				if (isCoolingDown(event.getPlayer(), event.getItem()))
					return;

				for (Effect effect : effects) {
					effect.runAll(event.getPlayer());
					
					// Clicked a block
					if (event.getClickedBlock() != null) {
						
						effect.runAll(event.getClickedBlock().getLocation(),
								event.getClickedBlock());
						
					}
					// Clicked air
					else {
						
						Block target = getTargetBlock(event.getPlayer());
						
						if (target != null)
							effect.runAll(target, target.getLocation());
						
					}
					
					if (!event.isCancelled())
						cancelAction(effect, event);
					
				}
				
				startCooldown(event.getPlayer(), event.getItem());
				
			}
			
		}		
		
	}
	
	/**
	 * Player right clicks an entity
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		
		if (event.getPlayer().getItemInHand() == null)
			return;
		
		if (!(event.getRightClicked() instanceof LivingEntity))
			return;
		
		List<Effect> effects;
		
		if ((effects = plugin.getEffects(event.getPlayer().getItemInHand(), Trigger.RIGHT_CLICK_ENTITY)) != null) {
			
			if (isCoolingDown(event.getPlayer(), event.getPlayer().getItemInHand()))
				return;
			
			for (Effect effect : effects) {
				
				effect.runAll(event.getPlayer(), event.getRightClicked()
						.getLocation(), (LivingEntity) event.getRightClicked());

				if (!event.isCancelled())
					cancelAction(effect, event);
				
			}
			
			startCooldown(event.getPlayer(), event.getPlayer().getItemInHand());
						
		}
		
	}
	
	/**
	 * Player teleports to a location
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		
		applyLivingEntityEffects(Trigger.TELEPORT, event.getPlayer(), event);
		
	}
	
	/**
	 * Entity regains health
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent event) {
		
		if (event.getEntity() instanceof LivingEntity)
			applyLivingEntityEffects(Trigger.REGAIN_HEALTH, (LivingEntity) event.getEntity(), event);
		
	}
	
	/**
	 * Player sends a chat message
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		
		// TODO: Write proper chat hook for effects triggered

	}
	
	/**
	 * Player consumes an item
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		
		applyLivingEntityEffects(Trigger.ITEM_CONSUME, event.getPlayer(), event);
		
		removeEquippedEffects(event.getPlayer(), event.getItem());
		
	}
	
	/**
	 * Player drops an item
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDropItemEvent(final PlayerDropItemEvent event) {
		
		final ItemStack item = event.getItemDrop().getItemStack();
		
		if (plugin.getEffects(item, Trigger.ITEM_HELD) == null)
			return;
		
		new BukkitRunnable() {
			
			public void run() {
				
				if (!CraftFX.isSimilar(item, event.getPlayer().getItemInHand()))
					removeEquippedEffects(event.getPlayer(), item);
				
			}
			
		}.runTask(plugin);
		
	}
	
	/**
	 * Player picks up an item
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPickupItemEvent(final PlayerPickupItemEvent event) {
		
		if (plugin.getEffects(event.getItem().getItemStack(), Trigger.ITEM_HELD) == null)
			return;
		
		final ItemStack item1 = event.getPlayer().getItemInHand();
		final ItemStack item2 = event.getItem().getItemStack();
		final int slot = event.getPlayer().getInventory().getHeldItemSlot();
		
		new BukkitRunnable() {
			
			public void run() {
				
				if (event.getPlayer().getInventory().getHeldItemSlot() == slot 
						&& CraftFX.isSimilar(item2, event.getPlayer().getItemInHand())
						&& !CraftFX.isSimilar(item1, event.getPlayer().getItemInHand()))
					applyEquippedEffects(event.getPlayer(), item2, Trigger.ITEM_HELD);
				
			}
			
		}.runTask(plugin);
		
	}
	
	/**
	 * Item player has equipped or is using breaks
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerItemBreakEvent(PlayerItemBreakEvent event) {
		
		applyLivingEntityEffects(Trigger.ITEM_BREAK, event.getPlayer(), event);
		
		removeEquippedEffects(event.getPlayer(), event.getBrokenItem());
		
	}
	
	/**
	 * Player dies
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		
		applyLivingEntityEffects(Trigger.DEATH, event.getEntity(), event);
		
		for (ItemStack item : getTriggerItems(event.getEntity())) {
			
			removeEquippedEffects(event.getEntity(), item);
			
		}
				
	}
	
	/**
	 * Player empties bucket
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
				
		List<Effect> effects;
									
		if ((effects = plugin.getEffects(event.getPlayer().getItemInHand(), Trigger.EMPTY_BUCKET)) != null) {
							
			for (Effect effect : effects) {
				
				effect.runAll(event.getPlayer(), event.getBlockClicked(),
						event.getBlockClicked().getLocation());
				
				if (!event.isCancelled())
					cancelAction(effect, event);
				
			}
			
		}
		
	}
	
	/**
	 * Entity shoots a projectile
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
				
		ProjectileSource shooter = event.getEntity().getShooter();
		
		if (shooter == null || !(shooter instanceof Player))
			return;
		
		List<Effect> effects;
		
		ItemStack item = ((Player) shooter).getItemInHand();

		if ((effects = plugin.getEffects(item, Trigger.PROJECTILE_LAUNCH)) != null) {
			
			for (Effect effect : effects) {
				
				effect.runAll(((Player) shooter).getLocation(), shooter);
				
				if (!event.isCancelled())
					cancelAction(effect, event);
				
			}
			
		}
		
		if ((effects = plugin.getEffects(item, Trigger.PROJECTILE_HIT)) != null)
			livingProjectiles.put(event.getEntity().getEntityId(), item);
			
		
	}
	
	/**
	 * Projectile hits an entity
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onProjectileHitEvent(ProjectileHitEvent event) {
		
		final int entityId = event.getEntity().getEntityId();
		
		if (!livingProjectiles.containsKey(entityId))
			return;
		
		new BukkitRunnable() {

			@Override
			public void run() {
				
				livingProjectiles.remove(entityId);
				
			}
			
		}.runTaskLater(plugin, 1L);
		
	}
	
	/**
	 * Entity damages another entity
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		
		Trigger trigger;
		List<ItemStack> items;
		
		if (event.getDamager() instanceof LivingEntity) {
			
			// Items that might set off an effect
			items = getTriggerItems((LivingEntity) event.getDamager());
			trigger = Trigger.HIT_ENTITY;		
			
		} else if (event.getDamager() instanceof Projectile) {
			
			items = Lists.newArrayList(livingProjectiles.get(event.getDamager().getEntityId()));
			trigger = Trigger.PROJECTILE_HIT;
			
		} else {
			
			return;
			
		}
		
		List<Effect> effects;
		for (ItemStack item : items) {
			
			if (item == null)
				continue;
			
			if ((effects = plugin.getEffects(item, trigger)) != null) {
				
				if (event.getDamager() instanceof Player) {
					if (isCoolingDown((Player) event.getDamager(), item))
						return;
				}
				
				for (Effect effect : effects) {
					Entity affected = event.getEntity();
					
					if (effect.isDamagerAffected())
						affected = event.getDamager();
					
					effect.runAll(affected, affected.getLocation());
					
					if (!event.isCancelled())
						cancelAction(effect, event);
					
				}
				
				if (event.getDamager() instanceof Player)
					startCooldown((Player) event.getDamager(), item);
				
			}

		}	
		
	}
	
	/**
	 * Entity damaged
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		
		if (!(event.getEntity() instanceof LivingEntity))
			return;
		
		applyLivingEntityEffects(Trigger.LOSE_HEALTH, (LivingEntity) event.getEntity(), event);
		
	}
	
	/**
	 * Player moved
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		
		applyLivingEntityEffects(Trigger.MOVE, event.getPlayer(), event);
		
	}
	
	/**
	 * Player changed held item
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
						
		ItemStack itemNew = event.getPlayer().getInventory().getItem(event.getNewSlot());
				
		applyEquippedEffects(event.getPlayer(), itemNew, Trigger.ITEM_HELD);
		
		ItemStack itemOld = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
				
		if (itemNew != null && CraftFX.isSimilar(itemNew, itemOld))
			return;

		removeEquippedEffects(event.getPlayer(), itemOld);
		
	}
	
	/**
	 * Player joins server
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		
		for (ItemStack item : event.getPlayer().getInventory().getArmorContents()) {
			
			applyEquippedEffects(event.getPlayer(), item, Trigger.EQUIPPED);
			
		}
		
		applyEquippedEffects(event.getPlayer(), event.getPlayer().getItemInHand(), Trigger.ITEM_HELD);
		
	}
	
	/**
	 * 
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		
		playerLeave(event.getPlayer());
		
	}
	
	/**
	 * Player is kicked from server
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerKickEvent(PlayerKickEvent event) {
		
		playerLeave(event.getPlayer());
		
	}
	
	private void playerLeave(Player player) {
		
		for (ItemStack item : getTriggerItems(player)) {
			
			removeEquippedEffects(player, item);
			
		}		
		
		for (Map<Player, ?> map : plugin.getPlayerMaps()) {
			
			if (map.keySet().contains(player))
				map.remove(player);
			
		}
		
	}
	
	/**
	 * Player clicked in their inventory
	 * @Cancellable
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		
		if (!Player.class.isInstance(event.getWhoClicked()))
			return;
				
		Player player = (Player) event.getWhoClicked();
					
		if (event.getSlotType().equals(SlotType.ARMOR)) {
						
			removeEquippedEffects(player, event.getCurrentItem());
			
			applyEquippedEffects(player, event.getCursor(), Trigger.EQUIPPED);
			
		} else if (event.getSlotType() == SlotType.QUICKBAR && event.getSlot() == player.getInventory().getHeldItemSlot()) {
			
			removeEquippedEffects(player, event.getCurrentItem());

			applyEquippedEffects(player, event.getCursor(), Trigger.ITEM_HELD);
			
		}		
		
	}
	
	/**
	 * Player is about to craft an item
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {
		/**
		 * Check custom items aren't being used in non-plugin related recipes
		 */
		if (plugin.getRecipes(event.getRecipe().getResult()) == null) {
			
			for (ItemStack item : event.getInventory().getMatrix()) {
				
				if (plugin.getRecipes(item) != null) {
					event.getInventory().setResult(null);
					break;
				}
				
			}
			
		}
				
		/**
		 * Extra checking to ensure recipe ingredients match items in the matrix
		 */
		else if (!recipeMatches(event.getRecipe().getResult(), event.getInventory().getMatrix())) {
				
			event.getInventory().setResult(null);
						
		}
			
	}
	
	/**
	 * Item is about to burn in furnace
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFurnaceBurnEvent(FurnaceBurnEvent event) {
		
		if (plugin.getRecipes(event.getFuel()) != null)
			event.setCancelled(true);
		
	}
	
	/**
	 * Item is about to smelt in furnace
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
		/**
		 * Custom items not used in non-plugin recipes
		 */
		if (plugin.getRecipes(event.getResult()) == null) {
			
			if (plugin.getRecipes(event.getSource()) != null)
			event.setCancelled(true);
			
		}
		/**
		 * Check if a valid furnace recipe
		 */
		else {
			List<Recipe> recipes = plugin.getRecipes(event.getResult());
			
			boolean hasRecipe = false;
			
			for (Recipe recipe : recipes) {
				if (recipe instanceof FXFurnaceRecipe) {
					FXFurnaceRecipe furnaceRecipe = (FXFurnaceRecipe) recipe;
					
					if (CraftFX.isSimilar(furnaceRecipe.getInput(), event.getSource()))
						hasRecipe = true;
					
				}				
			}
			
			if (!hasRecipe)
				event.setCancelled(true);
			
		}
	}
	
	/**
	 * Item is about to brew into a potion
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBrewEvent(BrewEvent event) {
		
		if (plugin.getRecipes(event.getContents().getIngredient()) != null)
			event.setCancelled(true);
		
	}
	
	public void toggleCooldown(Player player) {
		
		if (noCooldownPlayers.contains(player.getName()))
			noCooldownPlayers.add(player.getName());
		else
			noCooldownPlayers.remove(player.getName());
		
	}
	
	public void toggleCooldownMessage(Player player) {
		
		if (noCooldownMessagePlayers.contains(player.getName()))
			noCooldownMessagePlayers.add(player.getName());
		else
			noCooldownMessagePlayers.remove(player.getName());
		
	}
	
	private boolean isCoolingDown(Player player, ItemStack item) {
		
		if (playerCooldowns.get(player) == null)
			return false;
		
		ItemStack key;
		if ((key = getMatching(item, playerCooldowns.get(player).keySet())) != null) {
			
			long remaining = playerCooldowns.get(player).get(key);
			
			if (remaining > 0L && !noCooldownPlayers.contains(player.getName())) {
				
				ItemData data = plugin.getItemData(item);
				
				if (data.isCooldownMessage() && !noCooldownMessagePlayers.contains(player.getName()))
					player.sendMessage(FormatUtil.format("{0}That item is currently on cooldown for {1}{2}{3} more seconds", 
						ChatColor.AQUA, ChatColor.GOLD, remaining / 20.0, ChatColor.AQUA));
				
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
	private void startCooldown(Player player, ItemStack item) {

		long cooldown;
		if ((cooldown = plugin.getItemData(item).getCooldown()) > 0L) {
			
			ItemStack itemNorm = new ItemStack(item);

			Map<ItemStack, Long> cooldowns = playerCooldowns.get(player);
			
			if (cooldowns == null)
				cooldowns = Maps.newHashMap();
			
			boolean exists = false;
			
			Iterator<ItemStack> iterator = cooldowns.keySet().iterator();
			
			while (iterator.hasNext()) {
				if (CraftFX.isSimilar(iterator.next(), itemNorm)) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				cooldowns.put(itemNorm, cooldown);
				playerCooldowns.put(player, cooldowns);
			}
			
		}
		
	}
		
	private ItemStack getMatching(ItemStack item, Collection<ItemStack> collection) {
		
		for (ItemStack ingredient : collection) {
			
			if (CraftFX.isSimilar(ingredient, item))
				return ingredient;
			
		}
		
		return null;
		
	}
	
	private int newEffectsRepeater(final Player player, final List<Effect> effects) {
		
		for (Effect effect : effects)
			effect.beginAll(player);
		
		return new BukkitRunnable() {
			
			long counter = 0;
			
			public void run() {
				
				if (!player.isOnline())
					equippedPlayers.remove(player);
								
				if (!equippedPlayers.containsKey(player) || !equippedPlayers.get(player).containsValue(this.getTaskId())) {
					
					for (Effect effect : effects) 
						effect.endAll(player);
											
					this.cancel();
					return;
					
				}
				
				counter++;
				
				for (Effect effect : effects) {
					
					if (effect.getDelay() != 0 && counter % effect.getDelay() == 0)
						effect.runAll(player, player.getLocation());
					
				}
				
			}			
			
		}.runTaskTimer(plugin, 0L, 1L).getTaskId();
		
	}
	
	/**
	 * TODO: Equipped items include certain parts of the inventory as well?
	 * @param trigger Trigger method for effect
	 * @param entity Entity that activated the effect
	 * @param event Event that activated the effect
	 */
	private void applyLivingEntityEffects(Trigger trigger, LivingEntity entity, Event event) {
		
		List<Effect> effects;
		boolean isCancelled = false;

		List<ItemStack> items = getTriggerItems(entity);
		
		// Iterate over all possible items to check if any have effects
		for (ItemStack item : items) {
			
			if ((effects = plugin.getEffects(item, trigger)) != null) {
				
				for (Effect effect : effects) {
					
					effect.runAll(entity, entity.getLocation());
					
					if (!isCancelled)
						isCancelled = cancelAction(effect, event);
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Items that might set off an effect
	 * @param entity LivingEntity that activated effect
	 * @return list of items that may have activated effect
	 */
	private List<ItemStack> getTriggerItems(LivingEntity entity) {
		
		// Items that might set off an effect
		List<ItemStack> items = Lists.newArrayList();
		
		if (entity instanceof Player)
			items.add(((Player) entity).getItemInHand());
		
		for (ItemStack item : entity.getEquipment().getArmorContents())
			items.add(item);
		
		return items;
		
	}
	
	private boolean recipeMatches(ItemStack result, ItemStack[] matrix) {
		
		List<Recipe> recipes = plugin.getRecipes(result);
		boolean ret = false;
		
		for (Recipe recipe : recipes) {
			
			boolean matches = true;
			
			if (recipe == null)
				continue;

			if (recipe instanceof FXShapedRecipe) {
				FXShapedRecipe shapedRecipe = (FXShapedRecipe) recipe;
							
				matches = shapedRecipe.matches(matrix);
				
			} else if (recipe instanceof FXShapelessRecipe) {
				FXShapelessRecipe shapelessRecipe = (FXShapelessRecipe) recipe;
				
				Map<ItemStack, Integer> matrixCount = getItemCount(matrix);
				Map<ItemStack, Integer> recipeCount = getItemCount(shapelessRecipe.getIngredientList().toArray(new ItemStack[0]));
				
				for (Entry<ItemStack, Integer> entry : recipeCount.entrySet()) {
					
					ItemStack item;
					
					if ((item = getMatching(entry.getKey(), matrixCount.keySet())) != null) {
						
						if (entry.getValue() != matrixCount.get(item))
							matches = false;
						
					}
					
				}
				
			}
			
			if (matches)
				ret = true;
			
		}
		
		return ret;
		
	}
	
	private Map<ItemStack, Integer> getItemCount(ItemStack[] items) {
		
		Map<ItemStack, Integer> count = Maps.newHashMap();
		
		for (ItemStack item : items) {
			boolean counted = false;
			
			for (Entry<ItemStack, Integer> entry : count.entrySet()) {
				
				if (CraftFX.isSimilar(entry.getKey(), item)) {					
					
					entry.setValue(entry.getValue() + 1);
					counted = true;
					break;
					
				}
				
			}
			
			if (!counted)
				count.put(item, 1);
			
		}
		
		return count;
		
	}
	
	private boolean cancelAction(Effect effect, Event event) {
			
		boolean cancelled = false;
		
		if (effect.cancelsAction() && event instanceof Cancellable) {
			
			cancelled = true;
			((Cancellable) event).setCancelled(cancelled);
			
		}
		
		return cancelled;
	}
	
	@SuppressWarnings({ "deprecation" })
	private Block getTargetBlock(LivingEntity entity) {
		
		return entity.getTargetBlock(null, 100);
		
	}
	
	private void removeEquippedEffects(Player player, ItemStack item) {
				
		if (plugin.getEffects(item, Trigger.EQUIPPED) != null 
				|| plugin.getEffects(item, Trigger.ITEM_HELD) != null) {
						
			Map<ItemStack, Integer> tasks = equippedPlayers.get(player);
						
			if (tasks == null)
				tasks = Maps.newHashMap();
			
			Iterator<ItemStack> iterator = tasks.keySet().iterator();
			
			while (iterator.hasNext()) {
				if (CraftFX.isSimilar(iterator.next(), item))
					iterator.remove();
			}
			
			equippedPlayers.put(player, tasks);
			
		}
		
	}
	
	private void applyEquippedEffects(Player player, ItemStack item, Trigger trigger) {
				
		List<Effect> effects;
				
		if ((effects = plugin.getEffects(item, trigger)) != null) {
						
			ItemStack itemNorm = new ItemStack(item);
			itemNorm.setAmount(1);
			
			Map<ItemStack, Integer> tasks = equippedPlayers.get(player);
						
			if (tasks == null)
				tasks = Maps.newHashMap();
			
			boolean exists = false;
			
			Iterator<ItemStack> iterator = tasks.keySet().iterator();
			
			while (iterator.hasNext()) {
				if (CraftFX.isSimilar(iterator.next(), itemNorm)) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				tasks.put(itemNorm, newEffectsRepeater(player, effects));
				equippedPlayers.put(player, tasks);
			}
			
		}
		
	}
	
}
