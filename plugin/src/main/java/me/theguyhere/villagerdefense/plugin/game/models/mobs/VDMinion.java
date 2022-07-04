package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public abstract class VDMinion extends VDMob {
    protected LivingEntity minion;

    protected VDMinion(Arena arena, LivingEntity minion, String name, String lore, int level, AttackType attackType) {
        super(lore, level, attackType);
        this.minion = minion;
        id = minion.getUniqueId();
        hostile = true;
        Main.getMonstersTeam().addEntry(minion.getUniqueId().toString());
        minion.setMetadata(VD, new FixedMetadataValue(Main.plugin, arena.getId()));
        gameID = arena.getGameID();
        wave = arena.getCurrentWave();
        this.name = name;
        hpBarSize = 2;
        minion.setRemoveWhenFarAway(false);
        minion.setCanPickupItems(false);
        if (minion.isInsideVehicle())
            Objects.requireNonNull(minion.getVehicle()).remove();
        for (Entity passenger : minion.getPassengers())
            passenger.remove();
        minion.setHealth(1);
        minion.setCustomNameVisible(true);
    }

    @Override
    protected void updateNameTag() {
        int healthLength = Integer.toString(currentHealth).length();
        int trueSize = hpBarSize * 4 + healthLength;
        int bars = (int) ((double) currentHealth / maxHealth * trueSize);
        StringBuilder healthIndicator = new StringBuilder(new String(new char[bars])
                .replace("\0", "\u258c"))
                .append(new String(new char[trueSize - bars]).replace("\0", " "));
        healthIndicator.replace(hpBarSize * 2, hpBarSize * 2 + healthLength, "&b" + currentHealth + "&c");
        getEntity().setCustomName(CommunicationManager.format(
                new ColoredMessage(ChatColor.RED, LanguageManager.messages.mobName),
                new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
                new ColoredMessage(ChatColor.RED, name),
                new ColoredMessage(ChatColor.RESET, CommunicationManager.format(
                        String.format("&7[&c%s&7]", healthIndicator)))
        ));
    }
}
