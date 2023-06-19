package me.theguyhere.villagerdefense.plugin.guis;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryButtons {
	// "No" button
	@NotNull
	public static ItemStack no() {
		return new ItemStackBuilder(Material.RED_CONCRETE, CommunicationManager.format("&c&lNO")).build();
	}

	// "Yes" button
	@NotNull
	public static ItemStack yes() {
		return new ItemStackBuilder(Material.LIME_CONCRETE, CommunicationManager.format("&a&lYES")).build();
	}

	// "Exit" button
	@NotNull
	public static ItemStack exit() {
		return new ItemStackBuilder(Material.BARRIER, CommunicationManager.format("&c&l" +
			LanguageManager.messages.exit)).build();
	}

	// "Remove x" button
	@NotNull
	public static ItemStack remove(String x) {
		return new ItemStackBuilder(Material.LAVA_BUCKET, CommunicationManager.format("&4&lREMOVE " + x)).build();
	}

	// "Create x" button
	@NotNull
	public static ItemStack create(String x) {
		return new ItemStackBuilder(Material.END_PORTAL_FRAME, CommunicationManager.format("&a&lCreate " + x)).build();
	}

	// "Relocate x" button
	@NotNull
	public static ItemStack relocate(String x) {
		return new ItemStackBuilder(Material.END_PORTAL_FRAME, CommunicationManager.format(
			"&a&lRelocate " + x)).build();
	}

	// "Teleport to x" button
	@NotNull
	public static ItemStack teleport(String x) {
		return new ItemStackBuilder(Material.ENDER_PEARL, CommunicationManager.format("&9&lTeleport to " + x)).build();
	}

	// "Center x" button
	@NotNull
	public static ItemStack center(String x) {
		return new ItemStackBuilder(
			Material.TARGET,
			CommunicationManager.format("&f&lCenter " + x)
		)
			.setLores(
				new ColoredMessage(ChatColor.GRAY, "Center the x and z coordinates").toString()
			)
			.build();
	}

	// "New x" button
	@NotNull
	public static ItemStack newAdd(String x) {
		return new ItemStackBuilder(Material.NETHER_STAR, CommunicationManager.format("&a&lNew " + x)).build();
	}

	// "Previous Page" button
	@NotNull
	public static ItemStack previousPage() {
		return new ItemStackBuilder(Material.PRISMARINE_SHARD, CommunicationManager.format("&e&lPrevious Page")).build();
	}

	// "Next Page" button
	@NotNull
	public static ItemStack nextPage() {
		return new ItemStackBuilder(Material.FEATHER, CommunicationManager.format("&d&lNext Page")).build();
	}

	// "No upgrade" button
	@NotNull
	public static ItemStack noUpgrade() {
		return new ItemStackBuilder(
			Material.RED_STAINED_GLASS_PANE,
			new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.noUpgrades).toString()
		)
			.setButtonFlags()
			.build();
	}
}
