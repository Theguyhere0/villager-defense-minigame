package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;

public abstract class VDVillager extends VDMob {
    protected Villager villager;

    protected VDVillager(Arena arena, Villager villager, String name, String lore, int level) {
        super(lore, level, null);
        this.villager = villager;
        id = villager.getUniqueId();
        hostile = false;
        villager.setAdult();
        Main.getVillagersTeam().addEntry(villager.getUniqueId().toString());
        villager.setMetadata(VD, new FixedMetadataValue(Main.plugin, arena.getId()));
        gameID = arena.getGameID();
        wave = arena.getCurrentWave();
        this.name = name;
        hpBarSize = 2;
        villager.setRemoveWhenFarAway(false);
        villager.setHealth(1);
        villager.setCustomNameVisible(true);
        villager.setVillagerLevel(1);
    }

    @Override
    protected void updateNameTag() {
        int healthLength = Integer.toString(currentHealth).length();
        int trueSize = hpBarSize * 4 + healthLength;
        int bars = (int) ((double) currentHealth / maxHealth * trueSize);
        StringBuilder healthIndicator = new StringBuilder(new String(new char[bars])
                .replace("\0", "\u258c"))
                .append(new String(new char[trueSize - bars]).replace("\0", " "));
        healthIndicator.replace(hpBarSize * 2, hpBarSize * 2 + healthLength, "&b" + currentHealth + "&2");
        getEntity().setCustomName(CommunicationManager.format(
                new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.messages.mobName),
                new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
                new ColoredMessage(ChatColor.DARK_GREEN, name),
                new ColoredMessage(ChatColor.RESET, CommunicationManager.format(
                        String.format("&7[&2%s&7]", healthIndicator)))
        ));
    }

    @Override
    public LivingEntity getEntity() {
        return villager;
    }
}
