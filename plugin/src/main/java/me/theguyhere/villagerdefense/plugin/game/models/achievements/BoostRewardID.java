package me.theguyhere.villagerdefense.plugin.game.models.achievements;

public enum BoostRewardID {
    DAMAGE_REDUCTION(0),
    DAMAGE_INCREASE(1),
    HEALTH_INCREASE(2),
    GEM_INCREASE(3),
    CRYSTAL_CONVERT(4),
    RESURRECTION(5),
    COOLDOWN_REDUCTION(6),
    SHARE_EFFECT(7),
    TWO_KITS(8)
    ;

    private final int ID;

    BoostRewardID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
}
