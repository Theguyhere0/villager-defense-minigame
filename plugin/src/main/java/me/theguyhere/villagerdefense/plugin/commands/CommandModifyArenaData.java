package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.background.DataManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.guis.Inventories;
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
		if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.ADMIN.getArg()) ||
			CommandGuard.checkArgsLengthLess(args, 2))
			return;
		CommandGuard.checkSenderPermissions(sender, CommandPermission.ADMIN);

		// Execute sub commands
		modifyLobby(args, sender);
		modifyInfoBoard(args, sender);
		modifyLeaderboard(args, sender);
		modifyArena(args, sender);

		// No valid commend sent
		if (Arrays
			.stream(Argument.values())
			.noneMatch(arg -> CommandGuard.checkArg(args, 1, arg.getArg())))
			CommandExecImp.notifyFailure(sender, COMMAND_FORMAT);
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
		COMMUNITY_CHEST("communityChest-"),
		DYNAMIC_TIME_LIMIT("dynamicTimeLimit-"),
		LATE_ARRIVAL("lateArrival-"),
		MAX_WAVES("maxWaves-"),
		WAVE_TIME_LIMIT("waveTimeLimit-"),
		DIFFICULTY_LABEL("difficultyLabel-"),
		DIFFICULTY_MULTIPLIER("difficultyMultiplier"),
		GAME_MODE("gameMode-");
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

	private enum GameModeArgument {
		LEGACY("legacy"),
		FREEPLAY("freeplay"),
		CAMPAIGN("campaign");
		private final String arg;

		GameModeArgument(String arg) {
			this.arg = arg;
		}

		private String getArg() {
			return arg;
		}
	}

	private static void modifyLobby(String[] args, CommandSender sender) throws CommandException {
		final String COMMAND_FORMAT = "/vd admin lobby [center, remove, set, teleport]";

		// Guard clauses
		if (!CommandGuard.checkArg(args, 1, Argument.LOBBY.arg))
			return;
		if (!CommandGuard.checkArgsLengthMatch(args, 3))
			throw new CommandFormatException(COMMAND_FORMAT);

		Player player;
		String path = "lobby";
		Location location = DataManager.getConfigLocationNoRotation(path);

		if (CommandGuard.checkArg(args, 2, LocationOptionArgument.SET.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);

			GameController.saveLobby(player.getLocation());
			PlayerManager.notifySuccess(player, "Lobby set!");
		}
		else if (CommandGuard.checkArg(args, 2, LocationOptionArgument.TELEPORT.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);
			if (location == null) {
				PlayerManager.notifyFailure(player, "No lobby to teleport to!");
				return;
			}

			player.teleport(location);
		}
		else if (CommandGuard.checkArg(args, 2, LocationOptionArgument.CENTER.arg)) {
			if (location == null) {
				CommandExecImp.notifyFailure(sender, "No lobby to center!");
				return;
			}

			DataManager.centerConfigLocation(path);
			CommandExecImp.notifySuccess(sender, "Lobby centered!");
		}
		else if (CommandGuard.checkArg(args, 2, LocationOptionArgument.REMOVE.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);

			if (GameController
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
		else throw new CommandFormatException(COMMAND_FORMAT);
	}

	private static void modifyInfoBoard(String[] args, CommandSender sender) throws CommandException {
		final String COMMAND_FORMAT = "/vd admin infoBoard [create, [info board id]] [center, remove, set, teleport]";

		// Guard clauses
		if (!CommandGuard.checkArg(args, 1, Argument.INFOBOARD.arg))
			return;
		if (CommandGuard.checkArgsLengthGreater(args, 4) ||
			CommandGuard.checkArgsLengthLess(args, 3))
			throw new CommandFormatException(COMMAND_FORMAT);

		Player player;
		int infoBoardID;
		String path;
		Location location;

		if (CommandGuard.checkArg(args, 2, CommandModifyArenaData.CREATE)) {
			player = CommandGuard.checkSenderPlayer(sender);

			GameController.setInfoBoard(player.getLocation(), GameController.newInfoBoardID());
			PlayerManager.notifySuccess(player, "Info board set!");
			return;
		}

		// Get info board ID
		try {
			infoBoardID = Integer.parseInt(args[2]);
		}
		catch (Exception e) {
			throw new CommandFormatException(COMMAND_FORMAT);
		}

		// Check for valid info board ID, then set path and location
		if (!Main
			.getArenaData()
			.contains("infoBoard." + infoBoardID)) {
			CommandExecImp.notifyFailure(sender, "Invalid info board id.");
			return;
		}
		path = "infoBoard." + infoBoardID;
		location = DataManager.getConfigLocationNoRotation(path);

		if (CommandGuard.checkArg(args, 3, LocationOptionArgument.SET.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);

			GameController.setInfoBoard(player.getLocation(), infoBoardID);
			PlayerManager.notifySuccess(player, "Info board set!");
		}
		else if (CommandGuard.checkArg(args, 3, LocationOptionArgument.TELEPORT.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);
			if (location == null) {
				PlayerManager.notifyFailure(player, "No info board to teleport to!");
				return;
			}

			player.teleport(location);
		}
		else if (CommandGuard.checkArg(args, 3, LocationOptionArgument.CENTER.arg)) {
			if (location == null) {
				CommandExecImp.notifyFailure(sender, "No info board to center!");
				return;
			}

			DataManager.centerConfigLocation(path);
			CommandExecImp.notifySuccess(sender, "Info board centered!");
		}
		else if (CommandGuard.checkArg(args, 3, LocationOptionArgument.REMOVE.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);

			if (Main
				.getArenaData()
				.contains(path))
				player.openInventory(Inventories.createInfoBoardConfirmMenu(infoBoardID));
			else PlayerManager.notifyFailure(player, "No info board to remove!");
		}
		else throw new CommandFormatException(COMMAND_FORMAT);
	}

	private static void modifyLeaderboard(String[] args, CommandSender sender) throws CommandException {
		final String COMMAND_FORMAT = "/vd admin leaderboard [leaderboard type] [center, remove, set, teleport]";

		// Guard clauses
		if (!CommandGuard.checkArg(args, 1, Argument.LEADERBOARD.arg))
			return;
		if (!CommandGuard.checkArgsLengthMatch(args, 4))
			throw new CommandFormatException(COMMAND_FORMAT);

		// Check for type validity
		if (Arrays
			.stream(LeaderboardTypeArgument.values())
			.noneMatch(type -> CommandGuard.checkArg(args, 2, type.arg))) {
			CommandExecImp.notifyFailure(sender, "Invalid leaderboard.");
			return;
		}

		Player player;
		String type = args[2];
		String path = "leaderboard." + args[2];
		Location location = DataManager.getConfigLocationNoRotation(path);

		if (CommandGuard.checkArg(args, 3, LocationOptionArgument.SET.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);

			GameController.setLeaderboard(player.getLocation(), type);
			PlayerManager.notifySuccess(player, "Leaderboard set!");
		}
		else if (CommandGuard.checkArg(args, 3, LocationOptionArgument.TELEPORT.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);
			if (location == null) {
				PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
				return;
			}

			player.teleport(location);
		}
		else if (CommandGuard.checkArg(args, 3, LocationOptionArgument.CENTER.arg)) {
			if (location == null) {
				CommandExecImp.notifyFailure(sender, "No leaderboard to center!");
				return;
			}

			DataManager.centerConfigLocation(path);
			CommandExecImp.notifySuccess(sender, "Leaderboard centered!");
		}
		else if (CommandGuard.checkArg(args, 3, LocationOptionArgument.REMOVE.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);


			if (Main
				.getArenaData()
				.contains(path)) {
				if (CommandGuard.checkArg(args, 2, LeaderboardTypeArgument.TOP_BALANCE.arg))
					player.openInventory(Inventories.createTopBalanceConfirmMenu());
				else if (CommandGuard.checkArg(args, 2, LeaderboardTypeArgument.TOP_KILLS.arg))
					player.openInventory(Inventories.createTopKillsConfirmMenu());
				else if (CommandGuard.checkArg(args, 2, LeaderboardTypeArgument.TOP_WAVE.arg))
					player.openInventory(Inventories.createTopWaveConfirmMenu());
				else if (CommandGuard.checkArg(args, 2, LeaderboardTypeArgument.TOTAL_GEMS.arg))
					player.openInventory(Inventories.createTotalGemsConfirmMenu());
				else if (CommandGuard.checkArg(args, 2, LeaderboardTypeArgument.TOTAL_KILLS.arg))
					player.openInventory(Inventories.createTotalKillsConfirmMenu());
			}
			else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
		}
		else throw new CommandFormatException(COMMAND_FORMAT);
	}

	private static void modifyArena(String[] args, CommandSender sender) throws CommandException {
		final String COMMAND_FORMAT = "/vd admin arena [operation][-value] [arena name]";

		// Guard clauses
		if (!CommandGuard.checkArg(args, 1, Argument.ARENA.arg))
			return;
		if (CommandGuard.checkArgsLengthLess(args, 4))
			throw new CommandFormatException(COMMAND_FORMAT);

		// Get arena name
		StringBuilder name = new StringBuilder(args[3]);
		for (int i = 0; i < args.length - 4; i++)
			name
				.append(" ")
				.append(args[i + 4]);

		// Check if this arena exists
		Arena arena;
		try {
			arena = GameController.getArena(name.toString());
		}
		catch (ArenaNotFoundException e) {
			CommandExecImp.notifyFailure(sender, LanguageManager.errors.noArena);
			return;
		}

		Player player;
		Location location;

		// Close or open arena, otherwise check if arena is closed
		if (CommandGuard.checkArg(args, 2, ArenaOperationArgument.CLOSE.arg)) {
			// Check if arena is already closed
			if (arena.isClosed()) {
				CommandExecImp.notifyFailure(sender, "Arena is already closed!");
				return;
			}

			// Close arena
			arena.setClosed(true);

			// Notify console and possibly player
			CommandExecImp.notifySuccess(sender, arena.getName() + " was closed.");
		}
		else if (CommandGuard.checkArg(args, 2, ArenaOperationArgument.OPEN.arg)) {
			// Check if arena is already open
			if (!arena.isClosed()) {
				CommandExecImp.notifyFailure(sender, "Arena is already open!");
				return;
			}

			// No lobby
			if (!Main
				.getArenaData()
				.contains("lobby")) {
				CommandExecImp.notifyFailure(sender, "Arena cannot open without a lobby!");
				return;
			}

			// No arena portal
			if (arena.getPortalLocation() == null) {
				CommandExecImp.notifyFailure(sender, "Arena cannot open without a portal!");
				return;
			}

			// No player spawn
			if (arena.getPlayerSpawn() == null) {
				CommandExecImp.notifyFailure(sender, "Arena cannot open without a player spawn!");
				return;
			}

			// No monster spawn
			if (arena
				.getMonsterSpawns()
				.isEmpty()) {
				CommandExecImp.notifyFailure(sender, "Arena cannot open without a monster spawn!");
				return;
			}

			// No villager spawn
			if (arena
				.getVillagerSpawns()
				.isEmpty()) {
				CommandExecImp.notifyFailure(sender, "Arena cannot open without a villager spawn!");
				return;
			}

			// Invalid arena bounds
			if (arena.getCorner1() == null || arena.getCorner2() == null ||
				!Objects.equals(arena
					.getCorner1()
					.getWorld(), arena
					.getCorner2()
					.getWorld())) {
				CommandExecImp.notifyFailure(sender, "Arena cannot open without valid arena bounds!");
				return;
			}

			// Outdated file configs
			if (Main.isOutdated()) {
				CommandExecImp.notifyFailure(
					sender,
					"Arena cannot open when file configurations are outdated!"
				);
				return;
			}

			// Open arena
			arena.setClosed(false);

			// Notify console and possibly player
			CommandExecImp.notifySuccess(sender, arena.getName() + " was opened.");
		}
		else if (!arena.isClosed())
			CommandExecImp.notifyFailure(sender, "Arena must be closed to modify this!");

			// Other operations
		else if (CommandGuard.checkArg(args, 2, ArenaOperationArgument.RENAME.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);

			NMSVersion
				.getCurrent()
				.getNmsManager()
				.nameArena(player, arena.getName(), arena.getId());
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.PORTAL.arg)) {
			location = arena.getPortalLocation();
			String value = args[2].substring(args[2].indexOf("-") + 1);
			if (LocationOptionArgument.SET.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);

				arena.setPortal(player.getLocation());
				PlayerManager.notifySuccess(player, "Portal set!");
			}
			else if (LocationOptionArgument.TELEPORT.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No portal to teleport to!");
					return;
				}

				player.teleport(location);
			}
			else if (LocationOptionArgument.CENTER.arg.equalsIgnoreCase(value)) {
				if (location == null) {
					CommandExecImp.notifyFailure(sender, "No portal to center!");
					return;
				}

				arena.centerPortal();
				CommandExecImp.notifySuccess(sender, "Portal centered!");
			}
			else if (LocationOptionArgument.REMOVE.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);

				if (arena.getPortal() != null)
					player.openInventory(Inventories.createPortalConfirmMenu(arena));
				else PlayerManager.notifyFailure(player, "No portal to remove!");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(LocationOptionArgument.values())
						.map(LocationOptionArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.LEADERBOARD.arg)) {
			location = arena.getArenaBoardLocation();
			String value = args[2].substring(args[2].indexOf("-") + 1);
			if (LocationOptionArgument.SET.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);

				arena.setArenaBoard(player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
			}
			else if (LocationOptionArgument.TELEPORT.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}

				player.teleport(location);
			}
			else if (LocationOptionArgument.CENTER.arg.equalsIgnoreCase(value)) {
				if (location == null) {
					CommandExecImp.notifyFailure(sender, "No leaderboard to center!");
					return;
				}

				arena.centerArenaBoard();
				CommandExecImp.notifySuccess(sender, "Leaderboard centered!");
			}
			else if (LocationOptionArgument.REMOVE.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);

				if (arena.getArenaBoard() != null)
					player.openInventory(Inventories.createArenaBoardConfirmMenu(arena));
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(LocationOptionArgument.values())
						.map(LocationOptionArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.PLAYER_SPAWN.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (LocationOptionArgument.SET.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);

				arena.setPlayerSpawn(player.getLocation());
				PlayerManager.notifySuccess(player, "Spawn set!");
			}
			else if (LocationOptionArgument.TELEPORT.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);
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
					CommandExecImp.notifySuccess(sender, "Spawn centered!");
				}
				catch (Exception e) {
					CommandExecImp.notifyFailure(sender, "No spawn to center!");
				}
			}
			else if (LocationOptionArgument.REMOVE.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);

				if (arena.getPlayerSpawn() != null)
					player.openInventory(Inventories.createSpawnConfirmMenu(arena));
				else PlayerManager.notifyFailure(player, "No spawn to remove!");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(LocationOptionArgument.values())
						.map(LocationOptionArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.WAITING_ROOM.arg)) {
			location = arena.getWaitingRoom();
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (LocationOptionArgument.SET.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);

				arena.setWaitingRoom(player.getLocation());
				PlayerManager.notifySuccess(player, "Waiting room set!");
			}
			else if (LocationOptionArgument.TELEPORT.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No waiting room to teleport to!");
					return;
				}

				player.teleport(location);
			}
			else if (LocationOptionArgument.CENTER.arg.equalsIgnoreCase(value)) {
				if (location == null) {
					CommandExecImp.notifyFailure(sender, "No waiting room to center!");
					return;
				}

				arena.centerWaitingRoom();
				CommandExecImp.notifySuccess(sender, "Waiting room centered!");
			}
			else if (LocationOptionArgument.REMOVE.arg.equalsIgnoreCase(value)) {
				player = CommandGuard.checkSenderPlayer(sender);

				if (arena.getWaitingRoom() != null)
					player.openInventory(Inventories.createWaitingConfirmMenu(arena));
				else PlayerManager.notifyFailure(player, "No waiting room to remove!");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(LocationOptionArgument.values())
						.map(LocationOptionArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.SPAWN_PARTICLES.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasSpawnParticles()) {
					CommandExecImp.notifyFailure(sender, "Spawn particles are already on!");
					return;
				}

				// Turn on
				arena.setSpawnParticles(true);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Spawn particles are on for " + arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasSpawnParticles()) {
					CommandExecImp.notifyFailure(sender, "Spawn particles are already off!");
					return;
				}

				// Turn off
				arena.setSpawnParticles(false);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Spawn particles are off for " + arena.getName() + ".");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.MAX_PLAYERS.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				CommandExecImp.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer.");
				return;
			}

			// Check if greater than min
			if (num < arena.getMinPlayers()) {
				CommandExecImp.notifyFailure(sender, "Max players cannot be less than min players!");
				return;
			}

			// Set new value
			arena.setMaxPlayers(num);
			CommandExecImp.notifySuccess(sender, "Max players for " + arena.getName() + " set to " + num + ".");
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.MIN_PLAYERS.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				CommandExecImp.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer.");
				return;
			}

			// Check if greater than 0
			if (num < 1) {
				CommandExecImp.notifyFailure(sender, "Min players cannot be less than 1!");
				return;
			}

			// Set new value
			arena.setMinPlayers(num);
			CommandExecImp.notifySuccess(sender, "Min players for " + arena.getName() + " set to " + num + ".");
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.MONSTER_SPAWN_PARTICLES.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasMonsterParticles()) {
					CommandExecImp.notifyFailure(sender, "Monster spawn particles are already on!");
					return;
				}

				// Turn on
				arena.setMonsterParticles(true);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Monster spawn particles are on for " + arena.getName() +
					".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasMonsterParticles()) {
					CommandExecImp.notifyFailure(sender, "Monster spawn particles are already off!");
					return;
				}

				// Turn off
				arena.setMonsterParticles(false);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Monster spawn particles are off for " + arena.getName() +
					".");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.VILLAGER_SPAWN_PARTICLES.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasVillagerParticles()) {
					CommandExecImp.notifyFailure(sender, "Villager spawn particles are already on!");
					return;
				}

				// Turn on
				arena.setVillagerParticles(true);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Villager spawn particles are on for " + arena.getName() +
					".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasVillagerParticles()) {
					CommandExecImp.notifyFailure(sender, "Villager spawn particles are already off!");
					return;
				}

				// Turn off
				arena.setVillagerParticles(false);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Villager spawn particles are off for " + arena.getName() +
					".");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.COMMUNITY_CHEST.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasCommunity()) {
					CommandExecImp.notifyFailure(sender, "Community chest is already on!");
					return;
				}

				// Turn on
				arena.setCommunity(true);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Community chest is on for " + arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasCommunity()) {
					CommandExecImp.notifyFailure(sender, "Community chest is already off!");
					return;
				}

				// Turn off
				arena.setCommunity(false);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Community chest is off for " + arena.getName() + ".");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.DYNAMIC_TIME_LIMIT.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasDynamicLimit()) {
					CommandExecImp.notifyFailure(sender, "Dynamic time limit is already on!");
					return;
				}

				// Turn on
				arena.setDynamicLimit(true);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Dynamic time limit is on for " + arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasDynamicLimit()) {
					CommandExecImp.notifyFailure(sender, "Dynamic time limit is already off!");
					return;
				}

				// Turn off
				arena.setDynamicLimit(false);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Dynamic time limit is off for " + arena.getName() + ".");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.LATE_ARRIVAL.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (ToggleArgument.ON.arg.equalsIgnoreCase(value)) {
				// Check if already on
				if (arena.hasLateArrival()) {
					CommandExecImp.notifyFailure(sender, "Late arrival is already on!");
					return;
				}

				// Turn on
				arena.setLateArrival(true);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Late arrival is on for " + arena.getName() + ".");
			}
			else if (ToggleArgument.OFF.arg.equalsIgnoreCase(value)) {
				// Check if already off
				if (!arena.hasLateArrival()) {
					CommandExecImp.notifyFailure(sender, "Late arrival is already off!");
					return;
				}

				// Turn off
				arena.setLateArrival(false);

				// Notify console and possibly player
				CommandExecImp.notifySuccess(sender, "Late arrival is off for " + arena.getName() + ".");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(ToggleArgument.values())
						.map(ToggleArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.MAX_WAVES.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				CommandExecImp.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer or -1.");
				return;
			}

			// Check if greater than 0 or is -1
			if (num < 1 && num != -1) {
				CommandExecImp.notifyFailure(sender, "Max waves cannot be less than 1!");
				return;
			}

			// Set new value
			arena.setMaxWaves(num);
			CommandExecImp.notifySuccess(sender, "Max waves for " + arena.getName() + " set to " + num + ".");
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.WAVE_TIME_LIMIT.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				CommandExecImp.notifyFailure(sender, "Invalid operation value. Value must be a positive " +
					"integer or -1.");
				return;
			}

			// Check if greater than 0 or is -1
			if (num < 1 && num != -1) {
				CommandExecImp.notifyFailure(sender, "Wave time limit cannot be less than 1!");
				return;
			}

			// Set new value
			arena.setWaveTimeLimit(num);
			CommandExecImp.notifySuccess(sender, "Wave time limit for " + arena.getName() + " set to " + num +
				".");
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.DIFFICULTY_LABEL.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (DifficultyLabelArgument.EASY.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel("Easy");
				CommandExecImp.notifySuccess(sender, arena.getName() + " is set to Easy.");
			}
			else if (DifficultyLabelArgument.MEDIUM.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel("Medium");
				CommandExecImp.notifySuccess(sender, arena.getName() + " is set to Medium.");
			}
			else if (DifficultyLabelArgument.HARD.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel("Hard");
				CommandExecImp.notifySuccess(sender, arena.getName() + " is set to Hard.");
			}
			else if (DifficultyLabelArgument.INSANE.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel("Insane");
				CommandExecImp.notifySuccess(sender, arena.getName() + " is set to Insane.");
			}
			else if (DifficultyLabelArgument.NONE.arg.equalsIgnoreCase(value)) {
				arena.setDifficultyLabel(null);
				CommandExecImp.notifySuccess(sender, arena.getName() + " is set to None.");
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(DifficultyLabelArgument.values())
						.map(DifficultyLabelArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.DIFFICULTY_MULTIPLIER.arg)) {
			// Get value
			int num;
			try {
				num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
			}
			catch (Exception e) {
				CommandExecImp.notifyFailure(sender, "Invalid operation value. Value must be an integer " +
					"between 1 and 4 inclusive.");
				return;
			}

			// Check if within range
			if (num < 1 || num > 4) {
				CommandExecImp.notifyFailure(sender, "Difficulty multiplier must be between 1 and 4!");
				return;
			}

			// Set new value
			arena.setDifficultyMultiplier(num);
			CommandExecImp.notifySuccess(sender, "Difficulty multiplier for " + arena.getName() + " set to " +
				num + ".");
		}
		else if (CommandGuard.checkArgStartWith(args, 2, ArenaOperationArgument.GAME_MODE.arg)) {
			String value = args[2].substring(args[2].indexOf("-") + 1);

			if (GameModeArgument.LEGACY.arg.equalsIgnoreCase(value)) {
//				arena.setGameMode("Legacy");
				CommandExecImp.notifyFailure(sender, LanguageManager.errors.construction);
			}
			else if (GameModeArgument.FREEPLAY.arg.equalsIgnoreCase(value)) {
				arena.setGameMode("Freeplay");
				CommandExecImp.notifySuccess(sender, arena.getName() + " is set to Freeplay.");
			}
			else if (GameModeArgument.CAMPAIGN.arg.equalsIgnoreCase(value)) {
//				arena.setGameMode("Campaign");
				CommandExecImp.notifyFailure(sender, LanguageManager.errors.construction);
			}
			else CommandExecImp.notifyFailure(sender, "Invalid operation value. Valid values: " +
					Arrays.toString(Arrays
						.stream(GameModeArgument.values())
						.map(GameModeArgument::getArg)
						.toArray()));
		}
		else if (CommandGuard.checkArg(args, 2, ArenaOperationArgument.REMOVE.arg)) {
			player = CommandGuard.checkSenderPlayer(sender);

			player.openInventory(Inventories.createArenaConfirmMenu(arena));
		}

		// No valid command
		else throw new CommandFormatException(COMMAND_FORMAT);
	}
}
