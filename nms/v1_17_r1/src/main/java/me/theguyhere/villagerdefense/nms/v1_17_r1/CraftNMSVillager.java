package me.theguyhere.villagerdefense.nms.v1_17_r1;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftVillager;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Collection;

public class CraftNMSVillager extends CraftVillager {

    public CraftNMSVillager(EntityNMSVillager entity) {
        super((CraftServer) Bukkit.getServer(), entity);
        super.setInvulnerable(true);
        super.setCollidable(false);
    }

    // Disallow all the bukkit methods.

    @Override
    public void remove() {
        // Cannot be removed, this is the most important to override.
    }

    // Methods from Villager class
    @Override public void setProfession(Profession profession) { }
    @Override public void setVillagerType(Type type) { }
    @Override public void setVillagerLevel(int level) { }
    @Override public void setVillagerExperience(int experience) { }
    @Override public boolean sleep(Location location) { return false; }
    @Override public void wakeup() { }

    // Methods from LivingEntity class
    @Override public boolean addPotionEffect(PotionEffect effect) { return false; }
    @Override public boolean addPotionEffect(PotionEffect effect, boolean param) { return false; }
    @Override public boolean addPotionEffects(Collection<PotionEffect> effects) { return false; }
    @Override public void setRemoveWhenFarAway(boolean remove) { }
    @Override public void setAI(boolean ai) { }
    @Override public void setCanPickupItems(boolean pickup) { }
    @Override public void setCollidable(boolean collidable) { }
    @Override public void setGliding(boolean gliding) {	}
    @Override public boolean setLeashHolder(Entity holder) { return false; }
    @Override public void setSwimming(boolean swimming) { }

    // Methods from Entity class
    @Override public void setVelocity(Vector vel) { }
    @Override public boolean teleport(Location loc) { return false; }
    @Override public boolean teleport(Entity entity) { return false; }
    @Override public boolean teleport(Location loc, PlayerTeleportEvent.TeleportCause cause) { return false; }
    @Override public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause cause) { return false; }
    @Override public void setFireTicks(int ticks) { }
    @Override public boolean setPassenger(Entity entity) { return false; }
    @Override public boolean eject() { return false; }
    @Override public boolean leaveVehicle() { return false; }
    @Override public void playEffect(EntityEffect effect) { }
    @Override public void setCustomName(String name) { }
    @Override public void setCustomNameVisible(boolean flag) { }
    @Override public void setGlowing(boolean flag) { }
    @Override public void setGravity(boolean gravity) { }
    @Override public void setInvulnerable(boolean flag) { }
    @Override public void setMomentum(Vector value) { }
    @Override public void setSilent(boolean flag) { }
    @Override public void setTicksLived(int value) { }
    @Override public void setPersistent(boolean flag) { }
    @Override public void setRotation(float yaw, float pitch) { }
}
