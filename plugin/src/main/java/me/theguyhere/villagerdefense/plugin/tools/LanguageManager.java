package me.theguyhere.villagerdefense.plugin.tools;

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
    public static Enchants enchants;
    public static Errors errors;
    public static Kits kits;
    public static Messages messages;
    public static Names names;
    public static PlayerStats playerStats;

    public static void init(FileConfiguration config) throws InvalidLanguageKeyException {
        LanguageManager.config = config;

        LanguageManager.achievements = new Achievements();
        LanguageManager.arenaStats = new ArenaStats();
        LanguageManager.challenges = new Challenges();
        LanguageManager.confirms = new Confirms();
        LanguageManager.enchants = new Enchants();
        LanguageManager.errors = new Errors();
        LanguageManager.kits = new Kits();
        LanguageManager.messages = new Messages();
        LanguageManager.names = new Names();
        LanguageManager.playerStats = new PlayerStats();
    }

    private static abstract class Section {
        @NotNull String getConfigString(String path) throws InvalidLanguageKeyException {
            String result = config.getString(path);
            if (result == null)
                throw new InvalidLanguageKeyException("The key '" + path + "' is either missing or corrupt in the " +
                        "active language file");
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
                String pathPrefix = "achievements." + key;

                one = getConfigString(pathPrefix + ".1");
                two = getConfigString(pathPrefix + ".2");
                three = getConfigString(pathPrefix + ".3");
                four = getConfigString(pathPrefix + ".4");
                five = getConfigString(pathPrefix + ".5");
                six = getConfigString(pathPrefix + ".6");
                seven = getConfigString(pathPrefix + ".7");
                eight = getConfigString(pathPrefix + ".8");
                nine = getConfigString(pathPrefix + ".9");
                description = getConfigString(pathPrefix + ".description");
            }
        }

        public static class AchievementPortion extends Section {
            public final @NotNull String alone;
            public final @NotNull String balance;
            public final @NotNull String kills;
            public final @NotNull String wave;

            private AchievementPortion(@NotNull String key) throws InvalidLanguageKeyException {
                String pathPrefix = "achievements." + key;

                alone = getConfigString(pathPrefix + ".alone");
                balance = getConfigString(pathPrefix + ".balance");
                kills = getConfigString(pathPrefix + ".kills");
                wave = getConfigString(pathPrefix + ".wave");
            }
        }

        public static class SpecialAchievement extends Section {
            public final @NotNull String name;
            public final @NotNull String description;

            private SpecialAchievement(@NotNull String key) throws InvalidLanguageKeyException {
                String pathPrefix = "achievements." + key;

                name = getConfigString(pathPrefix + ".name");
                description = getConfigString(pathPrefix + ".description");
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
                String pathPrefix = "arenaStats." + key;

                name = getConfigString(pathPrefix + ".name");
                description = getConfigString(pathPrefix + ".description");
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
                String pathPrefix = "challenges." + key;

                name = getConfigString(pathPrefix + ".name");
                description1 = getConfigString(pathPrefix + ".description1");
                description2 = getConfigString(pathPrefix + ".description2");
            }
        }
    }

    public static class Confirms extends Section {
        public final @NotNull String autoUpdate;
        public final @NotNull String balanceSet;
        public final @NotNull String boots;
        public final @NotNull String buy;
        public final @NotNull String carePackage;
        public final @NotNull String challengeAdd;
        public final @NotNull String challengeDelete;
        public final @NotNull String chestplate;
        public final @NotNull String enchant;
        public final @NotNull String helmet;
        public final @NotNull String kitBuy;
        public final @NotNull String kitSelect;
        public final @NotNull String kitUpgrade;
        public final @NotNull String leggings;

        private Confirms() throws InvalidLanguageKeyException {
            String pathPrefix = "confirms";

            autoUpdate = getConfigString(pathPrefix + ".autoUpdate");
            balanceSet = getConfigString(pathPrefix + ".balanceSet");
            boots = getConfigString(pathPrefix + ".boots");
            buy = getConfigString(pathPrefix + ".buy");
            carePackage = getConfigString(pathPrefix + ".carePackage");
            challengeAdd = getConfigString(pathPrefix + ".challengeAdd");
            challengeDelete = getConfigString(pathPrefix + ".challengeDelete");
            chestplate = getConfigString(pathPrefix + ".chestplate");
            enchant = getConfigString(pathPrefix + ".enchant");
            helmet = getConfigString(pathPrefix + ".helmet");
            kitBuy = getConfigString(pathPrefix + ".kitBuy");
            kitSelect = getConfigString(pathPrefix + ".kitSelect");
            kitUpgrade = getConfigString(pathPrefix + ".kitUpgrade");
            leggings = getConfigString(pathPrefix + ".leggings");
        }
    }

    public static class Enchants extends Section {
        public final @NotNull String blastProtection;
        public final @NotNull String fireAspect;
        public final @NotNull String flame;
        public final @NotNull String infinity;
        public final @NotNull String knockback;
        public final @NotNull String loyalty;
        public final @NotNull String mending;
        public final @NotNull String multishot;
        public final @NotNull String piercing;
        public final @NotNull String power;
        public final @NotNull String projectileProtection;
        public final @NotNull String protection;
        public final @NotNull String punch;
        public final @NotNull String quickCharge;
        public final @NotNull String sharpness;
        public final @NotNull String smite;
        public final @NotNull String sweepingEdge;
        public final @NotNull String thorns;
        public final @NotNull String unbreaking;

        private Enchants() throws InvalidLanguageKeyException {
            String pathPrefix = "enchants";

            blastProtection = getConfigString(pathPrefix + ".blastProtection");
            fireAspect = getConfigString(pathPrefix + ".fireAspect");
            flame = getConfigString(pathPrefix + ".flame");
            infinity = getConfigString(pathPrefix + ".infinity");
            knockback = getConfigString(pathPrefix + ".knockback");
            loyalty = getConfigString(pathPrefix + ".loyalty");
            mending = getConfigString(pathPrefix + ".mending");
            multishot = getConfigString(pathPrefix + ".multishot");
            piercing = getConfigString(pathPrefix + ".piercing");
            power = getConfigString(pathPrefix + ".power");
            projectileProtection = getConfigString(pathPrefix + ".projectileProtection");
            protection = getConfigString(pathPrefix + ".protection");
            punch = getConfigString(pathPrefix + ".punch");
            quickCharge = getConfigString(pathPrefix + ".quickCharge");
            sharpness = getConfigString(pathPrefix + ".sharpness");
            smite = getConfigString(pathPrefix + ".smite");
            sweepingEdge = getConfigString(pathPrefix + ".sweepingEdge");
            thorns = getConfigString(pathPrefix + ".thorns");
            unbreaking = getConfigString(pathPrefix + ".unbreaking");
        }
    }

    public static class Errors extends Section {
        public final @NotNull String activePlayer;
        public final @NotNull String amputee;
        public final @NotNull String arenaInProgress;
        public final @NotNull String arenaNoPlayers;
        public final @NotNull String bounds;
        public final @NotNull String buy;
        public final @NotNull String close;
        public final @NotNull String command;
        public final @NotNull String communityChest;
        public final @NotNull String cooldown;
        public final @NotNull String customShop;
        public final @NotNull String emptyArena;
        public final @NotNull String enchant;
        public final @NotNull String enchantShop;
        public final @NotNull String endingSoon;
        public final @NotNull String fatal;
        public final @NotNull String golem;
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
        public final @NotNull String normalShop;
        public final @NotNull String notInGame;
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
            String pathPrefix = "errors";

            activePlayer = getConfigString(pathPrefix + ".activePlayer");
            amputee = getConfigString(pathPrefix + ".amputee");
            arenaInProgress = getConfigString(pathPrefix + ".arenaInProgress");
            arenaNoPlayers = getConfigString(pathPrefix + ".arenaNoPlayers");
            bounds = getConfigString(pathPrefix + ".bounds");
            buy = getConfigString(pathPrefix + ".buy");
            close = getConfigString(pathPrefix + ".close");
            command = getConfigString(pathPrefix + ".command");
            communityChest = getConfigString(pathPrefix + ".communityChest");
            cooldown = getConfigString(pathPrefix + ".cooldown");
            customShop = getConfigString(pathPrefix + ".customShop");
            emptyArena = getConfigString(pathPrefix + ".emptyArena");
            enchant = getConfigString(pathPrefix + ".enchant");
            enchantShop = getConfigString(pathPrefix + ".enchantShop");
            endingSoon = getConfigString(pathPrefix + ".endingSoon");
            fatal = getConfigString(pathPrefix + ".fatal");
            golem = getConfigString(pathPrefix + ".golem");
            inGame = getConfigString(pathPrefix + ".inGame");
            integer = getConfigString(pathPrefix + ".integer");
            invalidPlayer = getConfigString(pathPrefix + ".invalidPlayer");
            inventoryFull = getConfigString(pathPrefix + ".inventoryFull");
            join = getConfigString(pathPrefix + ".join");
            kitBuy = getConfigString(pathPrefix + ".kitBuy");
            kitSelect = getConfigString(pathPrefix + ".kitSelect");
            kitUpgrade = getConfigString(pathPrefix + ".kitUpgrade");
            level = getConfigString(pathPrefix + ".level");
            naked = getConfigString(pathPrefix + ".naked");
            ninja = getConfigString(pathPrefix + ".ninja");
            noArena = getConfigString(pathPrefix + ".noArena");
            noGameEnd = getConfigString(pathPrefix + ".noGameEnd");
            normalShop = getConfigString(pathPrefix + ".normalShop");
            notInGame = getConfigString(pathPrefix + ".notInGame");
            outdated = getConfigString(pathPrefix + ".outdated");
            permission = getConfigString(pathPrefix + ".permission");
            phantomArena = getConfigString(pathPrefix + ".phantomArena");
            phantomOwn = getConfigString(pathPrefix + ".phantomOwn");
            phantomPlayer = getConfigString(pathPrefix + ".phantomPlayer");
            playerOnlyCommand = getConfigString(pathPrefix + ".playerOnlyCommand");
            startingSoon = getConfigString(pathPrefix + ".startingSoon");
            suicide = getConfigString(pathPrefix + ".suicide");
            suicideActive = getConfigString(pathPrefix + ".suicideActive");
            teleport = getConfigString(pathPrefix + ".teleport");
            wolf = getConfigString(pathPrefix + ".wolf");
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
                String pathPrefix = "kits." + key;

                name = getConfigString(pathPrefix + ".name");
                description = getConfigString(pathPrefix + ".description");
            }
        }

        public static class GiftKit extends Section {
            public final @NotNull String name;
            public final @NotNull String description;
            public final @NotNull Items items;

            private GiftKit(@NotNull String key) throws InvalidLanguageKeyException {
                String pathPrefix = "kits." + key;

                name = getConfigString(pathPrefix + ".name");
                description = getConfigString(pathPrefix + ".description");
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
                String pathPrefix = "kits." + key;

                name = getConfigString(pathPrefix + ".name");
                description1 = getConfigString(pathPrefix + ".description1");
                description2 = getConfigString(pathPrefix + ".description2");
                description3 = getConfigString(pathPrefix + ".description3");
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
                String pathPrefix = "kits." + key + ".items";

                String temp;
                try {
                    temp = getConfigString(pathPrefix + ".boots");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("tailor"))
                        throw e;
                    else temp = null;
                }
                boots = temp;

                try {
                    temp = getConfigString(pathPrefix + ".carrot");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("farmer"))
                        throw e;
                    else temp = null;
                }
                carrot = temp;

                try {
                    temp = getConfigString(pathPrefix + ".chestplate");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("tailor"))
                        throw e;
                    else temp = null;
                }
                chestplate = temp;

                try {
                    temp = getConfigString(pathPrefix + ".club");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("orc"))
                        throw e;
                    else temp = null;
                }
                club = temp;

                try {
                    temp = getConfigString(pathPrefix + ".golem");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("summoner"))
                        throw e;
                    else temp = null;
                }
                golem = temp;

                try {
                    temp = getConfigString(pathPrefix + ".health");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("alchemist"))
                        throw e;
                    else temp = null;
                }
                health = temp;

                try {
                    temp = getConfigString(pathPrefix + ".helmet");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("tailor"))
                        throw e;
                    else temp = null;
                }
                helmet = temp;
                try {
                    temp = getConfigString(pathPrefix + ".leggings");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("tailor"))
                        throw e;
                    else temp = null;
                }
                leggings = temp;

                try {
                    temp = getConfigString(pathPrefix + ".scythe");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("reaper"))
                        throw e;
                    else temp = null;
                }
                scythe = temp;

                try {
                    temp = getConfigString(pathPrefix + ".speed");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("alchemist"))
                        throw e;
                    else temp = null;
                }
                speed = temp;

                try {
                    temp = getConfigString(pathPrefix + ".sword");
                } catch (InvalidLanguageKeyException e) {
                    if (key.equals("soldier"))
                        throw e;
                    else temp = null;
                }
                sword = temp;

                try {
                    temp = getConfigString(pathPrefix + ".wolf");
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
        public final @NotNull String available;
        public final @NotNull String caution;
        public final @NotNull String challenges;
        public final @NotNull String closed;
        public final @NotNull String commandFormat;
        public final @NotNull String consumable;
        public final @NotNull String crystalBonus;
        public final @NotNull String crystalBalance;
        public final @NotNull String crystalsEarned;
        public final @NotNull String customShopInv;
        public final @NotNull String death;
        public final @NotNull String death1;
        public final @NotNull String death2;
        public final @NotNull String debugLevelSet;
        public final @NotNull String disabled;
        public final @NotNull String earnedGems;
        public final @NotNull String effectKitsDescription;
        public final @NotNull String enchantInstruction;
        public final @NotNull String end;
        public final @NotNull String ending;
        public final @NotNull String enemies;
        public final @NotNull String exit;
        public final @NotNull String free;
        public final @NotNull String foundGems;
        public final @NotNull String gameOver;
        public final @NotNull String gems;
        public final @NotNull String gemsReceived;
        public final @NotNull String ghosts;
        public final @NotNull String giftKitsDescription;
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
        public final @NotNull String level;
        public final @NotNull String manualUpdateWarn;
        public final @NotNull String maxCapacity;
        public final @NotNull String minutesLeft;
        public final @NotNull String noAutoUpdate;
        public final @NotNull String noStats;
        public final @NotNull String offToggle;
        public final @NotNull String onToggle;
        public final @NotNull String oneMinuteWarning;
        public final @NotNull String playerKits;
        public final @NotNull String players;
        public final @NotNull String playerStatistics;
        public final @NotNull String purchase;
        public final @NotNull String purchased;
        public final @NotNull String record;
        public final @NotNull String records;
        public final @NotNull String restartPlugin;
        public final @NotNull String rightClick;
        public final @NotNull String secondsLeft;
        public final @NotNull String shopInfo;
        public final @NotNull String shopUpgrade;
        public final @NotNull String spectators;
        public final @NotNull String starting;
        public final @NotNull String upToAbilityLevel;
        public final @NotNull String waveNum;
        public final @NotNull String weapon;
        public final @NotNull String unavailable;
        public final @NotNull String unlimited;
        public final @NotNull String visitWiki;
        public final @NotNull String villagers;
        public final @NotNull String waiting;
        public final @NotNull String waitingForPlayers;
        public final @NotNull String warning;
        public final @NotNull String wave;

        private Messages() throws InvalidLanguageKeyException {
            String pathPrefix = "messages";

            abilityKitsDescription = getConfigString(pathPrefix + ".abilityKitsDescription");
            abilityLevel = getConfigString(pathPrefix + ".abilityLevel");
            achievements = getConfigString(pathPrefix + ".achievements");
            allowedKits = getConfigString(pathPrefix + ".allowedKits");
            arenaInfo = getConfigString(pathPrefix + ".arenaInfo");
            arenaRecords = getConfigString(pathPrefix + ".arenaRecords");
            armor = getConfigString(pathPrefix + ".armor");
            available = getConfigString(pathPrefix + ".available");
            caution = getConfigString(pathPrefix + ".caution");
            challenges = getConfigString(pathPrefix + ".challenges");
            closed = getConfigString(pathPrefix + ".closed");
            commandFormat = getConfigString(pathPrefix + ".commandFormat");
            consumable = getConfigString(pathPrefix + ".consumable");
            crystalBonus = getConfigString(pathPrefix + ".crystalBonus");
            crystalBalance = getConfigString(pathPrefix + ".crystalBalance");
            crystalsEarned = getConfigString(pathPrefix + ".crystalsEarned");
            customShopInv = getConfigString(pathPrefix + ".customShopInv");
            death = getConfigString(pathPrefix + ".death");
            death1 = getConfigString(pathPrefix + ".death1");
            death2 = getConfigString(pathPrefix + ".death2");
            debugLevelSet = getConfigString(pathPrefix + ".debugLevelSet");
            disabled = getConfigString(pathPrefix + ".disabled");
            earnedGems = getConfigString(pathPrefix + ".earnedGems");
            effectKitsDescription = getConfigString(pathPrefix + ".effectKitsDescription");
            enchantInstruction = getConfigString(pathPrefix + ".enchantInstruction");
            end = getConfigString(pathPrefix + ".end");
            ending = getConfigString(pathPrefix + ".ending");
            enemies = getConfigString(pathPrefix + ".enemies");
            exit = getConfigString(pathPrefix + ".exit");
            free = getConfigString(pathPrefix + ".free");
            foundGems = getConfigString(pathPrefix + ".foundGems");
            gameOver = getConfigString(pathPrefix + ".gameOver");
            gems = getConfigString(pathPrefix + ".gems");
            gemsReceived = getConfigString(pathPrefix + ".gemsReceived");
            ghosts = getConfigString(pathPrefix + ".ghosts");
            giftKitsDescription = getConfigString(pathPrefix + ".giftKitsDescription");
            infoAboutWiki = getConfigString(pathPrefix + ".infoAboutWiki");
            info1 = getConfigString(pathPrefix + ".info1");
            info2 = getConfigString(pathPrefix + ".info2");
            info3 = getConfigString(pathPrefix + ".info3");
            info4 = getConfigString(pathPrefix + ".info4");
            info5 = getConfigString(pathPrefix + ".info5");
            info6 = getConfigString(pathPrefix + ".info6");
            itemShopDesc = getConfigString(pathPrefix + ".itemShopDesc");
            join = getConfigString(pathPrefix + ".join");
            kills = getConfigString(pathPrefix + ".kills");
            kit = getConfigString(pathPrefix + ".kit");
            kits = getConfigString(pathPrefix + ".kits");
            late = getConfigString(pathPrefix + ".late");
            leave = getConfigString(pathPrefix + ".leave");
            leaveArena = getConfigString(pathPrefix + ".leaveArena");
            level = getConfigString(pathPrefix + ".level");
            manualUpdateWarn = getConfigString(pathPrefix + ".manualUpdateWarn");
            maxCapacity = getConfigString(pathPrefix + ".maxCapacity");
            minutesLeft = getConfigString(pathPrefix + ".minutesLeft");
            noAutoUpdate = getConfigString(pathPrefix + ".noAutoUpdate");
            noStats = getConfigString(pathPrefix + ".noStats");
            offToggle = getConfigString(pathPrefix + ".offToggle");
            onToggle = getConfigString(pathPrefix + ".onToggle");
            oneMinuteWarning = getConfigString(pathPrefix + ".oneMinuteWarning");
            playerKits = getConfigString(pathPrefix + ".playerKits");
            players = getConfigString(pathPrefix + ".players");
            playerStatistics = getConfigString(pathPrefix + ".playerStatistics");
            purchase = getConfigString(pathPrefix + ".purchase");
            purchased = getConfigString(pathPrefix + ".purchased");
            record = getConfigString(pathPrefix + ".record");
            records = getConfigString(pathPrefix + ".records");
            restartPlugin = getConfigString(pathPrefix + ".restartPlugin");
            rightClick = getConfigString(pathPrefix + ".rightClick");
            secondsLeft = getConfigString(pathPrefix + ".secondsLeft");
            shopInfo = getConfigString(pathPrefix + ".shopInfo");
            shopUpgrade = getConfigString(pathPrefix + ".shopUpgrade");
            spectators = getConfigString(pathPrefix + ".spectators");
            starting = getConfigString(pathPrefix + ".starting");
            upToAbilityLevel = getConfigString(pathPrefix + ".upToAbilityLevel");
            waveNum = getConfigString(pathPrefix + ".waveNum");
            weapon = getConfigString(pathPrefix + ".weapon");
            unavailable = getConfigString(pathPrefix + ".unavailable");
            unlimited = getConfigString(pathPrefix + ".unlimited");
            visitWiki = getConfigString(pathPrefix + ".visitWiki");
            villagers = getConfigString(pathPrefix + ".villagers");
            waiting = getConfigString(pathPrefix + ".waiting");
            waitingForPlayers = getConfigString(pathPrefix + ".waitingForPlayers");
            warning = getConfigString(pathPrefix + ".warning");
            wave = getConfigString(pathPrefix + ".wave");
        }
    }

    public static class Names extends Section {
        public final @NotNull String abilityKits;
        public final @NotNull String armorShop;
        public final @NotNull String carePackageExtra;
        public final @NotNull String carePackageLarge;
        public final @NotNull String carePackageMedium;
        public final @NotNull String carePackageSmall;
        public final @NotNull String challengeSelection;
        public final @NotNull String communityChest;
        public final @NotNull String consumableShop;
        public final @NotNull String contents;
        public final @NotNull String crystals;
        public final @NotNull String customShop;
        public final @NotNull String defaultShop;
        public final @NotNull String easy;
        public final @NotNull String effectKits;
        public final @NotNull String enchantBook;
        public final @NotNull String enchantShop;
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
        public final @NotNull String playerSpawnParticles;
        public final @NotNull String timeBar;
        public final @NotNull String villagerSpawnParticles;
        public final @NotNull String weaponShop;

        private Names() throws InvalidLanguageKeyException {
            String pathPrefix = "names";

            abilityKits = getConfigString(pathPrefix + ".abilityKits");
            armorShop = getConfigString(pathPrefix + ".armorShop");
            carePackageExtra = getConfigString(pathPrefix + ".carePackageExtra");
            carePackageLarge = getConfigString(pathPrefix + ".carePackageLarge");
            carePackageMedium = getConfigString(pathPrefix + ".carePackageMedium");
            carePackageSmall = getConfigString(pathPrefix + ".carePackageSmall");
            challengeSelection = getConfigString(pathPrefix + ".challengeSelection");
            communityChest = getConfigString(pathPrefix + ".communityChest");
            consumableShop = getConfigString(pathPrefix + ".consumableShop");
            contents = getConfigString(pathPrefix + ".contents");
            crystals = getConfigString(pathPrefix + ".crystals");
            customShop = getConfigString(pathPrefix + ".customShop");
            defaultShop = getConfigString(pathPrefix + ".defaultShop");
            easy = getConfigString(pathPrefix + ".easy");
            effectKits = getConfigString(pathPrefix + ".effectKits");
            enchantBook = getConfigString(pathPrefix + ".enchantBook");
            enchantShop = getConfigString(pathPrefix + ".enchantShop");
            essence = getConfigString(pathPrefix + ".essence");
            giftKits = getConfigString(pathPrefix + ".giftKits");
            golemEgg = getConfigString(pathPrefix + ".golemEgg");
            hard = getConfigString(pathPrefix + ".hard");
            insane = getConfigString(pathPrefix + ".insane");
            itemShop = getConfigString(pathPrefix + ".itemShop");
            kitSelection = getConfigString(pathPrefix + ".kitSelection");
            medium = getConfigString(pathPrefix + ".medium");
            monsterSpawnParticles = getConfigString(pathPrefix + ".monsterSpawnParticles");
            none = getConfigString(pathPrefix + ".none");
            playerSpawnParticles = getConfigString(pathPrefix + ".playerSpawnParticles");
            timeBar = getConfigString(pathPrefix + ".timeBar");
            villagerSpawnParticles = getConfigString(pathPrefix + ".villagerSpawnParticles");
            weaponShop = getConfigString(pathPrefix + ".weaponShop");
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
                String pathPrefix = "playerStats." + key;

                name = getConfigString(pathPrefix + ".name");
                description = getConfigString(pathPrefix + ".description");
                leaderboard = getConfigString(pathPrefix + ".leaderboard");
            }
        }
    }
}
