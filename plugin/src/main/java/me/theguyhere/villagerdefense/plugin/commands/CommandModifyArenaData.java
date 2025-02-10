package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.WrongFormatException;
import me.theguyhere.villagerdefense.plugin.data.YAMLManager;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.data.listeners.ChatListener;
import me.theguyhere.villagerdefense.plugin.game.Arena;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.exceptions.InvalidNameException;
import me.theguyhere.villagerdefense.plugin.visuals.Inventories;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

/**
 * Executes commands to modify arena data without the admin GUI.
 */
class CommandModifyArenaData {
	static final String CREATE = "create";
	static final String COMMAND_FORMAT = "/vd admin [arena, infoBoard, leaderboard, lobby] [extra arguments]";

	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.ADMIN.getArg()) ||
			GuardClause.checkArgsLengthLess(args, 2))
			return;
		GuardClause.checkSenderPermissions(sender, Permission.ADMIN);

		// Execute sub commands
		modifyLobby(args, sender);
		modifyInfoBoard(args, sender);
		modifyLeaderboard(args, sender);
		modifyArena(args, sender);

		// No valid commend sent
		if (Arrays
			.stream(Argument.values())
			.noneMatch(arg -> GuardClause.checkArg(args, 1, arg.getArg())))
			VDCommandExecutor.notifyFailure(sender, COMMAND_FORMAT);
	}

	enum Argument {
		LOBBY("lobby"),
		INFOBOARD("infoBoard"),
		LEADERBOARD("leaderboard"),
		ARENA("arena");
		private final String arg;

		Argument(String arg) {
			this.arg = arg;
		}

		String getArg() {
			return arg;
		}
	}

	enum LocationOptionArgument {
		SET("set"),
		TELEPORT("teleport"),
		CENTER("center"),
		REMOVE("remove");
		private final String arg;

		LocationOptionArgument(String arg) {
			this.arg = arg;
		}

		String getArg() {
			return arg;
		}
	}

	enum LeaderboardTypeArgument {
		TOP_BALANCE("topBalance"),
		TOP_KILLS("topKills"),
		TOP_WAVE("topWave"),
		TOTAL_GEMS("totalGems"),
		TOTAL_KILLS("totalKills");
		private final String arg;

		LeaderboardTypeArgument(String arg) {
			this.arg = arg;
		}

		String getArg() {
			return arg;
		}
	}

	enum ArenaOperationArgument {
		CLOSE("close"),
		OPEN("open"),
		RENAME("rename"),
		PORTAL("portal-"),
		REMOVE("remove"),
		LEADERBOARD("leaderboard-"),
		PLAYER_SPAWN("playerSpawn-"),
		WAITING_ROOM("waitingRoom-"),
		SPAWN_PARTICLES("spawnParticles-"),
		MAX_PLAYERS("maxPlayers-"),
		MIN_PLAYERS("minPlayers-"),
		MONSTER_SPAWN_PARTICLES("monsterSpawnParticles-"),
		VILLAGER_SPAWN_PARTICLES("villagerSpawnParticles-"),
		DYNAMIC_MOB_COUNT("dynamicMobCount-"),
		DEFAULT_SHOP("defaultShop-"),
		CUSTOM_SHOP("customShop-"),
		ENCHANT_SHOP("enchantShop-"),
		COMMUNITY_CHEST("communityChest-"),
		DYNAMIC_PRICES("dynamicPrices-"),
		DYNAMIC_TIME_LIMIT("dynamicTimeLimit-"),
		DYNAMIC_DIFFICULTY("dynamicDifficulty-"),
		LATE_ARRIVAL("lateArrival-"),
		EXPERIENCE_DROP("experienceDrop-"),
		ITEM_DROP("itemDrop-"),
		MAX_WAVES("maxWaves-"),
		WAVE_TIME_LIMIT("waveTimeLimit-"),
		WOLF_CAP("wolfCap-"),
		GOLEM_CAP("golemCap-"),
		DIFFICULTY_LABEL("difficultyLabel-"),
		DIFFICULTY_MULTIPLIER("difficultyMultiplier");
		private final String arg;

		ArenaOperationArgument(String arg) {
			this.arg = arg;
		}

		String getArg() {
			return arg;
		}
	}

	private enum ToggleArgument {
		ON("on"),
		OFF("off");
		private final String arg;

		ToggleArgument(String arg) {
			this.arg = arg;
		}

		private String getArg() {
			return arg;
		}
	}

	private enum DifficultyLabelArgument {
		EASY("easy"),
		MEDIUM("medium"),
		HARD("hard"),
		INSANE("insane"),
		NONE("none");
		private final String arg;

		DifficultyLabelArgument(String arg) {
			this.arg = arg;
		}

		private String getArg() {
			return arg;
		}
	}

	private static void modifyLobby(String[] args, CommandSender sender) throws CommandException {
		final String COMMAND_FORMAT = "/vd admin lobby [center, remove, set, teleport]";

		// Guard clauses
		if (!GuardClause.checkArg(args, 1, Argument.LOBBY.arg))
			return;
		if (!GuardClause.checkArgsLengthMatch(args, 3))
			throw new WrongFormatException(COMMAND_FORMAT);

		Player player;
		String path = "lobby";
		Location location = YAMLManager.getConfigLocationNoRotation(path);

		if (GuardClause.checkArg(args, 2, LocationOptionArgument.SET.arg)) {
			player = GuardClause.checkSenderPlayer(sender);

			GameManager.saveLobby(player.getLocation());
			PlayerManager.notifySuccess(player, "Lobby set!");
		}
		else if (GuardClause.checkArg(args, 2, LocationOptionArgument.TELEPORT.arg)) {
			player = GuardClause.checkSenderPlayer(sender);
			if (location == null) {
				PlayerManager.notifyFailure(player, "No lobby to teleport to!");
				return;
			}

			player.teleport(location);
		}
		else if (GuardClause.checkArg(args, 2, LocationOptionArgument.CENTER.arg)) {
			if (location == null) {
				VDCommandExecutor.notifyFailure(sender, "No lobby to center!");
				return;
			}

			YAMLManager.centerConfigLocation(path);
			VDCommandExecutor.notifySuccess(sender, "Lobby centered!");
		}
		else if (GuardClause.checkArg(args, 2, LocationOptionArgument.REMOVE.arg)) {
			player = GuardClause.checkSenderPlayer(sender);

			if (GameManager
				.getArenas()
				.values()
				.stream()
				.filter(Objects::nonNull)
				.anyMatch(arenaInstance -> !arenaInstance.isClosed()))
				PlayerManager.notifyFailure(
					player,
					"All arenas must be closed to modify this!"
				);
			else if (Main
				.getArenaData()
				.contains("lobby"))
				player.openInventory(Inventories.createLobbyConfirmMenu());
			else PlayerManager.notifyFailure(player, "No lobby to remove!");
		}
		else throw new WrongFormatException(COMMAND_FORMAT);
	}

	private static void modifyInfoBoard(String[] args, CommandSender sender) throws CommandException {
		final String COMMAND_FORMAT = "/vd admin infoBoard [create, [info board id]] [center, remove, set, teleport]";

		// Guard clauses
		if (!GuardClause.checkArg(args, 1, Argument.INFOBOARD.arg))
			return;
		if (GuardClause.checkArgsLengthGreater(args, 4) ||
			GuardClause.checkArgsLengthLess(args, 3))
			throw new WrongFormatException(COMMAND_FORMAT);

		Player player;
		int infoBoardID;
		String path;
		Location location;

		if (GuardClause.checkArg(args, 2, CommandModifyArenaData.CREATE)) {
			player = GuardClause.checkSenderPlayer(sender);

			GameManager.setInfoBoard(player.getLocation(), GameManager.newInfoBoardID());
			PlayerManager.notifySuccess(player, "Info board set!");
			return;
		}

		// Get info board ID
		try {
			infoBoardID = Integer.parseInt(args[2]);
		}
		catch (Exception e) {
			throw new WrongFormatException(COMMAND_FORMAT);
		}

		// Check for valid info board ID, then set path and location
		if (!Main
			.getArenaData()
			.contains("infoBoard." + infoBoardID)) {
			VDCommandExecutor.notifyFailure(sender, "Invalid info board id.");
			return;
		}
		path = "infoBoard." + infoBoardID;
		location = YAMLManager.getConfigLocationNoRotation(path);

		if (GuardClause.checkArg(args, 3, LocationOptionArgument.SET.arg)) {
			player = GuardClause.checkSenderPlayer(sender);

			GameManager.setInfoBoard(player.getLocation(), infoBoardID);
			PlayerManager.notifySuccess(player, "Info board set!");
		}
		else if (GuardClause.checkArg(args, 3, LocationOptionArgument.TELEPORT.arg)) {
			player = GuardClause.checkSenderPlayer(sender);
			if (location == null) {
				PlayerManager.notifyFailure(player, "No info board to teleport to!");
				return;
			}

			player.teleport(location);
		}
		else if (GuardClause.checkArg(args, 3, LocationOptionArgument.CENTER.arg)) {
			if (location == null) {
				VDCommandExecutor.notifyFailure(sender, "No info board to center!");
				return;
			}

			YAMLManager.centerConfigLocation(path);
			VDCommandExecutor.notifySuccess(sender, "Info board centered!");
		}
		else if (GuardClause.checkArg(args, 3, LocationOptionArgument.REMOVE.arg)) {
			player = GuardClause.checkSenderPlayer(sender);

			if (Main
				.getArenaData()
				.contains(path))
				player.openInventory(Inventories.createInfoBoardConfirmMenu(infoBoardID));
			else PlayerManager.notifyFailure(player, "No info board to remove!");
		}
		else throw new WrongFormatException(COMMAND_FORMAT);
	}

	private static void modifyLeaderboard(String[] args, CommandSender sender) throws CommandException {
		final String COMMAND_FORMAT = "/vd admin leaderboard [leaderboard type] [center, remove, set, teleport]";

		// Guard clauses
		if (!GuardClause.checkArg(args, 1, Argument.LEADERBOARD.arg))
			return;
		if (!GuardClause.checkArgsLengthMatch(args, 4))
			throw new WrongFormatException(COMMAND_FORMAT);

		// Check for type validity
		if (Arrays
			.stream(LeaderboardTypeArgument.values())
			.noneMatch(type -> GuardClause.checkArg(args, 2, type.arg))) {
			VDCommandExecutor.notifyFailure(sender, "Invalid leaderboard.");
			return;
		}

		Player player;
		String type = args[2];
		String path = "leaderboard." + args[2];
		Location location = YAMLManager.getConfigLocationNoRotation(path);

		if (GuardClause.checkArg(args, 3, LocationOptionArgument.SET.arg)) {
			player = GuardClause.checkSenderPlayer(sender);

			GameManager.setLeaderboard(player.getLocation(), type);
			PlayerManager.notifySuccess(player, "Leaderboard set!");
		}
		else if (GuardClause.checkArg(args, 3, LocationOptionArgument.TELEPORT.arg)) {
			player = GuardClause.checkSenderPlayer(sender);
			if (location == null) {
				PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
				return;
			}

			player.teleport(location);
		}
		else if (GuardClause.checkArg(args, 3, LocationOptionArgument.CENTER.arg)) {
			if (location == null) {
				VDCommandExecutor.notifyFailure(sender, "No leaderboard to center!");
				return;
			}

			YAMLManager.centerConfigLocation(path);
			VDCommandExecutor.notifySuccess(sender, "Leaderboard centered!");
		}
		else if (GuardClause.checkArg(args, 3, LocationOptionArgument.REMOVE.arg)) {
			player = GuardClause.checkSenderPlayer(sender);


			if (Main
				.getArenaData()
				.contains(path)) {
				if (GuardClause.checkArg(args, 2, LeaderboardTypeArgument.TOP_BALANCE.arg))
					player.openInventory(Inventories.createTopBalanceConfirmMenu());
				else if (GuardClause.checkArg(args, 2, LeaderboardTypeArgument.TOP_KILLS.arg))
					player.openInventory(Inventories.createTopKillsConfirmMenu());
				else if (GuardClause.checkArg(args, 2, LeaderboardTypeArgument.TOP_WAVE.arg))
					player.openInventory(Inventories.createTopWaveConfirmMenu());
				else if (GuardClause.checkArg(args, 2, LeaderboardTypeArgument.TOTAL_GEMS.arg))
					player.openInventory(Inventories.createTotalGemsConfirmMenu());
				else if (GuardClause.checkArg(args, 2, LeaderboardTypeArgument.TOTAL_KILLS.arg))
					player.openInventory(Inventories.createTotalKillsConfirmMenu());
			}
			else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
		}
		else throw new WrongFormatException(COMMAND_FORMAT);
	}

	private static void modifyArena(String[] args, CommandSender sender) throws CommandException {
		final String COMMAND_FORMAT = "/vd admin arena [operation][-value] [arena name]";

		// Guard clauses
		if (!GuardClause.checkArg(args, 1, Argument.ARENA.arg))
			return;
		if (GuardClause.checkArgsLengthLess(args, 4))
			throw new WrongFormatException(COMMAND_FORMAT);

		// Get arena name
		StringBuilder name = new StringBuilder(args[3]);
		for (int i = 0; i < args.length - 4; i++)
			name
				.append(" ")
				.append(args[i + 4]);

		// Check if this arena exists
		Arena arena;
		try {
			arena = GameManager.getArena(name.toString());
		}
		catch (ArenaNotFoundException e) {
			VDCommandExecutor.notifyFailure(sender, LanguageManager.errors.noArena);
			return;
		}

		Player player;
		Location location;

		// Close or open arena, otherwise check if arena is closed
		if (GuardClause.checkArg(args, 2, ArenaOperationArgument.CLOSE.arg)) {
			// Check if arena is already closed
			if (arena.isClosed()) {
				VDCommandExecutor.notifyFailure(sender, "Arena is already closed!");
				return;
			}

			// Close arena
			arena.setClosed(true);

			// Notify console and possibly player
			VDCommandExecutor.notifySuccess(sender, arena.getName() + " was closed.");
		}
		else if (GuardClause.checkArg(args, 2, ArenaOperationArgument.OPEN.arg)) {
			// Check if arena is already open
			if (!arena.isClosed()) {
				VDCommandExecutor.notifyFailure(sender, "Arena is already open!");
				return;
			}

			// No lobby
			if (!Main
				.getArenaData()
				.contains("lobby")) {
				VDCommandExecutor.notifyFailure(sender, "Arena cannot open without a lobby!");
				return;
			}

			// No arena portal
			if (arena.getPortalLocation() == null) {
				VDCommandExecutor.notifyFailure(sender, "Arena cannot open without a portal!");
				return;
			}

			// No player spawn
			if (arena.getPlayerSpawn() == null) {
				VDCommandExecutor.notifyFailure(sender, "Arena cannot open without a player spawn!");
				return;
			}

			// No monster spawn
			if (arena
				.getMonsterSpawns()
				.isEmpty()) {
				VDCommandExecutor.notifyFailure(sender, "Arena cannot open without a monster spawn!");
				return;
			}

			// No villager spawn
			if (arena
				.getVillagerSpawns()
				.isEmpty()) {
				VDCommandExecutor.notifyFailure(sender, "Arena cannot open without a villager spawn!");
				return;
			}

			// Invalid arena bounds
			if (arena.getCorner1() == null || arena.getCorner2() == null ||
				!Objects.equals(arena
					.getCorner1()
					.getWorld(), arena
					.getCorner2()
					.getWorld())) {
				VDCommandExecutor.notifyFailure(sender, "Arena cannot open without valid arena bounds!");
				return;
			}

			// Outdated file configs
			if (Main.isOutdated()) {
				VDCommandExecutor.notifyFailure(
					sender,
					"Arena cannot open when file configurations are outdated!"
				);
				return;
			}

			// Open arena
			arena.setClosed(false);

			// Notify console and possibly player
			VDCommandExecutor.notifySuccess(sender, arena.getName() + " was opened.");
		}
		else if (!arena.isClosed()) {
			VDCommandExecutor.notifyFailure(sender, "Arena must be closed to modify this!");
		}

		// Other operations
		else if (GuardClause.checkArg(args, 2, ArenaOperationArgument.RENAME.arg)) {
			player = GuardClause.checkSenderPlayer(sender);

			// Prompt for new name
			ChatListener.ChatTask task = (msg) -> {
				// Check for cancelling
				if (msg.equalsIgnoreCase("cancel")) {
					PlayerManager.notifyAlert(player, "Arena naming cancelled.");
					return;
				}

				// Try updating name
				try {
					arena.setName(msg.trim());
					CommunicationManager.debugInfo("Name changed for arena %s!",
						CommunicationManager.DebugLevel.VERBOSE,
						arena
							.getPath()
							.substring(1)
					);
				}
				catch (InvalidNameException err) {
					if (arena.getName() == null)
						GameManager.removeArena(arena.getId());
					PlayerManager.notifyFailure(player, "Invalid arena name!");
				}
			};
			ChatListener.addTask(
				player, task, "Enter the new unique name for the arena chat, or type CANCEL to " +
					"quit:");
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.PORTAL.arg)) {
			location = arena.getPortalLocation();
			String value = args[2].substring(args[2].indexOf("-") + 1);
			if (LocationOptionArgument.SET.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);

				arena.setPortal(player.getLocation());
				PlayerManager.notifySuccess(player, "Portal set!");
			}
			else if (LocationOptionArgument.TELEPORT.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No portal to teleport to!");
					return;
				}

				player.teleport(location);
			}
			else if (LocationOptionArgument.CENTER.arg.equalsIgnoreCase(value)) {
				if (location == null) {
					VDCommandExecutor.notifyFailure(sender, "No portal to center!");
					return;
				}

				arena.centerPortal();
				VDCommandExecutor.notifySuccess(sender, "Portal centered!");
			}
			else if (LocationOptionArgument.REMOVE.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);

				if (arena.getPortal() != null)
					player.openInventory(Inventories.createPortalConfirmMenu(arena));
				else PlayerManager.notifyFailure(player, "No portal to remove!");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(LocationOptionArgument.values())
						.map(LocationOptionArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.LEADERBOARD.arg)) {
			location = arena.getArenaBoardLocation();
			String value = args[2].substring(args[2].indexOf("-") + 1);
			if (LocationOptionArgument.SET.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);

				arena.setArenaBoard(player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
			}
			else if (LocationOptionArgument.TELEPORT.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}

				player.teleport(location);
			}
			else if (LocationOptionArgument.CENTER.arg.equalsIgnoreCase(value)) {
				if (location == null) {
					VDCommandExecutor.notifyFailure(sender, "No leaderboard to center!");
					return;
				}

				arena.centerArenaBoard();
				VDCommandExecutor.notifySuccess(sender, "Leaderboard centered!");
			}
			else if (LocationOptionArgument.REMOVE.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);

				if (arena.getArenaBoard() != null)
					player.openInventory(Inventories.createArenaBoardConfirmMenu(arena));
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(LocationOptionArgument.values())
						.map(LocationOptionArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.PLAYER_SPAWN.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (LocationOptionArgument.SET.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);

				arena.setPlayerSpawn(player.getLocation());
				PlayerManager.notifySuccess(player, "Spawn set!");
			}
			else if (LocationOptionArgument.TELEPORT.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);
				try {
					player.teleport(arena
						.getPlayerSpawn()
						.getLocation());
				}
				catch (Exception e) {
					PlayerManager.notifyFailure(player, "No spawn to teleport to!");
				}
			}
			else if (LocationOptionArgument.CENTER.arg.equalsIgnoreCase(value)) {
				try {
					arena.centerPlayerSpawn();
					VDCommandExecutor.notifySuccess(sender, "Spawn centered!");
				}
				catch (Exception e) {
					VDCommandExecutor.notifyFailure(sender, "No spawn to center!");
				}
			}
			else if (LocationOptionArgument.REMOVE.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);

				if (arena.getPlayerSpawn() != null)
					player.openInventory(Inventories.createSpawnConfirmMenu(arena));
				else PlayerManager.notifyFailure(player, "No spawn to remove!");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(LocationOptionArgument.values())
						.map(LocationOptionArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.WAITING_ROOM.arg)) {
			location = arena.getWaitingRoom();
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (LocationOptionArgument.SET.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);

				arena.setWaitingRoom(player.getLocation());
				PlayerManager.notifySuccess(player, "Waiting room set!");
			}
			else if (LocationOptionArgument.TELEPORT.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No waiting room to teleport to!");
					return;
				}

				player.teleport(location);
			}
			else if (LocationOptionArgument.CENTER.arg.equalsIgnoreCase(value)) {
				if (location == null) {
					VDCommandExecutor.notifyFailure(sender, "No waiting room to center!");
					return;
				}

				arena.centerWaitingRoom();
				VDCommandExecutor.notifySuccess(sender, "Waiting room centered!");
			}
			else if (LocationOptionArgument.REMOVE.arg.equalsIgnoreCase(value)) {
				player = GuardClause.checkSenderPlayer(sender);

				if (arena.getWaitingRoom() != null)
					player.openInventory(Inventories.createWaitingConfirmMenu(arena));
				else PlayerManager.notifyFailure(player, "No waiting room to remove!");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(LocationOptionArgument.values())
						.map(LocationOptionArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.SPAWN_PARTICLES.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasSpawnParticles()) {
					VDCommandExecutor.notifyFailure(sender, "Spawn particles are already on!");
					return;
				}

				// Turn on
				arena.setSpawnParticles(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Spawn particles are on for " + arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasSpawnParticles()) {
					VDCommandExecutor.notifyFailure(sender, "Spawn particles are already off!");
					return;
				}

				// Turn off
				arena.setSpawnParticles(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Spawn particles are off for " + arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.MAX_PLAYERS.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer.");
				return;
			}

			// Check if greater than min
			if (num < arena.getMinPlayers()) {
				VDCommandExecutor.notifyFailure(sender, "Max players cannot be less than min players!");
				return;
			}

			// Set new value
			arena.setMaxPlayers(num);
			VDCommandExecutor.notifySuccess(sender, "Max players for " + arena.getName() + " set to " + num + ".");
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.MIN_PLAYERS.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer.");
				return;
			}

			// Check if greater than 0
			if (num < 1) {
				VDCommandExecutor.notifyFailure(sender, "Min players cannot be less than 1!");
				return;
			}

			// Set new value
			arena.setMinPlayers(num);
			VDCommandExecutor.notifySuccess(sender, "Min players for " + arena.getName() + " set to " + num + ".");
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.MONSTER_SPAWN_PARTICLES.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasMonsterParticles()) {
					VDCommandExecutor.notifyFailure(sender, "Monster spawn particles are already on!");
					return;
				}

				// Turn on
				arena.setMonsterParticles(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Monster spawn particles are on for " + arena.getName() +
					".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasMonsterParticles()) {
					VDCommandExecutor.notifyFailure(sender, "Monster spawn particles are already off!");
					return;
				}

				// Turn off
				arena.setMonsterParticles(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Monster spawn particles are off for " + arena.getName() +
					".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.VILLAGER_SPAWN_PARTICLES.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasVillagerParticles()) {
					VDCommandExecutor.notifyFailure(sender, "Villager spawn particles are already on!");
					return;
				}

				// Turn on
				arena.setVillagerParticles(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Villager spawn particles are on for " + arena.getName() +
					".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasVillagerParticles()) {
					VDCommandExecutor.notifyFailure(sender, "Villager spawn particles are already off!");
					return;
				}

				// Turn off
				arena.setVillagerParticles(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Villager spawn particles are off for " + arena.getName() +
					".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.DYNAMIC_MOB_COUNT.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasDynamicCount()) {
					VDCommandExecutor.notifyFailure(sender, "Dynamic mob count is already on!");
					return;
				}

				// Turn on
				arena.setDynamicCount(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Dynamic mob count is on for " +
					arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasDynamicCount()) {
					VDCommandExecutor.notifyFailure(sender, "Dynamic mob count is already off!");
					return;
				}

				// Turn off
				arena.setDynamicCount(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Dynamic mob count is off for " +
					arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.DEFAULT_SHOP.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasNormal()) {
					VDCommandExecutor.notifyFailure(sender, "Default shop is already on!");
					return;
				}

				// Turn on
				arena.setNormal(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Default shop is on for " +
					arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasNormal()) {
					VDCommandExecutor.notifyFailure(sender, "Default shop is already off!");
					return;
				}

				// Turn off
				arena.setNormal(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Default shop is off for " +
					arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.CUSTOM_SHOP.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasCustom()) {
					VDCommandExecutor.notifyFailure(sender, "Custom shop is already on!");
					return;
				}

				// Turn on
				arena.setCustom(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Custom shop is on for " +
					arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasCustom()) {
					VDCommandExecutor.notifyFailure(sender, "Custom shop is already off!");
					return;
				}

				// Turn off
				arena.setCustom(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Custom shop is off for " +
					arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.ENCHANT_SHOP.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasEnchants()) {
					VDCommandExecutor.notifyFailure(sender, "Enchant shop is already on!");
					return;
				}

				// Turn on
				arena.setEnchants(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Enchant shop is on for " +
					arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasEnchants()) {
					VDCommandExecutor.notifyFailure(sender, "Enchant shop is already off!");
					return;
				}

				// Turn off
				arena.setEnchants(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Enchant shop is off for " +
					arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.COMMUNITY_CHEST.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasCommunity()) {
					VDCommandExecutor.notifyFailure(sender, "Community chest is already on!");
					return;
				}

				// Turn on
				arena.setCommunity(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Community chest is on for " + arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasCommunity()) {
					VDCommandExecutor.notifyFailure(sender, "Community chest is already off!");
					return;
				}

				// Turn off
				arena.setCommunity(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Community chest is off for " + arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.DYNAMIC_PRICES.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasDynamicPrices()) {
					VDCommandExecutor.notifyFailure(sender, "Dynamic prices are already on!");
					return;
				}

				// Turn on
				arena.setDynamicPrices(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Dynamic prices are on for " +
					arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasDynamicPrices()) {
					VDCommandExecutor.notifyFailure(sender, "Dynamic prices are already off!");
					return;
				}

				// Turn off
				arena.setDynamicPrices(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Dynamic prices are off for " +
					arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.DYNAMIC_TIME_LIMIT.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasDynamicLimit()) {
					VDCommandExecutor.notifyFailure(sender, "Dynamic time limit is already on!");
					return;
				}

				// Turn on
				arena.setDynamicLimit(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Dynamic time limit is on for " + arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasDynamicLimit()) {
					VDCommandExecutor.notifyFailure(sender, "Dynamic time limit is already off!");
					return;
				}

				// Turn off
				arena.setDynamicLimit(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Dynamic time limit is off for " + arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.DYNAMIC_DIFFICULTY.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasDynamicDifficulty()) {
					VDCommandExecutor.notifyFailure(sender, "Dynamic difficulty is already on!");
					return;
				}

				// Turn on
				arena.setDynamicDifficulty(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Dynamic difficulty is on for " +
					arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasDynamicDifficulty()) {
					VDCommandExecutor.notifyFailure(sender, "Dynamic difficulty is already off!");
					return;
				}

				// Turn off
				arena.setDynamicDifficulty(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Dynamic difficulty is off for " +
					arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.LATE_ARRIVAL.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasLateArrival()) {
					VDCommandExecutor.notifyFailure(sender, "Late arrival is already on!");
					return;
				}

				// Turn on
				arena.setLateArrival(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Late arrival is on for " + arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasLateArrival()) {
					VDCommandExecutor.notifyFailure(sender, "Late arrival is already off!");
					return;
				}

				// Turn off
				arena.setLateArrival(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Late arrival is off for " + arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.EXPERIENCE_DROP.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasExpDrop()) {
					VDCommandExecutor.notifyFailure(sender, "Experience drop is already on!");
					return;
				}

				// Turn on
				arena.setExpDrop(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Experience drop is on for " +
					arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasExpDrop()) {
					VDCommandExecutor.notifyFailure(sender, "Experience drop is already off!");
					return;
				}

				// Turn off
				arena.setExpDrop(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Experience drop is off for " +
					arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.ITEM_DROP.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasGemDrop()) {
					VDCommandExecutor.notifyFailure(sender, "Item drop is already on!");
					return;
				}

				// Turn on
				arena.setGemDrop(true);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Item drop is on for " +
					arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasGemDrop()) {
					VDCommandExecutor.notifyFailure(sender, "Item drop is already off!");
					return;
				}

				// Turn off
				arena.setGemDrop(false);

				// Notify console and possibly player
				VDCommandExecutor.notifySuccess(sender, "Item drop is off for " +
					arena.getName() + ".");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.MAX_WAVES.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer or -1.");
				return;
			}

			// Check if greater than 0 or is -1
			if (num < 1 && num != -1) {
				VDCommandExecutor.notifyFailure(sender, "Max waves cannot be less than 1!");
				return;
			}

			// Set new value
			arena.setMaxWaves(num);
			VDCommandExecutor.notifySuccess(sender, "Max waves for " + arena.getName() + " set to " + num + ".");
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.WAVE_TIME_LIMIT.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer or -1.");
				return;
			}

			// Check if greater than 0 or is -1
			if (num < 1 && num != -1) {
				VDCommandExecutor.notifyFailure(sender, "Wave time limit cannot be less than 1!");
				return;
			}

			// Set new value
			arena.setWaveTimeLimit(num);
			VDCommandExecutor.notifySuccess(sender, "Wave time limit for " + arena.getName() + " set to " + num +
				".");
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.WOLF_CAP.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer.");
				return;
			}

			// Check if greater than 0
			if (num < 1) {
				VDCommandExecutor.notifyFailure(sender, "Wolf cap cannot be less than 1!");
				return;
			}

			// Set new value
			arena.setWolfCap(num);
			VDCommandExecutor.notifySuccess(sender, "Wolf cap for " + arena.getName() + " set to " +
				num + ".");
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.GOLEM_CAP.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer.");
				return;
			}

			// Check if greater than 0
			if (num < 1) {
				VDCommandExecutor.notifyFailure(sender, "Iron golem cap cannot be less than 1!");
				return;
			}

			// Set new value
			arena.setgolemCap(num);
			VDCommandExecutor.notifySuccess(sender, "Iron golem cap for " + arena.getName() + " set to " +
				num + ".");
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.DIFFICULTY_LABEL.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (DifficultyLabelArgument.EASY.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel("Easy");
				VDCommandExecutor.notifySuccess(sender, arena.getName() + " is set to Easy.");
			}
			else if (DifficultyLabelArgument.MEDIUM.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel("Medium");
				VDCommandExecutor.notifySuccess(sender, arena.getName() + " is set to Medium.");
			}
			else if (DifficultyLabelArgument.HARD.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel("Hard");
				VDCommandExecutor.notifySuccess(sender, arena.getName() + " is set to Hard.");
			}
			else if (DifficultyLabelArgument.INSANE.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel("Insane");
				VDCommandExecutor.notifySuccess(sender, arena.getName() + " is set to Insane.");
			}
			else if (DifficultyLabelArgument.NONE.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel(null);
				VDCommandExecutor.notifySuccess(sender, arena.getName() + " is set to None.");
			}
			else VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(DifficultyLabelArgument.values())
						.map(DifficultyLabelArgument::getArg)
						.toArray()));
		}
		else if (GuardClause.checkArgStartWith(args, 2, ArenaOperationArgument.DIFFICULTY_MULTIPLIER.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				VDCommandExecutor.notifyFailure(sender, "Invalid operation value. Value must be an integer " +
					"between 1 and 4 inclusive.");
				return;
			}

			// Check if within range
			if (num < 1 || num > 4) {
				VDCommandExecutor.notifyFailure(sender, "Difficulty multiplier must be between 1 and 4!");
				return;
			}

			// Set new value
			arena.setDifficultyMultiplier(num);
			VDCommandExecutor.notifySuccess(sender, "Difficulty multiplier for " + arena.getName() + " set to " +
				num + ".");
		}
		else if (GuardClause.checkArg(args, 2, ArenaOperationArgument.REMOVE.arg)) {
			player = GuardClause.checkSenderPlayer(sender);

			player.openInventory(Inventories.createArenaConfirmMenu(arena));
		}

		// No valid command
		else throw new WrongFormatException(COMMAND_FORMAT);
	}
}
