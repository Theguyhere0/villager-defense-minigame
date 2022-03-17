package me.theguyhere.villagerdefense.plugin.game.displays;

import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A villager NPC.
 */
public class NPCVillager {
    /** The location of the NPCVillager.*/
    private final Location location;
    /** The villager entity used to create the NPCVillager.*/
    private final VillagerPacketEntity villagerPacketEntity;

    public NPCVillager(@NotNull Location location) throws InvalidLocationException {
        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        // Set location and packet entity
        this.location = location;
        villagerPacketEntity = NMSVersion.getCurrent().getNmsManager().newVillagerPacketEntity();
    }

    public Location getLocation() {
        return location;
    }

    public int getEntityID() {
        return villagerPacketEntity.getEntityID();
    }

    /**
     * Spawn in the NPCVillager for every online player.
     */
    public void displayForOnline() {
        PlayerManager.sendLocationPacketToOnline(villagerPacketEntity.newSpawnPackets(location), location.getWorld());
    }

    /**
     * Spawn in the NPCVillager for a specific player.
     * @param player - The player to display the NPCVillager for.
     */
    public void displayForPlayer(Player player) {
        // Only display if player is in the same world
        if (player.getWorld().equals(location.getWorld()))
            villagerPacketEntity.newSpawnPackets(location).sendTo(player);
    }

    /**
     * Stop displaying the NPCVillager for every online player.
     */
    public void remove() {
        PlayerManager.sendPacketToOnline(villagerPacketEntity.newDestroyPackets());
    }
}
