package com.DarkBlade12.ModernWeapons.Weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.DarkBlade12.ModernWeapons.ModernWeapons;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

//import net.minecraft.server.v1_13_R1.Material;
import org.bukkit.Material;
import org.bukkit.Particle;

@SuppressWarnings("deprecation")
public class Gun {
	
	Logger log = Logger.getLogger("Minecraft");

	private ItemStack gunIte;
	private ItemStack ammoIte;
	private List<String> lore = new ArrayList<String>();
	private int velocity;
	private boolean explode;
	private int damage;
	private int headBonus;
	private int magSize;
	private int reloadTime;
	private long shotDelay;
	private int delayBetweenBursts;
	private boolean burst;
	private int burstShots;
	private boolean scope;
	private int zoom;
	private double spreadBonus;
	private boolean aiming;
	private int expRange;
	private int expDamage;
	private String name;
	private String index;
	private Player holder;
	private String hname;
	private int shotsLeft;
	private double recoil;
	private int shots;
	private double spread;
	private SoundType sound;
	private EffectType hitEffect;
	private EffectType shootEffect;
	private int hitEffectData;
	private int shootEffectData;
	private String bullet;
	private int range;
	private boolean reloading;
	private boolean boltAction;
	private boolean boltActionPerform;
	private boolean unableToShoot;
	private int boltActionDelay;
	private boolean selfImmunity;
	private long cooldownMillis;
	private ItemStack source;
	private ModernWeapons plugin;
	private Configuration config;

	public Gun(String name, Player holder, ModernWeapons ModernWeapons, ItemStack i) {
		this.name = name;
		this.index = name;
		this.holder = holder;
		this.hname = holder.getName();
		this.source = i;
		plugin = ModernWeapons;
		this.config = plugin.getGuns();
		initialize();
	}

	private static enum SoundType {
		SUBMACHINE_GUN("submachinegun"), SHOTGUN("shotgun"), SNIPER("sniper"), ROCKET_LAUNCHER("rocketlauncher"), PISTOL("pistol"), ASSAULT_RIFLE("assaultrifle");
		private String name;
		private static final Map<String, SoundType> NAME_MAP = new HashMap<String, SoundType>();
		static {
			for (SoundType type : values()) {
				if (type.name != null) {
					NAME_MAP.put(type.getName(), type);
				}
			}
		}

		private SoundType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static SoundType fromName(String name) {
			if (name == null) {
				return null;
			}
			if (NAME_MAP.get(name.toLowerCase()) == null) {
				return null;
			}
			return NAME_MAP.get(name.toLowerCase());
		}
	}

	private static enum EffectType {
		BLOCK_BREAK("blockbreak"), POTION_BREAK("potionbreak"), ENDER_SIGNAL("endersignal"), FLAMES("flames"), SMOKE("smoke");
		private String name;
		private static final Map<String, EffectType> NAME_MAP = new HashMap<String, EffectType>();
		static {
			for (EffectType type : values()) {
				if (type.name != null) {
					NAME_MAP.put(type.getName(), type);
				}
			}
		}

		private EffectType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static EffectType fromName(String name) {
			if (name == null) {
				return null;
			}
			if (NAME_MAP.get(name.toLowerCase()) == null) {
				return null;
			}
			return NAME_MAP.get(name.toLowerCase());
		}
	}

	private void getShotsFromDisplay(ItemStack i) {

		if (i == null) {
			return;
		}
		if (!i.hasItemMeta() || !i.getItemMeta().hasDisplayName()) {
			if (plugin.fullmagStart) {
				this.shotsLeft = this.magSize;
			} else {
				this.shotsLeft = 0;
			}
			return;
		}
		String display = i.getItemMeta().getDisplayName();
		String[] parts = display.split(" \u25AA | \u25AB ");
		if (display.split("\\(").length == 1) {
			if (plugin.fullmagStart) {
				this.shotsLeft = this.magSize;
			} else {
				this.shotsLeft = 0;
			}
		}
		String[] nums = display.split("\\(");
		String num = "";
		if (nums.length >= 2) {
			//num = nums[1].replace("§7)", "").replace("§a", "").replace("§6", "").replace("§4", "").replace("§f", "").replace("§c\u0280", "");
			num = nums[1].replaceAll(ChatColor.GRAY.toString(), "").replaceAll(ChatColor.GREEN.toString(),"").replaceAll(ChatColor.GOLD.toString(), "").replaceAll(ChatColor.DARK_RED.toString(),"").replaceAll(ChatColor.WHITE.toString(),"").replaceAll(ChatColor.RED.toString(), "").replaceAll("[\\D]", "");
		}
		try {
			this.shotsLeft = Integer.parseInt(num);
		} catch (NumberFormatException e) {
			if (plugin.fullmagStart) {
				this.shotsLeft = this.magSize;
			} else {
				this.shotsLeft = 0;
			}
		}
	}

	private void getReloadingFromDisplay(ItemStack i) {
		if (i == null) {
			return;
		}
		if (!i.hasItemMeta() || !i.getItemMeta().hasDisplayName()) {
			this.reloading = false;
			return;
		}
		if (i.getItemMeta().getDisplayName().contains("\u0280")) {	//small caps R
			this.reloading = true;
		} else {
			this.reloading = false;
		}
	}

	private void getBoltActionFromDisplay(ItemStack i) {
		if (i == null) {
			return;
		}
		if (!i.hasItemMeta() || !i.getItemMeta().hasDisplayName()) {
			this.boltActionPerform = false;
			return;
		}
		if (i.getItemMeta().getDisplayName().contains("\u2043")) {	// hypen bullet point
			this.boltActionPerform = true;
		} else {
			this.boltActionPerform = false;
		}
	}

	private void getUnableToShootFromDisplay(ItemStack i) {
		if (i == null) {
			return;
		}
		if (!i.hasItemMeta() || !i.getItemMeta().hasDisplayName()) {
			this.unableToShoot = false;
			return;
		}
		if (i.getItemMeta().getDisplayName().contains(ChatColor.WHITE.toString())) {
			this.unableToShoot = true;
		} else {
			this.unableToShoot = false;
		}
	}

	private void getEffects() {
		String bhstr = config.getString(index + ".Effects.BulletHit");
		if (bhstr != null) {
			String[] split = bhstr.split(",");
			this.hitEffect = EffectType.fromName(split[0]);
			this.hitEffectData = 0;
			if (split.length == 2) {
				try {
					this.hitEffectData = Integer.parseInt(split[1]);
				} catch (NumberFormatException e) {
					this.hitEffectData = 0;
				}
			}
		}
		String bsstr = config.getString(index + ".Effects.BulletShoot");
		if (bsstr != null) {
			String[] split = bsstr.split(",");
			this.shootEffect = EffectType.fromName(split[0]);
			this.shootEffectData = 0;
			if (split.length == 2) {
				try {
					this.shootEffectData = Integer.parseInt(split[1]);
				} catch (NumberFormatException e) {
					this.shootEffectData = 0;
				}
			}
		}
	}

	private long getCooldownMillis() {
		if (this.holder.getMetadata("WeaponCooldown." + this.name).size() > 0) {
			return (long) this.holder.getMetadata("WeaponCooldown." + this.name).get(0).value();
		}
		return System.currentTimeMillis();
	}

	private String getBulletType() {
		if (config.getString(index + ".Shooting.Bullet") != null) {
			String bullet = config.getString(index + ".Shooting.Bullet");
			if (bullet.equalsIgnoreCase("snowball") || bullet.equalsIgnoreCase("egg") || bullet.equalsIgnoreCase("arrow")) {
				return bullet;
			}
		}
		return "snowball";
	}

	private void getAimingFromPlayer() {
		boolean inAim = false;
		if (this.holder.getMetadata("Aiming").size() > 0) {
			inAim = (boolean) this.holder.getMetadata("Aiming").get(0).value();
		}
		this.aiming = inAim;
	}

	private void initialize() {
		// General
		this.gunIte = getItem(config.getString(index + ".General.Item"));
		this.ammoIte = getItem(config.getString(index + ".General.Ammo"));
		this.sound = SoundType.fromName(config.getString(index + ".General.Sound"));
		this.selfImmunity = config.getBoolean(index + ".General.SelfImmunity");
		for (String l : config.getStringList(index + ".General.Lore")) {
			//this.lore.add(l.replace("&", "§"));
			this.lore.add(ChatColor.translateAlternateColorCodes('&', l));
		}
		// Shooting related
		this.velocity = config.getInt(index + ".Shooting.Velocity");
		this.damage = config.getInt(index + ".Shooting.Damage");
		this.headBonus = config.getInt(index + ".Shooting.HeadshotBonus");
		this.shotDelay = config.getLong(index + ".Shooting.Delay");
		this.recoil = config.getDouble(index + ".Shooting.Recoil") * 0.1D;
		this.spread = config.getDouble(index + ".Shooting.Spread") * 0.04D;
		this.shots = config.getInt(index + ".Shooting.ShotAmount");
		if (shots == 0)
			shots = 1;
		this.range = config.getInt(index + ".Shooting.Range");
		this.bullet = getBulletType();
		// Burst related
		this.burst = config.getBoolean(index + ".Bursts.Enabled");
		this.delayBetweenBursts = config.getInt(index + ".Bursts.DelayBetween");
		this.burstShots = config.getInt(index + ".Bursts.ShotAmount");
		// Magazine
		this.magSize = config.getInt(index + ".Magazine.Size");
		this.reloadTime = config.getInt(index + ".Magazine.ReloadTime");
		// Scope
		this.scope = config.getBoolean(index + ".Scope.Enabled");
		this.zoom = config.getInt(index + ".Scope.Zoom");
		this.spreadBonus = config.getDouble(index + ".Scope.SpreadBonus") * 0.04D;
		// Explosion
		this.explode = config.getBoolean(index + ".Explosion.Enabled");
		this.expDamage = config.getInt(index + ".Explosion.Damage");
		this.expRange = config.getInt(index + ".Explosion.Range");
		// Bolt action
		this.boltAction = config.getBoolean(index + ".BoltAction.Enabled");
		this.boltActionDelay = config.getInt(index + ".BoltAction.Delay");
		// Effects
		getEffects();
		// Cooldown
		this.cooldownMillis = getCooldownMillis();
		// Stuff bound bound to weapon
		getShotsFromDisplay(this.source);
		getReloadingFromDisplay(this.source);
		getBoltActionFromDisplay(this.source);
		getUnableToShootFromDisplay(this.source);
		getAimingFromPlayer();
	}

	public void scope() {
		if (!this.hasScope()) {
			return;
		}
		this.holder.getWorld().playSound(this.holder.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 5);
		if (this.aiming) {
			this.holder.removePotionEffect(PotionEffectType.SPEED);
			this.holder.setMetadata("Aiming", new FixedMetadataValue(plugin, false));
			return;
		} else {
			this.holder.addPotionEffect(PotionEffectType.SPEED.createEffect(9600, -this.zoom * 2));
			this.holder.setMetadata("Aiming", new FixedMetadataValue(plugin, true));
			return;
		}
	}

	public void playHitEffect(Location loc) {
		if (this.hitEffect == null) {
			return;
		}
		RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery rq = rc.createQuery();		
		if (plugin.hasWorldGuard) {
			if (!rq.testState(BukkitAdapter.adapt(loc), null, Flags.OTHER_EXPLOSION)) {
				return;
			}
		}
		World w = loc.getWorld();
		switch (this.hitEffect) {
			case BLOCK_BREAK:
				//w.playEffect(loc, Effect.STEP_SOUND, this.hitEffectData);
				w.spawnParticle(Particle.BLOCK_DUST, loc, this.hitEffectData, 0, 0, 0, Material.DEAD_BRAIN_CORAL_BLOCK.createBlockData());
				return;
			case ENDER_SIGNAL:
//				w.playEffect(loc, Effect.ENDER_SIGNAL, this.hitEffectData);
				w.spawnParticle(Particle.PORTAL, loc, this.hitEffectData, 0, 0, 0, 1);
				return;
			case FLAMES:
//				w.playEffect(loc, Effect.MOBSPAWNER_FLAMES, this.hitEffectData);
				w.spawnParticle(Particle.FLAME, loc, this.hitEffectData, 0, 0, 0, 1);
				return;
			case POTION_BREAK:
//				w.playEffect(loc, Effect.POTION_BREAK, this.hitEffectData);
				w.spawnParticle(Particle.SPELL, loc, this.hitEffectData, 0, 0, 0, 0.2);
				return;
			case SMOKE:
//				w.playEffect(loc, Effect.SMOKE, this.hitEffectData);
				w.spawnParticle(Particle.SMOKE_NORMAL, loc, this.hitEffectData, 0, 0, 0, 1);
				return;
		}
	}

	public void playShootEffect() {
		if (this.shootEffect == null) {
			return;
		}
		Location loc = this.holder.getEyeLocation();
		World w = loc.getWorld();
		switch (this.shootEffect) {
			case BLOCK_BREAK:
				//w.playEffect(loc, Effect.STEP_SOUND, this.shootEffectData);
				w.spawnParticle(Particle.BLOCK_DUST, loc.add(1.0,-1.0,-2.0), this.hitEffectData, 0, 0, 0, Material.DEAD_BRAIN_CORAL_BLOCK.createBlockData());
				return;
			case ENDER_SIGNAL:
				//w.playEffect(loc, Effect.ENDER_SIGNAL, this.shootEffectData);
				w.spawnParticle(Particle.PORTAL, loc.add(1.0,-1.0,-2.0), this.hitEffectData, 0, 0, 0, 0.2);
				return;
			case FLAMES:
				//w.playEffect(loc, Effect.MOBSPAWNER_FLAMES, this.shootEffectData);
				w.spawnParticle(Particle.FLAME, loc.add(1.0,-1.0,-2.0), this.hitEffectData, 0, 0, 0, 2);
				return;
			case POTION_BREAK:
				//w.playEffect(loc, Effect.POTION_BREAK, this.shootEffectData);
				w.spawnParticle(Particle.SPELL, loc.add(1.0,-1.0,-2.0), this.hitEffectData, 0, 0, 0, 0.2);
				return;
			case SMOKE:
				//w.playEffect(loc, Effect.SMOKE, this.shootEffectData);
				w.spawnParticle(Particle.SMOKE_NORMAL, loc.add(1.0,-1.0,-2.0), this.hitEffectData, 0, 0, 0, 0.1);
				return;
		}
	}

	private void playSound() {
		if (this.sound == null) {
			return;
		}
		Location loc = this.holder.getEyeLocation();
		World w = loc.getWorld();
		switch (this.sound) {
			case SUBMACHINE_GUN:
				w.playSound(loc, Sound.BLOCK_METAL_HIT, 1, 5);
				w.playSound(loc, Sound.BLOCK_WOOD_HIT, 1, 5);
				w.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 5);
				break;
			case ROCKET_LAUNCHER:
				w.playSound(loc, Sound.ITEM_FLINTANDSTEEL_USE, 1, 5);
				w.playSound(loc, Sound.ENTITY_GHAST_SHOOT , 1, 5);
				w.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BASEDRUM , 1, 5);
				w.playSound(loc, Sound.ENTITY_IRON_GOLEM_HURT, 1, 5);
				break;
			case SHOTGUN:
				w.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 5);
				w.playSound(loc, Sound.ENTITY_SKELETON_HURT, 1, 5);
				break;
			case SNIPER:
				w.playSound(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 5);
				w.playSound(loc, Sound.ENTITY_BLAZE_HURT, 1, 5);
				break;
			case PISTOL:
				w.playSound(loc, Sound.BLOCK_LADDER_STEP, 1, 5);
				w.playSound(loc, Sound.ENTITY_ITEM_BREAK, 1, 5);
				w.playSound(loc, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 5);
				w.playSound(loc, Sound.ENTITY_IRON_GOLEM_HURT, 1, 5);
				w.playSound(loc, Sound.BLOCK_STONE_STEP, 1, 5);
				break;
			case ASSAULT_RIFLE:
				w.playSound(loc, Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 5);
				w.playSound(loc, Sound.ENTITY_WITHER_SHOOT, 1, 5);
				w.playSound(loc, Sound.ENTITY_BAT_TAKEOFF, 1, 5);
				w.playSound(loc, Sound.ITEM_FLINTANDSTEEL_USE, 1, 5);
				w.playSound(loc, Sound.ENTITY_IRON_GOLEM_HURT, 1, 5);
				break;
		}
	}

	private void launchProjectile() {
		Projectile p = null;
		switch (this.bullet.toLowerCase()) {
			case "snowball":
				p = this.holder.launchProjectile(Snowball.class);
				break;
			case "egg":
				p = this.holder.launchProjectile(Egg.class);
				break;
			case "arrow":
				p = this.holder.launchProjectile(Arrow.class);
				break;
		}
		p.setMetadata("WeaponName", new FixedMetadataValue(plugin, name));
		p.setShooter(this.holder);
		double spread = this.spread;
		if (this.aiming && this.spreadBonus != 0) {
			spread += this.spreadBonus;
		}
		if (this.spread < 0) {
			spread = 0;
		}
		if (spread != 0) {
			Random rn = new Random();
			Vector velo = p.getVelocity();
			double rx = rn.nextDouble() * spread;
			if (rn.nextBoolean()) {
				rx = velo.getX() + rx;
			} else {
				rx = velo.getX() - rx;
			}
			double ry = rn.nextDouble() * spread;
			if (rn.nextBoolean()) {
				ry = velo.getY() + ry;
			} else {
				ry = velo.getY() - ry;
			}
			double rz = rn.nextDouble() * spread;
			if (rn.nextBoolean()) {
				rz = velo.getZ() + rz;
			} else {
				rz = velo.getZ() - rz;
			}
			p.setVelocity(new Vector(rx, ry, rz));
		}
		playSound();
		playShootEffect();
		p.setVelocity(p.getVelocity().multiply(this.velocity * 1.0D));
		if (this.range != 0) {
			observeProjectile(p, p.getLocation());
		}
	}

	public void shoot() {
		if (this.reloading || this.boltActionPerform || this.unableToShoot) {
			return;
		}
		if (this.shotsLeft == 0) {
			startReloading();
			return;
		}
		if (this.cooldownMillis > System.currentTimeMillis()) {
			return;
		}
		this.holder.setMetadata("WeaponCooldown." + this.name, new FixedMetadataValue(plugin, System.currentTimeMillis() + this.shotDelay));
		if (!this.burst) {
			for (int i = 1; i <= this.shots; i++) {
				launchProjectile();
			}
		} else {
			//doBurstShot(1, this.holder.getItemInHand());
			doBurstShot(1, this.holder.getInventory().getItemInMainHand());
		}
		if (this.recoil != 0.0D) {
			this.holder.setVelocity(this.holder.getLocation().getDirection().multiply(-this.recoil).setY(0.0D));
		}
		if (this.boltAction) {
			prepareBoltAction();
		} else if (!burst) {
			if (!hasUnlimited()) {
				this.shotsLeft--;
				//refreshItem(this.holder.getItemInHand());
				refreshItem(this.holder.getInventory().getItemInMainHand());
			}
		}
		if (plugin.autoReload && this.shotsLeft == 0) {
			startReloading();
		}
	}

	private void doBurstShot(final int current, final ItemStack fItem) {
		if (current > this.burstShots) {
			return;
		}
		int delay = this.delayBetweenBursts;
		if (current == 1) {
			delay = 0;
		}
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				ItemStack i = fItem;
				if (!boltAction && !hasUnlimited()) {
					if (shotsLeft - 1 < 0) {
						if (plugin.autoReload) {
							startReloading();
						}
						return;
					} else {
						shotsLeft--;
						i = refreshItem(fItem);
					}
				}
				launchProjectile();
				doBurstShot(current + 1, i);
			}
		}, delay);
	}

	private boolean hasUnlimited() {
		return this.holder.getGameMode() == GameMode.CREATIVE && plugin.creativeUnlimited;
	}

	private void prepareBoltAction() {
		this.unableToShoot = true;
		//final ItemStack fItem = refreshItem(this.holder.getItemInHand());
		final ItemStack fItem = refreshItem(this.holder.getInventory().getItemInMainHand());
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				performBoltAction(fItem);
			}
		}, this.boltActionDelay * 20L);
	}

	private void performBoltAction(ItemStack i) {
		this.holder.getWorld().playSound(this.holder.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1, 5);
		this.boltActionPerform = true;
		final ItemStack fItem = this.refreshItem(i);
		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				holder.getWorld().playSound(holder.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 5);
				boltActionPerform = false;
				unableToShoot = false;
				if (!hasUnlimited()) {
					shotsLeft--;
				}
				refreshItem(fItem);
			}
		}, 4L);
	}

	public void observeProjectile(final Projectile p, final Location start) {
		if (p.isDead()) {
			return;
		}
		final int fRange = this.range;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				double distance = p.getLocation().distance(start);
				if (distance >= fRange) {
					p.remove();
				} else {
					observeProjectile(p, start);
				}
			}
		}, 1);
	}

	public void removeAmmo(int amount) {
		if (hasUnlimited()) {
			return;
		}
		ItemStack[] contents = this.holder.getInventory().getContents();
		int remaining = amount;
		for (ItemStack i : contents) {
			if (remaining == 0) {
				break;
			}
			if (i != null) {
				//int id = i.getTypeId();	//F451-07220218
				String id = i.getType().name();
				byte data = i.getData().getData();
				//if (id == this.ammoIte.getTypeId() && data == this.ammoIte.getData().getData()) {	//F451-07222018
				if (id == this.ammoIte.getType().name() && data == this.ammoIte.getData().getData()) {
					if (remaining > 0) {
						if (i.getAmount() == amount) {
							//i.setTypeId(0);	//F451-07222018
							i.setType(Material.AIR);
							break;
						}
						if (i.getAmount() > amount) {
							i.setAmount(i.getAmount() - amount);
							break;
						}
						if (i.getAmount() < amount) {
							remaining = remaining - i.getAmount();
							//i.setTypeId(0);	//F451-07222018
							i.setType(Material.AIR);
						}
					}
				}
			}
		}
		this.holder.getInventory().setContents(contents);
		this.holder.updateInventory();
	}

	public boolean hasAmmoLeft(int amount) {
		if (hasUnlimited()) {
			return true;
		}
		int cmag = getAmmoLeft();
		if (cmag >= amount) {
			return true;
		}
		return false;
	}

	public int getAmmoLeft() {
		if (hasUnlimited()) {
			return this.magSize;
		}
		int cmag = 0;
		ItemStack[] contents = this.holder.getInventory().getContents();
		for (ItemStack stack : contents) {
			if (stack != null) {
				//int id = stack.getTypeId();	//F451-07222018
				String id = stack.getType().name();
				byte data = stack.getData().getData();
				int amount = stack.getAmount();
				//if (id == this.ammoIte.getTypeId() && data == this.ammoIte.getData().getData()) {	//F451-07222018
				if (id == this.ammoIte.getType().name() && data == this.ammoIte.getData().getData()) {
					cmag = cmag + amount;
				}
			}
		}
		return cmag;
	}

	public void startReloading() {
		if (this.shotsLeft == this.magSize || this.reloading || this.boltActionPerform || this.unableToShoot) {
			return;
		}
		int amount = 0;
		int left = getAmmoLeft();
		if (left < 1) {
			if (plugin.messagesEnabled) {
				this.holder.sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+plugin.noMagazine);
			}
			this.holder.getWorld().playSound(this.holder.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 5);
			return;
		} else if (left + this.shotsLeft == this.magSize) {
			removeAmmo(left);
			amount = this.magSize;
		} else if (left + this.shotsLeft > this.magSize) {
			int remove = left - ((left + this.shotsLeft) - this.magSize);
			removeAmmo(remove);
			amount = this.magSize;
		} else if (left + this.shotsLeft < this.magSize) {
			int nshots = left + this.shotsLeft;
			removeAmmo(left);
			amount = nshots;
		}
		this.holder.getWorld().playSound(this.holder.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1, 5);
		this.reloading = true;
		//refreshItem(this.holder.getItemInHand());
		refreshItem(this.holder.getInventory().getItemInMainHand());
		final int fAmount = amount;
		//final ItemStack fItem = this.holder.getItemInHand();
		final ItemStack fItem = this.holder.getInventory().getItemInMainHand();
		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				if (fAmount == magSize) {
					shotsLeft = magSize;
				} else {
					shotsLeft = fAmount;
				}
				reloading = false;
				if (plugin.messagesEnabled) {
					holder.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+plugin.weaponReloaded.replace("%weapon%", name));
				}
				refreshItem(fItem);
				holder.getWorld().playSound(holder.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 5);
				holder.getWorld().playSound(holder.getLocation(), Sound.BLOCK_ANVIL_BREAK , 1, 5);
			}
		}, this.reloadTime * 20L);
	}

	public ItemStack refreshItem(ItemStack i) {
		ChatColor color = ChatColor.GREEN;
		if (this.shotsLeft == 0) {
			color = ChatColor.DARK_RED;
		} else if (this.shotsLeft < this.magSize / 2) {
			color = ChatColor.GOLD;
		}
		if (this.unableToShoot) {
			color = ChatColor.WHITE;
		}
		String spacer = ChatColor.RESET+""+ChatColor.AQUA+" \u25AA ";	//filled small square
		if (this.reloading || this.shotsLeft == 0) {
			spacer = ChatColor.RESET+""+ChatColor.AQUA+" \u25AB ";	// white small square
		} else if (this.boltActionPerform) {
			spacer = ChatColor.RESET+""+ChatColor.AQUA+" \u2043 ";	// hypen bullet point
		}
		String ammo = ChatColor.RESET+""+ChatColor.GRAY+"(" + color + this.shotsLeft + ChatColor.GRAY+")";
		if (this.reloading) {
			ammo += ChatColor.RED+"\u0280";
		}
		String display = ChatColor.DARK_GRAY+""+ChatColor.BOLD+this.name + spacer + ammo;
		//if (i.isSimilar(Bukkit.getPlayer(this.hname).getItemInHand())) {
		if (i.isSimilar(Bukkit.getPlayer(this.hname).getInventory().getItemInMainHand())) {
			//this.holder.setItemInHand(plugin.wu.rename(plugin.wu.setLore(this.gunIte, this.lore), display));
			this.holder.getInventory().setItemInMainHand(plugin.wu.rename(plugin.wu.setLore(this.gunIte, this.lore), display));
		} else {
			this.holder.getInventory().setItem(getGunInstance(i), plugin.wu.rename(plugin.wu.setLore(this.gunIte, this.lore), display));
		}
		this.holder.updateInventory();
		return plugin.wu.rename(plugin.wu.setLore(this.gunIte, this.lore), display);
	}

	private int getGunInstance(ItemStack g) {
		for (int i = 0; i <= 35; i++) {
			ItemStack is = this.holder.getInventory().getItem(i);
			if (is != null) {
				if (is.isSimilar(g)) {
					return i;
				}
			}
		}
		return 0;
	}

	private ItemStack getItem(String istr) {
		String[] split = istr.split(",");
		//int id = Integer.parseInt(split[0]);	//F451-07222018
		String id = split[0];		
		byte data = 0;
		if (split.length == 2) {
			data = Byte.parseByte(split[1]);
		}
		Material mat = Material.matchMaterial(id);	//F451-07222018
		return new ItemStack(mat, 1, data);
	}

	public ItemStack getGunItem() {
		return this.gunIte;
	}

	public ItemStack getAmmoItem() {
		return this.ammoIte;
	}

	public int getVelocity() {
		return this.velocity;
	}

	public boolean willExplode() {
		return this.explode;
	}

	public int getDamage() {
		return this.damage;
	}

	public int getHeadshotBonus() {
		return this.headBonus;
	}

	public int getMagazineSize() {
		return this.magSize;
	}

	public int getReloadTime() {
		return this.reloadTime;
	}

	public long getShotDelay() {
		return this.shotDelay;
	}

	public boolean hasScope() {
		return this.scope;
	}

	public int getZoom() {
		return this.zoom;
	}

	public String getName() {
		return this.name;
	}

	public Player getHolder() {
		return this.holder;
	}

	public String getHolderName() {
		return this.hname;
	}

	public int getShotsLeft() {
		return this.shotsLeft;
	}

	public boolean isReloading() {
		return this.reloading;
	}

	public int getExplosionRange() {
		return this.expRange;
	}

	public double getSpread() {
		return this.spread;
	}

	public double getRecoil() {
		return this.recoil;
	}

	public int getShots() {
		return this.shots;
	}

	public SoundType getSoundType() {
		return this.sound;
	}

	public String getBullet() {
		return this.bullet;
	}

	public int getRange() {
		return this.range;
	}

	public EffectType getHitEffectType() {
		return this.hitEffect;
	}

	public int getHitEffectData() {
		return this.hitEffectData;
	}

	public EffectType getShootEffectType() {
		return this.shootEffect;
	}

	public int getShootEffectData() {
		return this.shootEffectData;
	}

	public boolean hasBoltAction() {
		return this.boltAction;
	}

	public boolean isPerformingBoltAction() {
		return this.boltActionPerform;
	}

	public boolean hasBursts() {
		return this.burst;
	}

	public int getBurstShots() {
		return this.burstShots;
	}

	public int getBurstShotDelay() {
		return this.delayBetweenBursts;
	}

	public int getExplosionDamage() {
		return this.expDamage;
	}

	public boolean hasSelfImmunity() {
		return this.selfImmunity;
	}
}

