package me.theguyhere.villagerdefense.plugin.background.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import me.theguyhere.villagerdefense.plugin.Main;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ClientboundSetEquipmentPacketListener extends PacketAdapter {
	public ClientboundSetEquipmentPacketListener() {
		super(Main.plugin, PacketType.Play.Server.ENTITY_EQUIPMENT);
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		// Get equipment list
		List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList =
			event.getPacket().getSlotStackPairLists().readSafely(0);
		List<Pair<EnumWrappers.ItemSlot, ItemStack>> newPairList = new ArrayList<>();

		// Remove any invisible items
		pairList.forEach(pair -> {
			if (PacketManager.checkInvisible(pair.getSecond()))
				newPairList.add(new Pair<>(pair.getFirst(), null));
			else newPairList.add(new Pair<>(pair.getFirst(), pair.getSecond()));
		});

		// Modify equipment list
		event.getPacket().getSlotStackPairLists().writeSafely(0, newPairList);
	}
}
