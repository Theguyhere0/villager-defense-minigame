package me.theguyhere.villagerdefense.game.displays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.tools.Utils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

/**
 * A class managing data about a Villager Defense portal to an arena.
 */
public class Portal {
    private EntityVillager NPC;
    private Hologram hologram;

    public Portal(EntityVillager NPC, Hologram hologram) {
        this.NPC = NPC;
        addNPCAll();
        this.hologram = hologram;
    }

    public void setNPC(EntityVillager NPC) {
        removeNPCAll();
        this.NPC = NPC;
        addNPCAll();
    }

    public void setHologram(Hologram hologram) {
        if (this.hologram != null)
            this.hologram.delete();
        this.hologram = hologram;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public EntityVillager getNPC() {
        return NPC;
    }

    /**
     * Adds the NPC to a player's client
     * @param player Player to add NPC to
     */
    public void addNPC(Player player) {
        if (NPC != null && NPC.getWorld().getWorld().equals(player.getWorld())) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutSpawnEntityLiving(NPC));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(NPC, (byte) (NPC.yaw * 256 / 360)));
        }
    }

    /**
     * Add an NPC to every online player's client
     */
    public void addNPCAll() {
        for (Player player : Bukkit.getOnlinePlayers())
            addNPC(player);
    }

    /**
     * Removes the NPC from a player's client
     * @param player Player to remove NPC from
     */
    public void removeNPC(Player player) {
        if (NPC != null) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityDestroy(NPC.getId()));
        }
    }

    /**
     * Removes the NPC from every online player's client
     */
    public void removeNPCAll() {
        for (Player player : Bukkit.getOnlinePlayers())
            removeNPC(player);
    }

    public static void refreshPortals() {
        Arrays.stream(Game.arenas).filter(Objects::nonNull).forEach(Arena::refreshPortal);
    }

    public static void addJoinPacket(Player player) {
        Arrays.stream(Game.arenas).filter(Objects::nonNull).map(Arena::getPortal).filter(Objects::nonNull)
                .forEach(portal -> portal.addNPC(player));
    }

    public static void removePortals() {
        Arrays.stream(Game.arenas).filter(Objects::nonNull).map(Arena::getPortal).filter(Objects::nonNull)
                .forEach(Portal::removeNPCAll);
    }

    /**
     * Formats a string array of text based on the Arena given.
     * @param arena The arena this text will be for.
     * @return The properly formatted string array of text.
     */
    public static String[] getHoloText(Arena arena) {
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
        else if (arena.getStatus() == ArenaStatus.ENDING)
            status = "&c&lEnding";
        else if (arena.getStatus() == ArenaStatus.WAITING)
            status = "&5&lWaiting";
        else status = "&a&lWave: " + arena.getCurrentWave();

        // Get player count color
        String countColor;
        double fillRatio = arena.getActiveCount() / (double) arena.getMaxPlayers();
        if (fillRatio < .8)
            countColor = "&a";
        else if (fillRatio < 1)
            countColor = "&6";
        else countColor = "&c";

        // Return full hologram text
        return new String[]{Utils.format("&6&l" + arena.getName() + difficulty),
                Utils.format("&bPlayers: " + countColor + arena.getActiveCount() + "&b/ " + arena.getMaxPlayers()),
                Utils.format("Spectators: " + arena.getSpectatorCount()),
                Utils.format(status)};
    }
}
