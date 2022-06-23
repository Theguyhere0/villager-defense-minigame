package me.theguyhere.villagerdefense.plugin.game.models.kits;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.models.GameItems;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.*;

/**
 * A class representing kits in Villager Defense. Comes with static methods for the following kits:<br/>
 * <br/><b>Gift Kits:</b><ul>
 *     <li>Orc</li>
 *     <li>Farmer</li>
 *     <li>Soldier</li>
 *     <li>Tailor</li>
 *     <li>Alchemist</li>
 *     <li>Trader</li>
 *     <li>Summoner</li>
 *     <li>Reaper</li>
 *     <li>Phantom</li>
 * </ul>
 * <b>Ability Kits:</b><ul>
 *     <li>Mage</li>
 *     <li>Ninja</li>
 *     <li>Templar</li>
 *     <li>Warrior</li>
 *     <li>Knight</li>
 *     <li>Priest</li>
 *     <li>Siren</li>
 *     <li>Monk</li>
 *     <li>Messenger</li>
 * </ul>
 * <b>Effect Kits:</b><ul>
 *     <li>Blacksmith</li>
 *     <li>Witch</li>
 *     <li>Merchant</li>
 *     <li>Vampire</li>
 *     <li>Giant</li>
 * </ul>
 */
public class Kit {
    /** The name of the kit.*/
    private final String name;
    /** The type of kit.*/
    private final KitType kitType;
    /** The ID of the kit.*/
    private final String ID;
    /** The main description for the kit.*/
    private List<String> masterDescription = new ArrayList<>();
    /** A mapping between kit level and kit description.*/
    private final Map<Integer, List<String>> descriptionsMap = new LinkedHashMap<>();
    /** The material used for GUI buttons relating to this kit.*/
    private final Material buttonMaterial;
    /** A mapping between kit level and purchase price.*/
    private final Map<Integer, Integer> pricesMap = new HashMap<>();
    /** A mapping between kit level and an array of {@link ItemStack} the player would receive.*/
    private final Map<Integer, ItemStack[]> itemsMap = new HashMap<>();
    /** The level of this instance of the kit.*/
    private int level;

    public Kit(String name, KitType kitType, String ID, Material buttonMaterial) {
        this.name = name;
        this.kitType = kitType;
        this.ID = ID;
        this.buttonMaterial = buttonMaterial;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public int getLevel() {
        return level;
    }

    public boolean nameCompare(Kit kit) {
        return kit != null && name.equals(kit.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kit kit = (Kit) o;
        return level == kit.level && Objects.equals(name, kit.name) && kitType == kit.kitType &&
                Objects.equals(ID, kit.ID) && Objects.equals(masterDescription, kit.masterDescription) &&
                Objects.equals(descriptionsMap, kit.descriptionsMap) && buttonMaterial == kit.buttonMaterial &&
                Objects.equals(pricesMap, kit.pricesMap) && Objects.equals(itemsMap, kit.itemsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, kitType, ID, masterDescription, descriptionsMap, buttonMaterial, pricesMap, itemsMap,
                level);
    }

    /**
     * Returns the highest level this kit goes to.
     * @return Highest level.
     */
    public int getMaxLevel() {
        return pricesMap.size();
    }

    /**
     * Checks if this kit has multiple levels.
     * @return Whether the kit has multiple levels.
     */
    public boolean isMultiLevel() {
        return pricesMap.size() > 1;
    }

    /**
     * Returns the proper kit color code.
     * @param kitType Type of kit.
     * @return Color code.
     */
    private String getKitColor(KitType kitType) {
        switch (kitType) {
            case NONE: return "&f&l";
            case GIFT: return "&a&l";
            case ABILITY: return "&d&l";
            case EFFECT: return "&e&l";
        }
        return null;
    }

    /**
     * Sets the descriptions map to a common map used for ability kits.
     */
    private void setAbilityKitDescriptions() {
        ColoredMessage coloredAbilityText = new ColoredMessage(ChatColor.GRAY,
                LanguageManager.messages.upToAbilityLevel);
        addLevelDescriptions(1, CommunicationManager.format(coloredAbilityText, "10"));
        addLevelDescriptions(2, CommunicationManager.format(coloredAbilityText, "20"));
        addLevelDescriptions(3, CommunicationManager.format(coloredAbilityText, "30"));
    }

    /**
     * Adds a line into the master description for the kit.
     * @param line Line to add to the description.
     */
    public void addMasterDescription(String line) {
        masterDescription = CommunicationManager.formatDescriptionList(ChatColor.GRAY, line, Utils.LORE_CHAR_LIMIT);
    }

    /**
     * Adds a kit level-description pair into the descriptions map.
     * @param level Kit level.
     * @param description Kit description.
     */
    public void addLevelDescriptions(int level, String description) {
        descriptionsMap.put(level, CommunicationManager.formatDescriptionList(ChatColor.GRAY, description,
                Utils.LORE_CHAR_LIMIT));
    }

    /**
     * Returns the description of the kit at the specified level.
     * @param level Kit level.
     * @return Kit description.
     */
    public List<String> getLevelDescription(int level) {
        return descriptionsMap.get(level);
    }

    /**
     * Adds a kit level-kit price pair into the prices map.
     * @param level Kit level.
     * @param price Kit price.
     */
    public void addPrice(int level, int price) {
        pricesMap.put(level, price);
    }

    /**
     * Returns the price of the kit at the specified level, adjusting for custom economy multiplier.
     * @param level Kit level.
     * @return Kit price.
     */
    public int getPrice(int level) {
        if (pricesMap.get(level) == 0)
            return 0;
        else if (Main.hasCustomEconomy())
            return Math.max((int) (pricesMap.get(level) * Main.plugin.getConfig().getDouble("vaultEconomyMult")),
                    1);
        else return pricesMap.get(level);
    }

    /**
     * Adds a kit level-items pair into the items map.
     * @param level Kit level.
     * @param items Items to be received by the player.
     */
    public void addItems(int level, ItemStack[] items) {
        itemsMap.put(level, items);
    }

    /**
     * Returns the items a player would receive.
     * @return Items to be received by the player.
     */
    public ItemStack[] getItems() {
        if (itemsMap.containsKey(level))
            return itemsMap.get(level);
        else return itemsMap.get(1);
    }

    /**
     * Returns an {@link ItemStack} for a GUI button.<br/>
     * <br/>Use -1 as purchasedLevel if the button is for display only.
     * @param purchasedLevel The level at which the player has purchased this kit.
     * @param purchaseMode Whether the button is in purchase mode or not. When used for display only, represents
     *                     whether the kit is active or not.
     * @return GUI button.
     */
    public ItemStack getButton(int purchasedLevel, boolean purchaseMode) {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);

        if (kitType == KitType.NONE) {
            return ItemManager.createItem(buttonMaterial,
                    CommunicationManager.format(getKitColor(kitType) + name), ItemManager.BUTTON_FLAGS,
                    null);
        }
        else if (isMultiLevel()) {
            List<String> lores = new ArrayList<>();
            if (purchasedLevel == 0) {
                lores.addAll(masterDescription);
                lores.add(CommunicationManager.format("&c" + LanguageManager.messages.level + " 1"));
                lores.addAll(getLevelDescription(1));
                lores.add(purchaseMode ? CommunicationManager.format("&c" +
                        LanguageManager.messages.purchase + ": &b" + getPrice(1) + " " +
                        LanguageManager.names.crystals) : CommunicationManager.format(ChatColor.RED +
                        LanguageManager.messages.unavailable));
            }
            else if (purchasedLevel == pricesMap.size()) {
                lores.addAll(masterDescription);
                lores.add(CommunicationManager.format("&a" + LanguageManager.messages.level + " " +
                        pricesMap.size()));
                lores.addAll(getLevelDescription(pricesMap.size()));
                lores.add(purchaseMode ? CommunicationManager.format(ChatColor.GREEN +
                        LanguageManager.messages.purchased) : CommunicationManager.format(ChatColor.GREEN +
                        LanguageManager.messages.available));
            }
            else if (purchasedLevel == -1) {
                lores.addAll(masterDescription);
                descriptionsMap.forEach((level, description) -> {
                    lores.add(CommunicationManager.format("&f" + LanguageManager.messages.level + " " + level));
                    lores.addAll(description);
                });
                return ItemManager.createItem(buttonMaterial,
                        CommunicationManager.format((purchaseMode ? getKitColor(kitType): "&4&l") + name),
                        ItemManager.BUTTON_FLAGS, purchaseMode ? enchants : null, lores);
            }
            else {
                lores.addAll(masterDescription);
                lores.add(CommunicationManager.format("&a" + LanguageManager.messages.level + " " +
                        purchasedLevel));
                lores.addAll(getLevelDescription(purchasedLevel));
                if (purchaseMode) {
                    lores.add(CommunicationManager.format("&c" + LanguageManager.messages.level +
                            " " + ++purchasedLevel));
                    lores.addAll(getLevelDescription(purchasedLevel));
                    lores.add(CommunicationManager.format("&c" +
                            LanguageManager.messages.purchase + ": &b" + getPrice(purchasedLevel) +
                            " " + LanguageManager.names.crystals));
                } else lores.add(CommunicationManager.format(ChatColor.GREEN +
                        LanguageManager.messages.available));
            }

            return ItemManager.createItem(buttonMaterial,
                    CommunicationManager.format(getKitColor(kitType) + name), ItemManager.BUTTON_FLAGS,
                    null, lores);
        } else {
            if (purchasedLevel == -1)
                return ItemManager.createItem(buttonMaterial,
                        CommunicationManager.format((purchaseMode ? getKitColor(kitType): "&4&l") + name),
                        ItemManager.BUTTON_FLAGS, purchaseMode ? enchants : null, masterDescription);
            else if (pricesMap.get(1) == 0)
                return ItemManager.createItem(buttonMaterial,
                        CommunicationManager.format(getKitColor(kitType) + name), ItemManager.BUTTON_FLAGS,
                        null, masterDescription, purchaseMode ?
                                CommunicationManager.format(ChatColor.GREEN + LanguageManager.messages.free) :
                                CommunicationManager.format(ChatColor.GREEN + LanguageManager.messages.available));
            else return ItemManager.createItem(buttonMaterial,
                    CommunicationManager.format(getKitColor(kitType) + name), ItemManager.BUTTON_FLAGS,
                        null, masterDescription, purchasedLevel == 1 ?
                                (purchaseMode ?
                                        CommunicationManager.format(ChatColor.GREEN +
                                                LanguageManager.messages.purchased) :
                                        CommunicationManager.format(ChatColor.GREEN +
                                                LanguageManager.messages.available)) :
                                (purchaseMode ? CommunicationManager.format("&c" +
                                        LanguageManager.messages.purchase + ": &b" +
                                        getPrice(1) + " " + LanguageManager.names.crystals) :
                                        CommunicationManager.format(ChatColor.RED +
                                                LanguageManager.messages.unavailable)));
        }
    }

    /**
     * Sets the kit level while returning the same kit.
     * @param level Kit level.
     * @return This kit.
     */
    public Kit setKitLevel(int level) {
        this.level = level;
        return this;
    }

    /**
     * Attempts to return a {@link Kit} based on the kit's name.
     * @param kitName Name to check.
     * @return Kit or null.
     */
    public static Kit getKitByName(String kitName) {
        if (none().getName().equals(kitName))
            return none();
        else if (orc().getName().equals(kitName))
            return orc();
        else if (farmer().getName().equals(kitName))
            return farmer();
        else if (soldier().getName().equals(kitName))
            return soldier();
        else if (alchemist().getName().equals(kitName))
            return alchemist();
        else if (tailor().getName().equals(kitName))
            return tailor();
        else if (trader().getName().equals(kitName))
            return trader();
        else if (summoner().getName().equals(kitName))
            return summoner();
        else if (reaper().getName().equals(kitName))
            return reaper();
        else if (phantom().getName().equals(kitName))
            return phantom();
        else if (mage().getName().equals(kitName))
            return mage();
        else if (ninja().getName().equals(kitName))
            return ninja();
        else if (templar().getName().equals(kitName))
            return templar();
        else if (warrior().getName().equals(kitName))
            return warrior();
        else if (knight().getName().equals(kitName))
            return knight();
        else if (priest().getName().equals(kitName))
            return priest();
        else if (siren().getName().equals(kitName))
            return siren();
        else if (monk().getName().equals(kitName))
            return monk();
        else if (messenger().getName().equals(kitName))
            return messenger();
        else if (blacksmith().getName().equals(kitName))
            return blacksmith();
        else if (witch().getName().equals(kitName))
            return witch();
        else if (merchant().getName().equals(kitName))
            return merchant();
        else if (vampire().getName().equals(kitName))
            return vampire();
        else if (giant().getName().equals(kitName))
            return giant();
        else return null;
    }

    /**
     * Attempts to return a {@link Kit} based on the kit's ID.
     * @param kitID ID to check.
     * @return Kit or null.
     */
    public static Kit getKitByID(String kitID) {
        if (none().getID().equals(kitID))
            return none();
        else if (orc().getID().equals(kitID))
            return orc();
        else if (farmer().getID().equals(kitID))
            return farmer();
        else if (soldier().getID().equals(kitID))
            return soldier();
        else if (alchemist().getID().equals(kitID))
            return alchemist();
        else if (tailor().getID().equals(kitID))
            return tailor();
        else if (trader().getID().equals(kitID))
            return trader();
        else if (summoner().getID().equals(kitID))
            return summoner();
        else if (reaper().getID().equals(kitID))
            return reaper();
        else if (phantom().getID().equals(kitID))
            return phantom();
        else if (mage().getID().equals(kitID))
            return mage();
        else if (ninja().getID().equals(kitID))
            return ninja();
        else if (templar().getID().equals(kitID))
            return templar();
        else if (warrior().getID().equals(kitID))
            return warrior();
        else if (knight().getID().equals(kitID))
            return knight();
        else if (priest().getID().equals(kitID))
            return priest();
        else if (siren().getID().equals(kitID))
            return siren();
        else if (monk().getID().equals(kitID))
            return monk();
        else if (messenger().getID().equals(kitID))
            return messenger();
        else if (blacksmith().getID().equals(kitID))
            return blacksmith();
        else if (witch().getID().equals(kitID))
            return witch();
        else if (merchant().getID().equals(kitID))
            return merchant();
        else if (vampire().getID().equals(kitID))
            return vampire();
        else if (giant().getID().equals(kitID))
            return giant();
        else return null;
    }

    // Default Kit
    public static Kit none() {
        Kit kit = new Kit(LanguageManager.names.none, KitType.NONE, "none", Material.LIGHT_GRAY_CONCRETE);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }

    // Gift Kits
    public static Kit orc() {
        Kit kit = new Kit(LanguageManager.kits.orc.name, KitType.GIFT, "orc", Material.STICK);
        kit.addMasterDescription(LanguageManager.kits.orc.description);
        kit.addPrice(1, 0);

        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.KNOCKBACK, 5);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItem(Material.STICK, new ColoredMessage(ChatColor.GREEN,
                        LanguageManager.kits.orc.items.club).toString(), ItemManager.NORMAL_FLAGS, enchants)});
        return kit;
    }
    public static Kit farmer() {
        Kit kit = new Kit(LanguageManager.kits.farmer.name, KitType.GIFT, "farmer", Material.CARROT);
        kit.addMasterDescription(LanguageManager.kits.farmer.description);
        kit.addPrice(1, 0);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItems(Material.CARROT, 5, new ColoredMessage(ChatColor.GREEN,
                        LanguageManager.kits.farmer.items.carrot).toString())});
        return kit;
    }
    public static Kit soldier() {
        Kit kit = new Kit(LanguageManager.kits.soldier.name, KitType.GIFT, "soldier", Material.STONE_SWORD);
        kit.addMasterDescription(LanguageManager.kits.soldier.description);
        kit.addPrice(1, 250);
        kit.addItems(1, new ItemStack[]{
                ItemManager.createItem(Material.STONE_SWORD, new ColoredMessage(ChatColor.GREEN,
                        LanguageManager.kits.soldier.items.sword).toString())});
        return kit;
    }
    public static Kit alchemist() {
        Kit kit = new Kit(LanguageManager.kits.alchemist.name, KitType.GIFT, "alchemist", Material.BREWING_STAND);
        kit.addMasterDescription(LanguageManager.kits.alchemist.description);
        kit.addPrice(1, 300);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.SPEED),
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.alchemist.items.speed).toString()),
                ItemManager.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.INSTANT_HEAL),
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.alchemist.items.health).toString()),
                ItemManager.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.INSTANT_HEAL),
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.alchemist.items.health).toString())
        });
        return kit;
    }
    public static Kit tailor() {
        Kit kit = new Kit(LanguageManager.kits.tailor.name, KitType.GIFT, "tailor", Material.LEATHER_CHESTPLATE);
        kit.addMasterDescription(CommunicationManager.format(LanguageManager.kits.tailor.description));
        kit.addPrice(1, 400);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItem(Material.LEATHER_HELMET,
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.tailor.items.helmet).toString()),
                ItemManager.createItem(Material.LEATHER_CHESTPLATE,
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.tailor.items.chestplate).toString()),
                ItemManager.createItem(Material.LEATHER_LEGGINGS,
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.tailor.items.leggings).toString()),
                ItemManager.createItem(Material.LEATHER_BOOTS,
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.tailor.items.boots).toString())
        });
        return kit;
    }
    public static Kit trader() {
        Kit kit = new Kit(LanguageManager.kits.trader.name, KitType.GIFT, "trader", Material.EMERALD);
        kit.addMasterDescription(CommunicationManager.format(LanguageManager.kits.trader.description));
        kit.addPrice(1, 500);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit summoner() {
        Kit kit = new Kit(LanguageManager.kits.summoner.name, KitType.GIFT, "summoner",
                Material.POLAR_BEAR_SPAWN_EGG);

        kit.addLevelDescriptions(1, LanguageManager.kits.summoner.description1);
        kit.addLevelDescriptions(2, LanguageManager.kits.summoner.description2);
        kit.addLevelDescriptions(3, LanguageManager.kits.summoner.description3);

        kit.addPrice(1, 750);
        kit.addPrice(2, 1750);
        kit.addPrice(3, 4500);

        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItem(Material.WOLF_SPAWN_EGG,
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.summoner.items.wolf).toString())
        });
        kit.addItems(2, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItems(Material.WOLF_SPAWN_EGG, 2,
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.summoner.items.wolf).toString())
        });
        kit.addItems(3, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItem(Material.GHAST_SPAWN_EGG,
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.summoner.items.golem).toString())
        });

        return kit;
    }
    public static Kit reaper() {
        Kit kit = new Kit(LanguageManager.kits.reaper.name, KitType.GIFT, "reaper", Material.NETHERITE_HOE);

        kit.addLevelDescriptions(1, String.format(LanguageManager.kits.reaper.description, "III"));
        kit.addLevelDescriptions(2, String.format(LanguageManager.kits.reaper.description, "V"));
        kit.addLevelDescriptions(3, String.format(LanguageManager.kits.reaper.description, "VIII"));

        kit.addPrice(1, 750);
        kit.addPrice(2, 2000);
        kit.addPrice(3, 4000);

        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DAMAGE_ALL, 3);
        kit.addItems(1, new ItemStack[]{ItemManager.createItem(
                Material.NETHERITE_HOE,
                new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.reaper.items.scythe).toString(),
                ItemManager.NORMAL_FLAGS,
                enchants
        )});
        enchants.put(Enchantment.DAMAGE_ALL, 5);
        kit.addItems(2, new ItemStack[]{ItemManager.createItem(
                Material.NETHERITE_HOE,
                new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.reaper.items.scythe).toString(),
                ItemManager.NORMAL_FLAGS,
                enchants
        )});
        enchants.put(Enchantment.DAMAGE_ALL, 8);
        kit.addItems(3, new ItemStack[]{ItemManager.createItem(
                Material.NETHERITE_HOE,
                new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.reaper.items.scythe).toString(),
                ItemManager.NORMAL_FLAGS,
                enchants
        )});

        return kit;
    }
    public static Kit phantom() {
        Kit kit = new Kit(LanguageManager.kits.phantom.name, KitType.GIFT, "phantom", Material.PHANTOM_MEMBRANE);
        kit.addMasterDescription(CommunicationManager.format(
                new ColoredMessage(ChatColor.GRAY, LanguageManager.kits.phantom.description),
                "/vd select")
        );
        kit.addPrice(1, 6000);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }

    // Ability Kits
    public static Kit mage() {
        Kit kit = new Kit(LanguageManager.kits.mage.name, KitType.ABILITY, "mage", Material.FIRE_CHARGE);

        kit.addMasterDescription(LanguageManager.kits.mage.description);
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 3500);
        kit.addPrice(2, 7500);
        kit.addPrice(3, 13000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.mage()});

        return kit;
    }
    public static Kit ninja() {
        Kit kit = new Kit(LanguageManager.kits.ninja.name, KitType.ABILITY, "ninja", Material.CHAIN);

        kit.addMasterDescription(LanguageManager.kits.ninja.description);
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4000);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 14000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.ninja()});

        return kit;
    }
    public static Kit templar() {
        Kit kit = new Kit(LanguageManager.kits.templar.name, KitType.ABILITY, "templar", Material.GOLDEN_SWORD);

        kit.addMasterDescription(LanguageManager.kits.templar.description);
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 3500);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 12500);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.templar()});

        return kit;
    }
    public static Kit warrior() {
        Kit kit = new Kit(LanguageManager.kits.warrior.name, KitType.ABILITY, "warrior",
                Material.NETHERITE_HELMET);

        kit.addMasterDescription(LanguageManager.kits.warrior.description);
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 5000);
        kit.addPrice(2, 9000);
        kit.addPrice(3, 14000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.warrior()});

        return kit;
    }
    public static Kit knight() {
        Kit kit = new Kit(LanguageManager.kits.knight.name, KitType.ABILITY, "knight", Material.SHIELD);

        kit.addMasterDescription(LanguageManager.kits.knight.description);
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4500);
        kit.addPrice(2, 8500);
        kit.addPrice(3, 13000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.knight()});

        return kit;
    }
    public static Kit priest() {
        Kit kit = new Kit(LanguageManager.kits.priest.name, KitType.ABILITY, "priest",
                Material.TOTEM_OF_UNDYING);

        kit.addMasterDescription(LanguageManager.kits.priest.description);
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 5000);
        kit.addPrice(2, 9000);
        kit.addPrice(3, 15000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.priest()});

        return kit;
    }
    public static Kit siren() {
        Kit kit = new Kit(LanguageManager.kits.siren.name, KitType.ABILITY, "siren", Material.COBWEB);

        kit.addMasterDescription(LanguageManager.kits.siren.description);
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4000);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 13500);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.siren()});

        return kit;
    }
    public static Kit monk() {
        Kit kit = new Kit(LanguageManager.kits.monk.name, KitType.ABILITY, "monk", Material.BELL);

        kit.addMasterDescription(LanguageManager.kits.monk.description);
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 3000);
        kit.addPrice(2, 7000);
        kit.addPrice(3, 11000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.monk()});

        return kit;
    }
    public static Kit messenger() {
        Kit kit = new Kit(LanguageManager.kits.messenger.name, KitType.ABILITY, "messenger", Material.FEATHER);

        kit.addMasterDescription(LanguageManager.kits.messenger.description);
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4000);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 12000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.messenger()});

        return kit;
    }

    // Effect Kits
    public static Kit blacksmith() {
        Kit kit = new Kit(LanguageManager.kits.blacksmith.name, KitType.EFFECT, "blacksmith", Material.ANVIL);
        kit.addMasterDescription(LanguageManager.kits.blacksmith.description);
        kit.addPrice(1, 7500);
        kit.addItems(1, new ItemStack[]{ItemManager.makeUnbreakable(new ItemStack(Material.WOODEN_SWORD))});
        return kit;
    }
    public static Kit witch() {
        Kit kit = new Kit(LanguageManager.kits.witch.name, KitType.EFFECT, "witch", Material.CAULDRON);
        kit.addMasterDescription(LanguageManager.kits.witch.description);
        kit.addPrice(1, 2500);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit merchant() {
        Kit kit = new Kit(LanguageManager.kits.merchant.name, KitType.EFFECT, "merchant", Material.EMERALD_BLOCK);
        kit.addMasterDescription(String.format(LanguageManager.kits.merchant.description, "10%"));
        kit.addPrice(1, 4000);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit vampire() {
        Kit kit = new Kit(LanguageManager.kits.vampire.name, KitType.EFFECT, "vampire", Material.GHAST_TEAR);
        kit.addMasterDescription(LanguageManager.kits.vampire.description);
        kit.addPrice(1, 6000);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit giant() {
        Kit kit = new Kit(LanguageManager.kits.giant.name, KitType.EFFECT, "giant", Material.DARK_OAK_SAPLING);

        kit.addLevelDescriptions(1, String.format(LanguageManager.kits.giant.description, "10%"));
        kit.addLevelDescriptions(2, String.format(LanguageManager.kits.giant.description, "20%"));

        kit.addPrice(1, 5000);
        kit.addPrice(2, 8000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});

        return kit;
    }

    public static Kit randomKit() {
        Random random = new Random();
        switch(random.nextInt(23)) {
            case 0: return orc();
            case 1: return farmer();
            case 2: return soldier();
            case 3: return tailor();
            case 4: return alchemist();
            case 5: return trader();
            case 6: return summoner();
            case 7: return reaper();
            case 8: return phantom();
            case 9: return mage();
            case 10: return ninja();
            case 11: return templar();
            case 12: return warrior();
            case 13: return knight();
            case 14: return priest();
            case 15: return siren();
            case 16: return monk();
            case 17: return messenger();
            case 18: return blacksmith();
            case 19: return witch();
            case 20: return merchant();
            case 21: return vampire();
            case 22: return giant();
            default: return none();
        }
    }
}
