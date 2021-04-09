package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.ReloadBoardsEvent;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;

public class Mobs {
    private static void setMinion(Main plugin, Arena arena, LivingEntity livingEntity) {
        livingEntity.setCustomName(Utils.healthBar(1, 1, 5));
        livingEntity.setCustomNameVisible(true);
        livingEntity.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
        Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
    }

    public static void setVillager(Main plugin, Arena arena, Villager villager) {
        arena.incrementVillagers();
        setMinion(plugin, arena, villager);
    }

    public static void setZombie(Main plugin, Arena arena, Zombie zombie) {
        arena.incrementEnemies();
        setMinion(plugin, arena, zombie);
    }
}
