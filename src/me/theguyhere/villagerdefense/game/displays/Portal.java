package me.theguyhere.villagerdefense.game.displays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.tools.Utils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Portal {
	private final Main plugin;

	public Portal(Main plugin) {
		this.plugin = plugin;
	}

	// Lists to store NPCs and holograms
	private final EntityVillager[] NPC = new EntityVillager[45];
	private final Hologram[] holos = new Hologram[45];

	public void createPortal(Player player, int num, Game game) {
		Arena arena = game.arenas.get(num);

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
		addHolo(player.getLocation(), num, getHoloText(arena));

		// Save data about the NPC
		Utils.setConfigurationLocation(plugin, "portal." + num, player.getLocation());
		plugin.saveArenaData();
	}

	public void loadPortal(int arena, Game game) {
		Arena arenaInstance = game.arenas.get(arena);
		WorldServer world;

		// Create portal NPC
		try {
			world = ((CraftWorld) Bukkit.getWorld(arenaInstance.getPortal().getWorld().getName()))
					.getHandle();
		} catch (NullPointerException e) {
			plugin.debugError("Invalid location for arena number " + arena, 1);
			plugin.debugInfo("Portal location data may be corrupt. If data cannot be manually corrected in " +
					"arenaData.yml, please delete the portal location data for arena " + arena + ".", 1);
			return;
		}
		EntityVillager npc = new EntityVillager(EntityTypes.VILLAGER, world);
		npc.setLocation(arenaInstance.getPortal().getX(), arenaInstance.getPortal().getY(),
				arenaInstance.getPortal().getZ(), arenaInstance.getPortal().getYaw(),
				arenaInstance.getPortal().getPitch());

		// Add packets to all players
		addNPCAll(npc);
		NPC[arena] = npc;

		// Create hologram
		addHolo(arenaInstance.getPortal(), arena, getHoloText(arenaInstance));
	}

	public void removePortalAll(int arena) {
		removeNPCAll(arena);
		NPC[arena] = null;
		if (holos[arena] != null)
			holos[arena].delete();
	}

	public void refreshPortal(int arena, Game game) {
		removePortalAll(arena);
		loadPortal(arena, game);
	}

	private void addNPC(Player player, EntityVillager npc) {
		if (npc != null && npc.getWorld().getWorld().equals(player.getWorld())) {
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
		Arena arenaInstance = game.arenas.get(arena);
		if (holos[arena] != null)
			holos[arena].delete();
		Location location = Utils.getConfigLocationNoPitch(plugin, "portal." + arena);
		if (location != null)
			addHolo(location, arena, getHoloText(arenaInstance));
	}

	public void removeAll() {
		for (int i = 0; i < 45; i++)
			removeNPCAll(i);
		for (Hologram holo : holos)
			if (holo != null)
				holo.delete();
	}

	private String[] getHoloText(Arena arena) {
		String status;

		// Get difficulty
		String difficulty = arena.getDifficultyLabel();
		if (difficulty != null)
			switch (difficulty) {
				case "Easy":
					difficulty = " &a&l[" + difficulty + "]";
					break;
				case "Medium":
					difficulty = " &e&l[" + difficulty + "]";
					break;
				case "Hard":
					difficulty = " &c&l[" + difficulty + "]";
					break;
				case "Insane":
					difficulty = " &d&l[" + difficulty + "]";
					break;
				default:
					difficulty = "";
			}
		else difficulty = "";

		// Get status
		if (arena.isClosed()) {
			return new String[]{Utils.format("&6&l" + arena.getName() + difficulty),
					Utils.format("&4&lClosed")};
		}
		else if (arena.isEnding())
			status = "&c&lEnding";
		else if (!arena.isActive())
			status = "&5&lWaiting";
		else status = "&a&lWave: " + arena.getCurrentWave();

		return new String[]{Utils.format("&6&l" + arena.getName() + difficulty),
		Utils.format("&bPlayers: " + arena.getActiveCount() + '/' + arena.getMaxPlayers()),
		Utils.format("Spectators: " + arena.getSpectatorCount()),
		Utils.format(status)};
	}
}
