package com.DarkBlade12.ModernWeapons.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.DarkBlade12.ModernWeapons.ModernWeapons;
import com.DarkBlade12.ModernWeapons.Weapons.Grenade;
import com.DarkBlade12.ModernWeapons.Weapons.Gun;

public class ModernWeaponsCE_mw implements CommandExecutor {
	ModernWeapons plugin;

	public ModernWeaponsCE_mw(ModernWeapons ModernWeapons) {
		plugin = ModernWeapons;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mw")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Invalid usage!" + "\n" + ChatColor.GOLD + "/mw help");
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Invalid usage!" + "\n" + ChatColor.GOLD + "/mw reload");
					return true;
				}
				if (!sender.hasPermission("ModernWeapons.reload")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission for this command!");
					return true;
				}
				plugin.reloadConfig();
				plugin.initializeStuff();
				if (sender instanceof Player) {
					sender.sendMessage(ChatColor.GOLD+""+ChatColor.ITALIC+"[ModernWeapons] Config has been reloaded.");
					return true;
				} else {
					sender.sendMessage("CONSOLE: "+ChatColor.GOLD+"ModernWeapons config reloaded.");
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.hasPermission("McWeapons.reload")) {
							p.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"[CONSOLE: "+ChatColor.GOLD+ChatColor.ITALIC+"ModernWeapons config has been reloaded."+ChatColor.GRAY+ChatColor.ITALIC+"]");
						}
					}
					return true;
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Invalid usage!" + "\n" + ChatColor.GOLD + "/mw list");
					return true;
				}
				if (!sender.hasPermission("ModernWeapons.list")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission for this command!");
					return true;
				}
				sender.sendMessage(plugin.prefix + ChatColor.BLUE +"List of weapons: \n "+ChatColor.DARK_RED+"\u2022 "+ChatColor.RED+ChatColor.ITALIC+"Guns:"+ChatColor.RESET + plugin.wu.getGunList() + "\n "+ChatColor.DARK_RED+"\u2022 "+ChatColor.GOLD+ChatColor.ITALIC+"Grenades:"+ChatColor.RESET + plugin.wu.getGrenadeList());
				return true;
			} else if (args[0].equalsIgnoreCase("info")) {
				if (args.length != 2) {
					sender.sendMessage(ChatColor.RED + "Invalid usage!" + "\n" + ChatColor.GOLD + "/mw info <weapon>");
					return true;
				}
				if (!sender.hasPermission("ModernWeapons.info")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission for this command!");
					return true;
				}
				String weapon = plugin.wu.getWeaponByName(args[1]);
				if (weapon == null) {
					sender.sendMessage(plugin.prefix + ChatColor.RED + "That weapon doesn't exist!");
					return true;
				}
				sender.sendMessage(plugin.prefix + ChatColor.BLUE + "Detailed information about " + ChatColor.AQUA + weapon + ChatColor.BLUE + ":" + plugin.wu.getWeaponInformations(weapon));
				return true;
			} else if (args[0].equalsIgnoreCase("give")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Command can't be run as console!");
					return true;
				}
				Player p = (Player) sender;
				if (args.length < 2) {
					p.sendMessage(ChatColor.RED + "Invalid usage!" + "\n" + ChatColor.GOLD + "/mw give <weapon>");
					return true;
				}
				if (!sender.hasPermission("ModernWeapons.give")) {
					p.sendMessage(ChatColor.RED + "You don't have permission for this command!");
					return true;
				}
				String wstr = "";
				for (int i = 1; i <= args.length - 1; i++) {
					if (wstr.length() == 0) {
						wstr += args[i];
					} else {
						wstr += " " + args[i];
					}
				}
				String weapon = plugin.wu.getWeaponByName(wstr);
				if (weapon == null) {
					if (wstr.equalsIgnoreCase("Knife")) {
						if (!plugin.wu.hasEnoughSpace(p)) {
							p.sendMessage(plugin.prefix + ChatColor.RED + "You don't have enough space!");
							return true;
						}
						p.getInventory().addItem(plugin.wu.rename(plugin.knifeIte, ChatColor.AQUA+""+ChatColor.ITALIC+"Knife"));
						p.sendMessage(plugin.prefix + ChatColor.YELLOW + "Here's your knife!");
						return true;
					}
					p.sendMessage(plugin.prefix + ChatColor.RED + "That weapon doesn't exist!");
					return true;
				}
				if (!plugin.wu.hasEnoughSpace(p)) {
					p.sendMessage(plugin.prefix + ChatColor.RED + "You don't have enough space!");
					return true;
				}
				if (plugin.wu.isGun(weapon)) {
					Gun g = new Gun(weapon, p, plugin, null);
					p.getInventory().addItem(g.getGunItem());
					g.refreshItem(g.getGunItem());
				} else {
					Grenade gr = new Grenade(weapon, p, plugin);
					p.getInventory().addItem(gr.getGrenadeItem());
					gr.refreshItem();
				}
				p.sendMessage(plugin.prefix + ChatColor.YELLOW + "Here's your weapon supply!");
				return true;
			} else if (args[0].equalsIgnoreCase("ammo")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Command can't be run as console!");
					return true;
				}
				Player p = (Player) sender;
				if (args.length < 2) {
					p.sendMessage(ChatColor.RED + "Invalid usage!" + "\n" + ChatColor.GOLD + "/mw ammo <weapon>");
					return true;
				}
				if (!sender.hasPermission("ModernWeapons.ammo")) {
					p.sendMessage(ChatColor.RED + "You don't have permission for this command!");
					return true;
				}
				String wstr = "";
				for (int i = 1; i <= args.length - 1; i++) {
					if (wstr.length() == 0) {
						wstr += args[i];
					} else {
						wstr += " " + args[i];
					}
				}
				String weapon = plugin.wu.getWeaponByName(wstr);
				if (weapon == null) {
					p.sendMessage(plugin.prefix + ChatColor.RED + "That weapon doesn't exist!");
					return true;
				}
				if (!plugin.wu.isGun(weapon)) {
					p.sendMessage(plugin.prefix + ChatColor.RED + "Grenades don't have ammo!");
					return true;
				}
				if (!plugin.wu.hasEnoughSpace(p)) {
					p.sendMessage(plugin.prefix + ChatColor.RED + "You don't have enough space!");
					return true;
				}
				Gun g = new Gun(weapon, p, plugin, null);
				ItemStack ammo = g.getAmmoItem();
				ammo.setAmount(64);
				List<String> lore = new ArrayList<String>();
				lore.add("Ammo for "+g.getName());
				ItemMeta im = ammo.getItemMeta();
					im.setDisplayName("Ammo");
					im.setLore(lore);
				ammo.setItemMeta(im);
				p.getInventory().addItem(ammo);
				p.sendMessage(plugin.prefix + ChatColor.YELLOW + "Here's your ammo supply!");
				return true;
			}
		}
		return false;
	}
}
