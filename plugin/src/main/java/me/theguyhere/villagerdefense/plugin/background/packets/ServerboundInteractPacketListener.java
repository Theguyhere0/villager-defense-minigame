package me.theguyhere.villagerdefense.plugin.background.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.theguyhere.villagerdefense.plugin.Main;

import java.util.Objects;

public class ServerboundInteractPacketListener extends PacketAdapter {
	public ServerboundInteractPacketListener() {
		super(Main.plugin, PacketType.Play.Client.USE_ENTITY);
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		PacketContainer packetContainer = event.getPacket();
		int entityID = packetContainer.getIntegers().readSafely(0);
		WrappedEnumEntityUseAction type = packetContainer.getEnumEntityUseActions().readSafely(0);

		// Left click
		if (type.getAction() == EnumWrappers.EntityUseAction.ATTACK)
			PacketManager.onAttack(event.getPlayer(), entityID);

		// Main hand right click
		else if (type.getAction() == EnumWrappers.EntityUseAction.INTERACT &&
			type.getHand() == EnumWrappers.Hand.MAIN_HAND)
			PacketManager.onInteractMain(event.getPlayer(), entityID);
	}
}
