package me.theguyhere.villagerdefense.plugin.background.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.theguyhere.villagerdefense.plugin.Main;

public class ServerboundSignUpdatePacketListener extends PacketAdapter {
	public ServerboundSignUpdatePacketListener() {
		super(Main.plugin, PacketType.Play.Client.UPDATE_SIGN);
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		PacketContainer packetContainer = event.getPacket();
		String[] lines = packetContainer.getStringArrays().readSafely(0);

		PacketManager.onSignUpdate(event.getPlayer(), lines);
	}
}
