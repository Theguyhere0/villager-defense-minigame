package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import net.minecraft.world.entity.monster.Zombie;

public class VDZombieAttackGoal extends VDMeleeAttackGoal {
	private final Zombie zombie;
	private int raiseArmTicks;

	public VDZombieAttackGoal(Zombie zombie, int attackInterval, double attackReach) {
		super(zombie, false, attackInterval, attackReach);
		this.zombie = zombie;
	}

	@Override
	public void start() {
		super.start();
		raiseArmTicks = 0;
	}

	@Override
	public void stop() {
		super.stop();
		zombie.setAggressive(false);
	}

	@Override
	public void tick() {
		super.tick();
		++raiseArmTicks;
		zombie.setAggressive(raiseArmTicks >= 5 && getTicksUntilNextAttack() < getAttackInterval() / 2);
	}
}
