package me.theguyhere.villagerdefense.game;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.tools.Utils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Portal {
	private final Main plugin;
	private Arena arena;
	private final Utils utils;

	public Portal(Main plugin) {
		this.plugin = plugin;
		utils = new Utils(plugin);
	}

	// Lists to store NPCs and holograms
	private final EntityVillager[] NPC = new EntityVillager[45];
	private final Hologram[] holos = new Hologram[45];

	public void createPortal(Player player, int num, Game game) {
		this.arena = game.arenas.get(num);

		// Get NMS versions of world
		WorldServer nmsWorld = ((CraftWorld) player.getWorld()).getHandle();

		// Create portal NPC
		EntityVillager npc = new EntityVillager(EntityTypes.VILLAGER, nmsWorld);
		npc.setLocation(player.getLocation().getX(),
				player.getLocation().getY(),
				player.getLocation().getZ(),
				player.getLocation().getYaw(),
				0);

		// Add packets to all players
		addNPCAll(npc);
		NPC[num] = npc;

		// Create hologram
		addHolo(player.getLocation(), num, getHoloText());

		// Save data about the NPC
		utils.setConfigurationLocation("portal." + num, player.getLocation());
		plugin.saveArenaData();
	}

	public void loadPortal(Location location, int arena, Game game) {
		this.arena = game.arenas.get(arena);

		// Create portal NPC
		WorldServer world = ((CraftWorld) Bukkit.getWorld(location.getWorld().getName())).getHandle();
		EntityVillager npc = new EntityVillager(EntityTypes.VILLAGER, world);
		npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		// Add packets to all players
		addNPCAll(npc);
		NPC[arena] = npc;

		// Create hologram
		addHolo(location, arena, getHoloText());
	}

	public void removePortalAll(int arena) {
		removeNPCAll(arena);
		if (holos[arena] != null)
			holos[arena].delete();
	}
	
	private void addNPC(Player player, EntityVillager npc) {
		if (npc != null) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutSpawnEntityLiving(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
		}
	}

	private void addNPCAll(EntityVillager npc) {
		for (Player player : Bukkit.getOnlinePlayers())
			addNPC(player, npc);
	}
	
	private void removeNPC(Player player, EntityVillager npc) {
		if (npc != null) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
		}
	}

	private void removeNPCAll(int arena) {
		for (Player player : Bukkit.getOnlinePlayers())
			removeNPC(player, NPC[arena]);
	}
	
	public void addJoinPacket(Player player) {
		for (EntityVillager npc : NPC)
			addNPC(player, npc);
	}

	public EntityVillager[] getNPCs() {
		return NPC;
	}

	private void addHolo(Location location, int arena, String[] text) {
		// Create hologram
		Location newLocation = location.clone();
		newLocation.setY(newLocation.getY() + 3);
		Hologram holo = HologramsAPI.createHologram(plugin, newLocation);
		holo.insertTextLine(0, text[0]);
		for (int i = 1; i < text.length; i++)
			holo.appendTextLine(text[i]);

		// Save hologram in array
		holos[arena] = holo;
	}

	public void refreshHolo(int arena, Game game) {
		this.arena = game.arenas.get(arena);
		holos[arena].delete();
		Location location = utils.getConfigLocationNoPitch("portal." + arena);
		addHolo(location, arena, getHoloText());
	}

	public void removeAll() {
		for (int i = 0; i < 45; i++)
			removeNPCAll(i);
		for (Hologram holo : holos)
			if (holo != null)
				holo.delete();
	}

	private String[] getHoloText() {
		String status;
		if (arena.isClosed())
			status = "&4&lClosed";
		else if (arena.isEnding())
			status = "&c&lEnding";
		else if (!arena.isActive())
			status = "&5&lWaiting";
		else status = "&a&lWave: " + arena.getCurrentWave();
		return new String[]{Utils.format("&6&l" + arena.getName()),
		Utils.format("&bPlayers: " + arena.getActiveCount() + '/' + arena.getMaxPlayers()),
		Utils.format("&7Spectators: " + arena.getSpectatorCount()),
		Utils.format(status)};
	}
}
