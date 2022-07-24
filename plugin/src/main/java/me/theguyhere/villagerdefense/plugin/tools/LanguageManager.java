package me.theguyhere.villagerdefense.plugin.tools;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLanguageKeyException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class LanguageManager {
    private static FileConfiguration config;

    // Sections
    public static Achievements achievements;
    public static ArenaStats arenaStats;
    public static Challenges challenges;
    public static Confirms confirms;
    public static Errors errors;
    public static Kits kits;
    public static Messages messages;
    public static Names names;
    public static PlayerStats playerStats;
    public static Rewards rewards;

    public static void init(FileConfiguration config) throws InvalidLanguageKeyException {
        LanguageManager.config = config;

        LanguageManager.achievements = new Achievements();
        LanguageManager.arenaStats = new ArenaStats();
        LanguageManager.challenges = new Challenges();
        LanguageManager.confirms = new Confirms();
        LanguageManager.errors = new Errors();
        LanguageManager.kits = new Kits();
        LanguageManager.messages = new Messages();
        LanguageManager.names = new Names();
        LanguageManager.playerStats = new PlayerStats();
        LanguageManager.rewards = new Rewards();
    }

    private static abstract class Section {
        String pathPrefix;

        void setPathPrefix(String pathPrefix) {
            this.pathPrefix = pathPrefix + ".";
        }

        @NotNull String getConfigString(String path) throws InvalidLanguageKeyException {
            String result = config.getString(pathPrefix + path);
            if (result == null)
                throw new InvalidLanguageKeyException("The key '" + pathPrefix + path + "' is either missing or" +
                        " corrupt in the active language file");
            else return result;
        }
    }

    public static class Achievements extends Section {
        public final @NotNull SpecialAchievement allAbility;
        public final @NotNull SpecialAchievement allChallenges;
        public final @NotNull SpecialAchievement allEffect;
        public final @NotNull SpecialAchievement allGift;
        public final @NotNull SpecialAchievement allKits;
        public final @NotNull SpecialAchievement allMaxedAbility;
        public final @NotNull SpecialAchievement alone;
        public final @NotNull AchievementPortion amputee;
        public final @NotNull AchievementPortion blind;
        public final @NotNull AchievementPortion challengeDescription;
        public final @NotNull AchievementPortion clumsy;
        public final @NotNull AchievementPortion dwarf;
        public final @NotNull AchievementPortion explosive;
        public final @NotNull AchievementPortion featherweight;
        public final @NotNull SpecialAchievement maxedAbility;
        public final @NotNull AchievementPortion naked;
        public final @NotNull AchievementPortion pacifist;
        public final @NotNull SpecialAchievement pacifistUhc;
        public final @NotNull AchievementSection topBalance;
        public final @NotNull AchievementSection topKills;
        public final @NotNull AchievementSection topWave;
        public final @NotNull AchievementSection totalGems;
        public final @NotNull AchievementSection totalKills;
        public final @NotNull AchievementPortion uhc;

        private Achievements() throws InvalidLanguageKeyException {
            allAbility = new SpecialAchievement("allAbility");
            allChallenges = new SpecialAchievement("allChallenges");
            allEffect = new SpecialAchievement("allEffect");
            allGift = new SpecialAchievement("allGift");
            allKits = new SpecialAchievement("allKits");
            allMaxedAbility = new SpecialAchievement("allMaxedAbility");
            alone = new SpecialAchievement("alone");
            amputee = new AchievementPortion("amputee");
            blind = new AchievementPortion("blind");
            challengeDescription = new AchievementPortion("challengeDescription");
            clumsy = new AchievementPortion("clumsy");
            dwarf = new AchievementPortion("dwarf");
            explosive = new AchievementPortion("explosive");
            featherweight = new AchievementPortion("featherweight");
            maxedAbility = new SpecialAchievement("maxedAbility");
            naked = new AchievementPortion("naked");
            pacifist = new AchievementPortion("pacifist");
            pacifistUhc = new SpecialAchievement("pacifistUhc");
            topBalance = new AchievementSection("topBalance");
            topKills = new AchievementSection("topKills");
            topWave = new AchievementSection("topWave");
            totalGems = new AchievementSection("totalGems");
            totalKills = new AchievementSection("totalKills");
            uhc = new AchievementPortion("uhc");
        }

        public static class AchievementSection extends Section {
            public final @NotNull String one;
            public final @NotNull String two;
            public final @NotNull String three;
            public final @NotNull String four;
            public final @NotNull String five;
            public final @NotNull String six;
            public final @NotNull String seven;
            public final @NotNull String eight;
            public final @NotNull String nine;
            public final @NotNull String description;

            private AchievementSection(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("achievements." + key);

                one = getConfigString("1");
                two = getConfigString("2");
                three = getConfigString("3");
                four = getConfigString("4");
                five = getConfigString("5");
                six = getConfigString("6");
                seven = getConfigString("7");
                eight = getConfigString("8");
                nine = getConfigString("9");
                description = getConfigString("description");
            }
        }

        public static class AchievementPortion extends Section {
            public final @NotNull String alone;
            public final @NotNull String balance;
            public final @NotNull String kills;
            public final @NotNull String wave;

            private AchievementPortion(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("achievements." + key);

                alone = getConfigString("alone");
                balance = getConfigString("balance");
                kills = getConfigString("kills");
                wave = getConfigString("wave");
            }
        }

        public static class SpecialAchievement extends Section {
            public final @NotNull String name;
            public final @NotNull String description;

            private SpecialAchievement(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("achievements." + key);

                name = getConfigString("name");
                description = getConfigString("description");
            }
        }
    }

    public static class ArenaStats {
        public final @NotNull ArenaStat difficultyMultiplier;
        public final @NotNull ArenaStat dynamicDifficulty;
        public final @NotNull ArenaStat dynamicMobCount;
        public final @NotNull ArenaStat dynamicPrices;
        public final @NotNull ArenaStat dynamicTimeLimit;
        public final @NotNull ArenaStat expDrop;
        public final @NotNull ArenaStat gemDrop;
        public final @NotNull ArenaStat golemCap;
        public final @NotNull ArenaStat lateArrival;
        public final @NotNull ArenaStat maxPlayers;
        public final @NotNull ArenaStat maxWaves;
        public final @NotNull ArenaStat minPlayers;
        public final @NotNull ArenaStat timeLimit;
        public final @NotNull ArenaStat wolfCap;

        private ArenaStats() throws InvalidLanguageKeyException {
            difficultyMultiplier = new ArenaStat("difficultyMultiplier");
            dynamicDifficulty = new ArenaStat("dynamicDifficulty");
            dynamicMobCount = new ArenaStat("dynamicMobCount");
            dynamicPrices = new ArenaStat("dynamicPrices");
            dynamicTimeLimit = new ArenaStat("dynamicTimeLimit");
            expDrop = new ArenaStat("expDrop");
            gemDrop = new ArenaStat("gemDrop");
            golemCap = new ArenaStat("golemCap");
            lateArrival = new ArenaStat("lateArrival");
            maxPlayers = new ArenaStat("maxPlayers");
            maxWaves = new ArenaStat("maxWaves");
            minPlayers = new ArenaStat("minPlayers");
            timeLimit = new ArenaStat("timeLimit");
            wolfCap = new ArenaStat("wolfCap");
        }

        public static class ArenaStat extends Section {
            public final @NotNull String name;
            public final @NotNull String description;

            private ArenaStat(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("arenaStats." + key);

                name = getConfigString("name");
                description = getConfigString("description");
            }
        }
    }

    public static class Challenges {
        public final @NotNull Challenge amputee;
        public final @NotNull Challenge blind;
        public final @NotNull Challenge clumsy;
        public final @NotNull Challenge dwarf;
        public final @NotNull Challenge explosive;
        public final @NotNull Challenge featherweight;
        public final @NotNull Challenge naked;
        public final @NotNull Challenge pacifist;
        public final @NotNull Challenge uhc;

        private Challenges() throws InvalidLanguageKeyException {
            amputee = new Challenge("amputee");
            blind = new Challenge("blind");
            clumsy = new Challenge("clumsy");
            dwarf = new Challenge("dwarf");
            explosive = new Challenge("explosive");
            featherweight = new Challenge("featherweight");
            naked = new Challenge("naked");
            pacifist = new Challenge("pacifist");
            uhc = new Challenge("uhc");
        }

        public static class Challenge extends Section {
            public final @NotNull String name;
            public final @NotNull String description1;
            public final @NotNull String description2;

            private Challenge(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("challenges." + key);

                name = getConfigString("name");
                description1 = getConfigString("description1");
                description2 = getConfigString("description2");
            }
        }
    }

    public static class Confirms extends Section {
        public final @NotNull String achievement;
        public final @NotNull String autoUpdate;
        public final @NotNull String balanceSet;
        public final @NotNull String boostAdd;
        public final @NotNull String boots;
        public final @NotNull String buy;
        public final @NotNull String carePackage;
        public final @NotNull String challengeAdd;
        public final @NotNull String challengeDelete;
        public final @NotNull String chestplate;
        public final @NotNull String crystalAdd;
        public final @NotNull String helmet;
        public final @NotNull String kitBuy;
        public final @NotNull String kitSelect;
        public final @NotNull String kitUpgrade;
        public final @NotNull String leggings;
        public final @NotNull String reset;

        private Confirms() throws InvalidLanguageKeyException {
            setPathPrefix("confirms");

            achievement = getConfigString("achievement");
            autoUpdate = getConfigString("autoUpdate");
            balanceSet = getConfigString("balanceSet");
            boostAdd = getConfigString("boostAdd");
            boots = getConfigString("boots");
            buy = getConfigString("buy");
            carePackage = getConfigString("carePackage");
            challengeAdd = getConfigString("challengeAdd");
            challengeDelete = getConfigString("challengeDelete");
            chestplate = getConfigString("chestplate");
            crystalAdd = getConfigString("crystalAdd");
            helmet = getConfigString("helmet");
            kitBuy = getConfigString("kitBuy");
            kitSelect = getConfigString("kitSelect");
            kitUpgrade = getConfigString("kitUpgrade");
            leggings = getConfigString("leggings");
            reset = getConfigString("reset");
        }
    }

    public static class Errors extends Section {
        public final @NotNull String activePlayer;
        public final @NotNull String ammoOffHand;
        public final @NotNull String amputee;
        public final @NotNull String arenaInProgress;
        public final @NotNull String arenaNoPlayers;
        public final @NotNull String bounds;
        public final @NotNull String buy;
        public final @NotNull String buyGeneral;
        public final @NotNull String close;
        public final @NotNull String command;
        public final @NotNull String communityChest;
        public final @NotNull String construction;
        public final @NotNull String cooldown;
        public final @NotNull String economy;
        public final @NotNull String emptyArena;
        public final @NotNull String endingSoon;
        public final @NotNull String fatal;
        public final @NotNull String forcedChallenge;
        public final @NotNull String golem;
        public final @NotNull String hasForcedChallenges;
        public final @NotNull String inGame;
        public final @NotNull String integer;
        public final @NotNull String invalidPlayer;
        public final @NotNull String inventoryFull;
        public final @NotNull String join;
        public final @NotNull String kitBuy;
        public final @NotNull String kitSelect;
        public final @NotNull String kitUpgrade;
        public final @NotNull String level;
        public final @NotNull String naked;
        public final @NotNull String ninja;
        public final @NotNull String noArena;
        public final @NotNull String noGameEnd;
        public final @NotNull String notInGame;
        public final @NotNull String offWeapon;
        public final @NotNull String outdated;
        public final @NotNull String permission;
        public final @NotNull String phantomArena;
        public final @NotNull String phantomOwn;
        public final @NotNull String phantomPlayer;
        public final @NotNull String playerOnlyCommand;
        public final @NotNull String startingSoon;
        public final @NotNull String suicide;
        public final @NotNull String suicideActive;
        public final @NotNull String teleport;
        public final @NotNull String wolf;

        private Errors() throws InvalidLanguageKeyException {
            setPathPrefix("errors");

            activePlayer = getConfigString("activePlayer");
            ammoOffHand = getConfigString("ammoOffHand");
            amputee = getConfigString("amputee");
            arenaInProgress = getConfigString("arenaInProgress");
            arenaNoPlayers = getConfigString("arenaNoPlayers");
            bounds = getConfigString("bounds");
            buy = getConfigString("buy");
            buyGeneral = getConfigString("buyGeneral");
            close = getConfigString("close");
            command = getConfigString("command");
            communityChest = getConfigString("communityChest");
            construction = getConfigString("construction");
            cooldown = getConfigString("cooldown");
            economy = getConfigString("economy");
            emptyArena = getConfigString("emptyArena");
            endingSoon = getConfigString("endingSoon");
            fatal = getConfigString("fatal");
            forcedChallenge = getConfigString("forcedChallenge");
            golem = getConfigString("golem");
            hasForcedChallenges = getConfigString("hasForcedChallenges");
            inGame = getConfigString("inGame");
            integer = getConfigString("integer");
            invalidPlayer = getConfigString("invalidPlayer");
            inventoryFull = getConfigString("inventoryFull");
            join = getConfigString("join");
            kitBuy = getConfigString("kitBuy");
            kitSelect = getConfigString("kitSelect");
            kitUpgrade = getConfigString("kitUpgrade");
            level = getConfigString("level");
            naked = getConfigString("naked");
            ninja = getConfigString("ninja");
            noArena = getConfigString("noArena");
            noGameEnd = getConfigString("noGameEnd");
            notInGame = getConfigString("notInGame");
            offWeapon = getConfigString("offWeapon");
            outdated = getConfigString("outdated");
            permission = getConfigString("permission");
            phantomArena = getConfigString("phantomArena");
            phantomOwn = getConfigString("phantomOwn");
            phantomPlayer = getConfigString("phantomPlayer");
            playerOnlyCommand = getConfigString("playerOnlyCommand");
            startingSoon = getConfigString("startingSoon");
            suicide = getConfigString("suicide");
            suicideActive = getConfigString("suicideActive");
            teleport = getConfigString("teleport");
            wolf = getConfigString("wolf");
        }
    }

    public static class Kits {
        public final @NotNull GiftKit alchemist;
        public final @NotNull Kit blacksmith;
        public final @NotNull GiftKit farmer;
        public final @NotNull Kit giant;
        public final @NotNull Kit knight;
        public final @NotNull Kit mage;
        public final @NotNull Kit merchant;
        public final @NotNull Kit messenger;
        public final @NotNull Kit monk;
        public final @NotNull Kit ninja;
        public final @NotNull GiftKit orc;
        public final @NotNull Kit phantom;
        public final @NotNull Kit priest;
        public final @NotNull GiftKit reaper;
        public final @NotNull Kit siren;
        public final @NotNull GiftKit soldier;
        public final @NotNull TieredGiftKit summoner;
        public final @NotNull GiftKit tailor;
        public final @NotNull Kit templar;
        public final @NotNull Kit trader;
        public final @NotNull Kit vampire;
        public final @NotNull Kit warrior;
        public final @NotNull Kit witch;

        public Kits() throws InvalidLanguageKeyException {
            alchemist = new GiftKit("alchemist");
            blacksmith = new Kit("blacksmith");
            farmer = new GiftKit("farmer");
            giant = new Kit("giant");
            knight = new Kit("knight");
            mage = new Kit("mage");
            merchant = new Kit("merchant");
            messenger = new Kit("messenger");
            monk = new Kit("monk");
            ninja = new Kit("ninja");
            orc = new GiftKit("orc");
            phantom = new Kit("phantom");
            priest = new Kit("priest");
            reaper = new GiftKit("reaper");
            siren = new Kit("siren");
            soldier = new GiftKit("soldier");
            summoner = new TieredGiftKit("summoner");
            tailor = new GiftKit("tailor");
            templar = new Kit("templar");
            trader = new Kit("trader");
            vampire = new Kit("vampire");
            warrior = new Kit("warrior");
            witch = new Kit("witch");
        }

        public static class Kit extends Section {
            public final @NotNull String name;
            public final @NotNull String description;

            private Kit(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("kits." + key);

                name = getConfigString("name");
                description = getConfigString("description");
            }
        }

        public static class GiftKit extends Section {
            public final @NotNull String name;
            public final @NotNull String description;
            public final @NotNull Items items;

            private GiftKit(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("kits." + key);

                name = getConfigString("name");
                description = getConfigString("description");
                items = new Items(key);
            }
        }

        public static class TieredGiftKit extends Section {
            public final @NotNull String name;
            public final @NotNull String description1;
            public final @NotNull String description2;
            public final @NotNull String description3;
            public final @NotNull Items items;

            private TieredGiftKit(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("kits." + key);

                name = getConfigString("name");
                description1 = getConfigString("description1");
                description2 = getConfigString("description2");
                description3 = getConfigString("description3");
                items = new Items(key);
            }
        }

        public static class Items extends Section {
            public final String boots;
            public final String carrot;
            public final String chestplate;
            public final String club;
            public final String golem;
            public final String health;
            public final String helmet;
            public final String leggings;
            public final String scythe;
            public final String speed;
            public final String sword;
            public final String wolf;

            private Items(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("kits." + key + ".items");

                String temp;
                try {
                    temp = getConfigString("boots");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("tailor"))
                        throw e;
                    else temp = null;
                }
                boots = temp;

                try {
                    temp = getConfigString("carrot");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("farmer"))
                        throw e;
                    else temp = null;
                }
                carrot = temp;

                try {
                    temp = getConfigString("chestplate");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("tailor"))
                        throw e;
                    else temp = null;
                }
                chestplate = temp;

                try {
                    temp = getConfigString("club");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("orc"))
                        throw e;
                    else temp = null;
                }
                club = temp;

                try {
                    temp = getConfigString("golem");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("summoner"))
                        throw e;
                    else temp = null;
                }
                golem = temp;

                try {
                    temp = getConfigString("health");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("alchemist"))
                        throw e;
                    else temp = null;
                }
                health = temp;

                try {
                    temp = getConfigString("helmet");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("tailor"))
                        throw e;
                    else temp = null;
                }
                helmet = temp;
                try {
                    temp = getConfigString("leggings");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("tailor"))
                        throw e;
                    else temp = null;
                }
                leggings = temp;

                try {
                    temp = getConfigString("scythe");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("reaper"))
                        throw e;
                    else temp = null;
                }
                scythe = temp;

                try {
                    temp = getConfigString("speed");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("alchemist"))
                        throw e;
                    else temp = null;
                }
                speed = temp;

                try {
                    temp = getConfigString("sword");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("soldier"))
                        throw e;
                    else temp = null;
                }
                sword = temp;

                try {
                    temp = getConfigString("wolf");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("summoner"))
                        throw e;
                    else temp = null;
                }
                wolf = temp;
            }
        }
    }

    public static class Messages extends Section {
        public final @NotNull String abilityKitsDescription;
        public final @NotNull String abilityLevel;
        public final @NotNull String achievements;
        public final @NotNull String allowedKits;
        public final @NotNull String arenaInfo;
        public final @NotNull String arenaRecords;
        public final @NotNull String armor;
        public final @NotNull String ammo;
        public final @NotNull String ammoCost;
        public final @NotNull String attackCritDamage;
        public final @NotNull String attackMainDamage;
        public final @NotNull String attackRangeDamage;
        public final @NotNull String attackSpeed;
        public final @NotNull String attackSweepDamage;
        public final @NotNull String attackType;
        public final @NotNull String available;
        public final @NotNull String axe;
        public final @NotNull String boots;
        public final @NotNull String bow;
        public final @NotNull String capacity;
        public final @NotNull String challenges;
        public final @NotNull String chestplate;
        public final @NotNull String closed;
        public final @NotNull String commandFormat;
        public final @NotNull String crossbow;
        public final @NotNull String crystalBonus;
        public final @NotNull String crystalBalance;
        public final @NotNull String crystalsEarned;
        public final @NotNull String crystalsToConvert;
        public final @NotNull String death;
        public final @NotNull String death1;
        public final @NotNull String death2;
        public final @NotNull String debugLevelSet;
        public final @NotNull String disabled;
        public final @NotNull String duplicatePlaceholder;
        public final @NotNull String effectKitsDescription;
        public final @NotNull String effectShare;
        public final @NotNull String end;
        public final @NotNull String ending;
        public final @NotNull String enemies;
        public final @NotNull String exit;
        public final @NotNull String free;
        public final @NotNull String forcedChallenges;
        public final @NotNull String foundGems;
        public final @NotNull String gameOver;
        public final @NotNull String gems;
        public final @NotNull String gemsReceived;
        public final @NotNull String gemsToReceive;
        public final @NotNull String ghosts;
        public final @NotNull String giftKitsDescription;
        public final @NotNull String helmet;
        public final @NotNull String help;
        public final @NotNull String help1;
        public final @NotNull String help2;
        public final @NotNull String help2a;
        public final @NotNull String help3;
        public final @NotNull String infoAboutWiki;
        public final @NotNull String info1;
        public final @NotNull String info2;
        public final @NotNull String info3;
        public final @NotNull String info4;
        public final @NotNull String info5;
        public final @NotNull String info6;
        public final @NotNull String itemShopDesc;
        public final @NotNull String join;
        public final @NotNull String kills;
        public final @NotNull String kit;
        public final @NotNull String kits;
        public final @NotNull String late;
        public final @NotNull String leave;
        public final @NotNull String leaveArena;
        public final @NotNull String leggings;
        public final @NotNull String level;
        public final @NotNull String levelPlaceholder;
        public final @NotNull String levelUp;
        public final @NotNull String manualUpdateWarn;
        public final @NotNull String maxCapacity;
        public final @NotNull String minutesLeft;
        public final @NotNull String mobName;
        public final @NotNull String nextRefill;
        public final @NotNull String noAutoUpdate;
        public final @NotNull String noStats;
        public final @NotNull String offToggle;
        public final @NotNull String onToggle;
        public final @NotNull String oneMinuteWarning;
        public final @NotNull String pierce;
        public final @NotNull String perBlock;
        public final @NotNull String playerKits;
        public final @NotNull String players;
        public final @NotNull String playerStatistics;
        public final @NotNull String purchase;
        public final @NotNull String purchased;
        public final @NotNull String record;
        public final @NotNull String records;
        public final @NotNull String refill;
        public final @NotNull String reset;
        public final @NotNull String resetWarning;
        public final @NotNull String restartPlugin;
        public final @NotNull String resurrection;
        public final @NotNull String rightClick;
        public final @NotNull String scythe;
        public final @NotNull String seconds;
        public final @NotNull String secondsLeft;
        public final @NotNull String shopInfo;
        public final @NotNull String shopUpgrade;
        public final @NotNull String spectators;
        public final @NotNull String starterSword;
        public final @NotNull String starting;
        public final @NotNull String sword;
        public final @NotNull String toughness;
        public final @NotNull String unavailable;
        public final @NotNull String unlimited;
        public final @NotNull String upToAbilityLevel;
        public final @NotNull String visitWiki;
        public final @NotNull String villageCaptainDialogue1;
        public final @NotNull String villageCaptainDialogue2;
        public final @NotNull String villageCaptainDialogue3;
        public final @NotNull String villageCaptainDialogue4;
        public final @NotNull String villageCaptainDialogue5;
        public final @NotNull String villagers;
        public final @NotNull String waiting;
        public final @NotNull String waitingForPlayers;
        public final @NotNull String wave;
        public final @NotNull String waveNum;
        public final @NotNull String weight;

        private Messages() throws InvalidLanguageKeyException {
            setPathPrefix("messages");

            abilityKitsDescription = getConfigString("abilityKitsDescription");
            abilityLevel = getConfigString("abilityLevel");
            achievements = getConfigString("achievements");
            allowedKits = getConfigString("allowedKits");
            arenaInfo = getConfigString("arenaInfo");
            arenaRecords = getConfigString("arenaRecords");
            armor = getConfigString("armor");
            ammo = getConfigString("ammo");
            ammoCost = getConfigString("ammoCost");
            attackCritDamage = getConfigString("attackCritDamage");
            attackMainDamage = getConfigString("attackMainDamage");
            attackRangeDamage = getConfigString("attackRangeDamage");
            attackSpeed = getConfigString("attackSpeed");
            attackSweepDamage = getConfigString("attackSweepDamage");
            attackType = getConfigString("attackType");
            available = getConfigString("available");
            axe = getConfigString("axe");
            boots = getConfigString("boots");
            bow = getConfigString("bow");
            capacity = getConfigString("capacity");
            challenges = getConfigString("challenges");
            chestplate = getConfigString("chestplate");
            closed = getConfigString("closed");
            commandFormat = getConfigString("commandFormat");
            crossbow = getConfigString("crossbow");
            crystalBonus = getConfigString("crystalBonus");
            crystalBalance = getConfigString("crystalBalance");
            crystalsEarned = getConfigString("crystalsEarned");
            crystalsToConvert = getConfigString("crystalsToConvert");
            death = getConfigString("death");
            death1 = getConfigString("death1");
            death2 = getConfigString("death2");
            debugLevelSet = getConfigString("debugLevelSet");
            disabled = getConfigString("disabled");
            duplicatePlaceholder = getConfigString("duplicatePlaceholder");
            effectKitsDescription = getConfigString("effectKitsDescription");
            effectShare = getConfigString("effectShare");
            end = getConfigString("end");
            ending = getConfigString("ending");
            enemies = getConfigString("enemies");
            exit = getConfigString("exit");
            free = getConfigString("free");
            forcedChallenges = getConfigString("forcedChallenges");
            foundGems = getConfigString("foundGems");
            gameOver = getConfigString("gameOver");
            gems = getConfigString("gems");
            gemsReceived = getConfigString("gemsReceived");
            gemsToReceive = getConfigString("gemsToReceive");
            ghosts = getConfigString("ghosts");
            giftKitsDescription = getConfigString("giftKitsDescription");
            helmet = getConfigString("helmet");
            help = getConfigString("help");
            help1 = getConfigString("help1");
            help2 = getConfigString("help2");
            help2a = getConfigString("help2a");
            help3 = getConfigString("help3");
            infoAboutWiki = getConfigString("infoAboutWiki");
            info1 = getConfigString("info1");
            info2 = getConfigString("info2");
            info3 = getConfigString("info3");
            info4 = getConfigString("info4");
            info5 = getConfigString("info5");
            info6 = getConfigString("info6");
            itemShopDesc = getConfigString("itemShopDesc");
            join = getConfigString("join");
            kills = getConfigString("kills");
            kit = getConfigString("kit");
            kits = getConfigString("kits");
            late = getConfigString("late");
            leave = getConfigString("leave");
            leaveArena = getConfigString("leaveArena");
            leggings = getConfigString("leggings");
            level = getConfigString("level");
            levelPlaceholder = getConfigString("levelPlaceholder");
            levelUp = getConfigString("levelUp");
            manualUpdateWarn = getConfigString("manualUpdateWarn");
            maxCapacity = getConfigString("maxCapacity");
            minutesLeft = getConfigString("minutesLeft");
            mobName = getConfigString("mobName");
            nextRefill = getConfigString("nextRefill");
            noAutoUpdate = getConfigString("noAutoUpdate");
            noStats = getConfigString("noStats");
            offToggle = getConfigString("offToggle");
            onToggle = getConfigString("onToggle");
            oneMinuteWarning = getConfigString("oneMinuteWarning");
            pierce = getConfigString("pierce");
            perBlock = getConfigString("perBlock");
            playerKits = getConfigString("playerKits");
            players = getConfigString("players");
            playerStatistics = getConfigString("playerStatistics");
            purchase = getConfigString("purchase");
            purchased = getConfigString("purchased");
            record = getConfigString("record");
            records = getConfigString("records");
            refill = getConfigString("refill");
            reset = getConfigString("reset");
            resetWarning = getConfigString("resetWarning");
            restartPlugin = getConfigString("restartPlugin");
            resurrection = getConfigString("resurrection");
            rightClick = getConfigString("rightClick");
            scythe = getConfigString("scythe");
            seconds = getConfigString("seconds");
            secondsLeft = getConfigString("secondsLeft");
            shopInfo = getConfigString("shopInfo");
            shopUpgrade = getConfigString("shopUpgrade");
            spectators = getConfigString("spectators");
            starterSword = getConfigString("starterSword");
            starting = getConfigString("starting");
            sword = getConfigString("sword");
            toughness = getConfigString("toughness");
            unavailable = getConfigString("unavailable");
            unlimited = getConfigString("unlimited");
            upToAbilityLevel = getConfigString("upToAbilityLevel");
            visitWiki = getConfigString("visitWiki");
            villageCaptainDialogue1 = getConfigString("villageCaptainDialogue1");
            villageCaptainDialogue2 = getConfigString("villageCaptainDialogue2");
            villageCaptainDialogue3 = getConfigString("villageCaptainDialogue3");
            villageCaptainDialogue4 = getConfigString("villageCaptainDialogue4");
            villageCaptainDialogue5 = getConfigString("villageCaptainDialogue5");
            villagers = getConfigString("villagers");
            waiting = getConfigString("waiting");
            waitingForPlayers = getConfigString("waitingForPlayers");
            wave = getConfigString("wave");
            waveNum = getConfigString("waveNum");
            weight = getConfigString("weight");
        }
    }

    public static class Names extends Section {
        public final @NotNull String abilityKits;
        public final @NotNull String armorShop;
        public final @NotNull String boosts;
        public final @NotNull String carePackageExtra;
        public final @NotNull String carePackageLarge;
        public final @NotNull String carePackageMedium;
        public final @NotNull String carePackageSmall;
        public final @NotNull String challengeSelection;
        public final @NotNull String communityChest;
        public final @NotNull String consumableShop;
        public final @NotNull String contents;
        public final @NotNull String crystal;
        public final @NotNull String crystalConverter;
        public final @NotNull String crystals;
        public final @NotNull String easy;
        public final @NotNull String effectKits;
        public final @NotNull String effectShare;
        public final @NotNull String essence;
        public final @NotNull String giftKits;
        public final @NotNull String golemEgg;
        public final @NotNull String hard;
        public final @NotNull String insane;
        public final @NotNull String itemShop;
        public final @NotNull String kitSelection;
        public final @NotNull String medium;
        public final @NotNull String monsterSpawnParticles;
        public final @NotNull String none;
        public final @NotNull String normal;
        public final @NotNull String penetrating;
        public final @NotNull String playerSpawnParticles;
        public final @NotNull String timeBar;
        public final @NotNull String villageCaptain;
        public final @NotNull String villagerSpawnParticles;
        public final @NotNull String weaponShop;

        private Names() throws InvalidLanguageKeyException {
            setPathPrefix("names");

            abilityKits = getConfigString("abilityKits");
            armorShop = getConfigString("armorShop");
            boosts = getConfigString("boosts");
            carePackageExtra = getConfigString("carePackageExtra");
            carePackageLarge = getConfigString("carePackageLarge");
            carePackageMedium = getConfigString("carePackageMedium");
            carePackageSmall = getConfigString("carePackageSmall");
            challengeSelection = getConfigString("challengeSelection");
            communityChest = getConfigString("communityChest");
            consumableShop = getConfigString("consumableShop");
            contents = getConfigString("contents");

            // Special check for economy plugin to replace currency name
            if (Main.hasCustomEconomy())
                crystal = Main.getEconomy().currencyNameSingular();
            else crystal = getConfigString("crystal");

            crystalConverter = getConfigString("crystalConverter");

            // Special check for economy plugin to replace currency name
            if (Main.hasCustomEconomy())
                crystals = Main.getEconomy().currencyNamePlural();
            else crystals = getConfigString("crystals");

            easy = getConfigString("easy");
            effectKits = getConfigString("effectKits");
            effectShare = getConfigString("effectShare");
            essence = getConfigString("essence");
            giftKits = getConfigString("giftKits");
            golemEgg = getConfigString("golemEgg");
            hard = getConfigString("hard");
            insane = getConfigString("insane");
            itemShop = getConfigString("itemShop");
            kitSelection = getConfigString("kitSelection");
            medium = getConfigString("medium");
            monsterSpawnParticles = getConfigString("monsterSpawnParticles");
            none = getConfigString("none");
            normal = getConfigString("normal");
            penetrating = getConfigString("penetrating");
            playerSpawnParticles = getConfigString("playerSpawnParticles");
            timeBar = getConfigString("timeBar");
            villageCaptain = getConfigString("villageCaptain");
            villagerSpawnParticles = getConfigString("villagerSpawnParticles");
            weaponShop = getConfigString("weaponShop");
        }
    }

    public static class PlayerStats {
        public final @NotNull PlayerStat topBalance;
        public final @NotNull PlayerStat topKills;
        public final @NotNull PlayerStat topWave;
        public final @NotNull PlayerStat totalGems;
        public final @NotNull PlayerStat totalKills;

        private PlayerStats() throws InvalidLanguageKeyException {
            topBalance = new PlayerStat("topBalance");
            topKills = new PlayerStat("topKills");
            topWave = new PlayerStat("topWave");
            totalGems = new PlayerStat("totalGems");
            totalKills = new PlayerStat("totalKills");
        }

        public static class PlayerStat extends Section {
            public final @NotNull String name;
            public final @NotNull String description;
            public final @NotNull String leaderboard;

            private PlayerStat(@NotNull String key) throws InvalidLanguageKeyException {
                setPathPrefix("playerStats." + key);

                name = getConfigString("name");
                description = getConfigString("description");
                leaderboard = getConfigString("leaderboard");
            }
        }
    }

    public static class Rewards extends Section {
        public final @NotNull String cooldownReduction;
        public final @NotNull String crystalConvert;
        public final @NotNull String crystals;
        public final @NotNull String damageIncrease;
        public final @NotNull String damageReduction;
        public final @NotNull String gemIncrease;
        public final @NotNull String healthIncrease;
        public final @NotNull String resurrection;
        public final @NotNull String shareEffect;
        public final @NotNull String twoKits;

        public Rewards() throws InvalidLanguageKeyException {
            setPathPrefix("rewards");

            cooldownReduction = getConfigString("cooldownReduction");
            crystalConvert = getConfigString("crystalConvert");
            crystals = getConfigString("crystals");
            damageIncrease = getConfigString("damageIncrease");
            damageReduction = getConfigString("damageReduction");
            gemIncrease = getConfigString("gemIncrease");
            healthIncrease = getConfigString("healthIncrease");
            resurrection = getConfigString("resurrection");
            shareEffect = getConfigString("shareEffect");
            twoKits = getConfigString("twoKits");
        }
    }
}
