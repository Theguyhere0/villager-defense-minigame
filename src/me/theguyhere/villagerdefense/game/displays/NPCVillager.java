package me.theguyhere.villagerdefense.game.displays;

import me.theguyhere.villagerdefense.exceptions.EntitySpawnPacketException;
import me.theguyhere.villagerdefense.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.nms.NMSManager;
import me.theguyhere.villagerdefense.packets.PacketManager;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A villager NPC.
 */
public class NPCVillager {
    /** The location of the NPCVillager.*/
    private final Location location;
    /** The villager entity used to create the NPCVillager.*/
    private final Entity villager;

    public NPCVillager(@NotNull Location location) throws InvalidLocationException {
        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        // Set location and villager
        this.location = location;
        villager = NMSManager.getVillager(location);
    }

    public Location getLocation() {
        return location;
    }

    public Entity getVillager() {
        return villager;
    }

    /**
     * Spawn in the NPCVillager for every online player.
     */
    public void displayForOnline() {
        try {
            PacketManager.spawnEntityLivingForOnline(villager);
            PacketManager.entityHeadRotationForOnline(villager);
        } catch (EntitySpawnPacketException e) {
            Utils.debugError(e.getMessage(), 1);
        }
    }

    /**
     * Spawn in the NPCVillager for a specific player.
     * @param player - The player to display the NPCVillager for.
     */
    public void displayForPlayer(Player player) {
        try {
            PacketManager.spawnEntityLivingForPlayer(villager, player);
            PacketManager.entityHeadRotationForPlayer(villager, player);
        } catch (EntitySpawnPacketException e) {
            Utils.debugError(e.getMessage(), 1);
        }
    }

    /**
     * Stop displaying the NPCVillager for every online player.
     */
    public void remove() {
        PacketManager.destroyEntityForOnline(villager);
    }
}
