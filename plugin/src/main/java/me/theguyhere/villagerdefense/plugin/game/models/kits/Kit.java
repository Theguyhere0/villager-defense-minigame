package me.theguyhere.villagerdefense.plugin.game.models.kits;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.GameItems;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
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

    private static Main plugin;

    public Kit(String name, KitType kitType, Material buttonMaterial) {
        this.name = name;
        this.kitType = kitType;
        this.buttonMaterial = buttonMaterial;
    }

    public static void setPlugin(Main plugin) {
        Kit.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kit kit = (Kit) o;
        return level == kit.level && Objects.equals(name, kit.name) && kitType == kit.kitType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, kitType, masterDescription, descriptionsMap, buttonMaterial, pricesMap, itemsMap,
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
        addLevelDescriptions(1, plugin.getLanguageStringFormatted("messages.upToAbilityLevel",
                CommunicationManager.format("&b10&7")));
        addLevelDescriptions(2, plugin.getLanguageStringFormatted("messages.upToAbilityLevel",
                CommunicationManager.format("&b20&7")));
        addLevelDescriptions(3, plugin.getLanguageStringFormatted("messages.upToAbilityLevel",
                CommunicationManager.format("&b30&7")));
    }

    /**
     * Adds a line into the master description for the kit.
     * @param line Line to add to the description.
     */
    public void addMasterDescription(String line) {
        masterDescription = CommunicationManager.formatDescriptionList(ChatColor.GRAY, line);
    }

    /**
     * Adds a kit level-description pair into the descriptions map.
     * @param level Kit level.
     * @param description Kit description.
     */
    public void addLevelDescriptions(int level, String description) {
        descriptionsMap.put(level, CommunicationManager.formatDescriptionList(ChatColor.GRAY, description));
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
     * Returns the price of the kit at the specified level.
     * @param level Kit level.
     * @return Kit price.
     */
    public int getPrice(int level) {
        return pricesMap.get(level);
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
                lores.add(CommunicationManager.format("&c" + plugin.getLanguageString("messages.level") +
                        " 1"));
                lores.addAll(getLevelDescription(1));
                lores.add(purchaseMode ? CommunicationManager.format("&c" +
                        plugin.getLanguageString("messages.purchase") + ": &b" + getPrice(1) + " " +
                        plugin.getLanguageString("names.crystals")) : CommunicationManager.format(
                                ChatColor.RED + plugin.getLanguageString("messages.unavailable")));
            }
            else if (purchasedLevel == pricesMap.size()) {
                lores.addAll(masterDescription);
                lores.add(CommunicationManager.format("&a" + plugin.getLanguageString("messages.level") +
                        " " + pricesMap.size()));
                lores.addAll(getLevelDescription(pricesMap.size()));
                lores.add(purchaseMode ?
                        CommunicationManager.format(ChatColor.GREEN +
                                plugin.getLanguageString("messages.purchased")) :
                        CommunicationManager.format(ChatColor.GREEN +
                                plugin.getLanguageString("messages.available")));
            }
            else if (purchasedLevel == -1) {
                lores.addAll(masterDescription);
                descriptionsMap.forEach((level, description) -> {
                    lores.add(CommunicationManager.format("&f" + plugin.getLanguageString("messages.level") +
                            " " + level));
                    lores.addAll(description);
                });
                return ItemManager.createItem(buttonMaterial,
                        CommunicationManager.format((purchaseMode ? getKitColor(kitType): "&4&l") + name),
                        ItemManager.BUTTON_FLAGS, purchaseMode ? enchants : null, lores);
            }
            else {
                lores.addAll(masterDescription);
                lores.add(CommunicationManager.format("&a" + plugin.getLanguageString("messages.level") +
                        " " + purchasedLevel));
                lores.addAll(getLevelDescription(purchasedLevel));
                if (purchaseMode) {
                    lores.add(CommunicationManager.format("&c" + plugin.getLanguageString("messages.level") +
                            " " + ++purchasedLevel));
                    lores.addAll(getLevelDescription(purchasedLevel));
                    lores.add(CommunicationManager.format("&c" +
                            plugin.getLanguageString("messages.purchase") + ": &b" + getPrice(purchasedLevel) +
                            " " + plugin.getLanguageString("names.crystals")));
                } else lores.add(CommunicationManager.format(ChatColor.GREEN +
                        plugin.getLanguageString("messages.available")));
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
                                CommunicationManager.format(ChatColor.GREEN + 
                                        plugin.getLanguageString("messages.free")) :
                                CommunicationManager.format(ChatColor.GREEN +
                                        plugin.getLanguageString("messages.available")));
            else return ItemManager.createItem(buttonMaterial,
                    CommunicationManager.format(getKitColor(kitType) + name), ItemManager.BUTTON_FLAGS,
                        null, masterDescription, purchasedLevel == 1 ?
                                (purchaseMode ?
                                        CommunicationManager.format(ChatColor.GREEN +
                                                plugin.getLanguageString("messages.purchased")) :
                                        CommunicationManager.format(ChatColor.GREEN +
                                                plugin.getLanguageString("messages.available"))) :
                                (purchaseMode ? CommunicationManager.format("&c" +
                                        plugin.getLanguageString("messages.purchase") + ": &b" + 
                                        getPrice(1) + " " + plugin.getLanguageString("names.crystals")) :
                                        CommunicationManager.format(ChatColor.RED +
                                                plugin.getLanguageString("messages.unavailable"))));
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
    public static Kit getKit(String kitName) {
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

    // Default Kit
    public static Kit none() {
        Kit kit = new Kit(plugin.getLanguageString("names.none"), KitType.NONE, Material.LIGHT_GRAY_CONCRETE);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }

    // Gift Kits
    public static Kit orc() {
        Kit kit = new Kit(plugin.getLanguageString("kits.orc.name"), KitType.GIFT, Material.STICK);
        kit.addMasterDescription(plugin.getLanguageString("kits.orc.description"));
        kit.addPrice(1, 0);

        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.KNOCKBACK, 5);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItem(Material.STICK, CommunicationManager.format(ChatColor.GREEN,
                        plugin.getLanguageString("kits.orc.items.club")), ItemManager.NORMAL_FLAGS, enchants)});
        return kit;
    }
    public static Kit farmer() {
        Kit kit = new Kit(plugin.getLanguageString("kits.farmer.name"), KitType.GIFT, Material.CARROT);
        kit.addMasterDescription(plugin.getLanguageString("kits.farmer.description"));
        kit.addPrice(1, 0);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItems(Material.CARROT, 5, CommunicationManager.format(ChatColor.GREEN,
                        plugin.getLanguageString("kits.farmer.items.carrot")))});
        return kit;
    }
    public static Kit soldier() {
        Kit kit = new Kit(plugin.getLanguageString("kits.soldier.name"), KitType.GIFT, Material.STONE_SWORD);
        kit.addMasterDescription(plugin.getLanguageString("kits.soldier.description"));
        kit.addPrice(1, 250);
        kit.addItems(1, new ItemStack[]{
                ItemManager.createItem(Material.STONE_SWORD, CommunicationManager.format(ChatColor.GREEN,
                        plugin.getLanguageString("kits.soldier.items.sword")))});
        return kit;
    }
    public static Kit alchemist() {
        Kit kit = new Kit(plugin.getLanguageString("kits.alchemist.name"), KitType.GIFT, Material.BREWING_STAND);
        kit.addMasterDescription(plugin.getLanguageString("kits.alchemist.description"));
        kit.addPrice(1, 300);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.SPEED),
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.alchemist.items.speed"))),
                ItemManager.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.INSTANT_HEAL),
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.alchemist.items.health"))),
                ItemManager.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.INSTANT_HEAL),
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.alchemist.items.health")))});
        return kit;
    }
    public static Kit tailor() {
        Kit kit = new Kit(plugin.getLanguageString("kits.tailor.name"), KitType.GIFT, Material.LEATHER_CHESTPLATE);
        kit.addMasterDescription(CommunicationManager.format(plugin.getLanguageString("kits.tailor.description")));
        kit.addPrice(1, 400);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItem(Material.LEATHER_HELMET,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.tailor.items.helmet"))),
                ItemManager.createItem(Material.LEATHER_CHESTPLATE,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.tailor.items.chestplate"))),
                ItemManager.createItem(Material.LEATHER_LEGGINGS,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.tailor.items.leggings"))),
                ItemManager.createItem(Material.LEATHER_BOOTS,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.tailor.items.boots")))});
        return kit;
    }
    public static Kit trader() {
        Kit kit = new Kit(plugin.getLanguageString("kits.trader.name"), KitType.GIFT, Material.EMERALD);
        kit.addMasterDescription(CommunicationManager.format(plugin.getLanguageString("kits.trader.description")));
        kit.addPrice(1, 500);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit summoner() {
        Kit kit = new Kit(plugin.getLanguageString("kits.summoner.name"), KitType.GIFT,
                Material.POLAR_BEAR_SPAWN_EGG);

        kit.addLevelDescriptions(1, plugin.getLanguageString("kits.summoner.description1"));
        kit.addLevelDescriptions(2, plugin.getLanguageString("kits.summoner.description2"));
        kit.addLevelDescriptions(3, plugin.getLanguageString("kits.summoner.description3"));

        kit.addPrice(1, 750);
        kit.addPrice(2, 1750);
        kit.addPrice(3, 4500);

        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItem(Material.WOLF_SPAWN_EGG,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.summoner.items.wolf")))});
        kit.addItems(2, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItems(Material.WOLF_SPAWN_EGG, 2,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.summoner.items.wolf")))
        });
        kit.addItems(3, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                ItemManager.createItem(Material.GHAST_SPAWN_EGG,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.summoner.items.golem")))});

        return kit;
    }
    public static Kit reaper() {
        Kit kit = new Kit(plugin.getLanguageString("kits.reaper.name"), KitType.GIFT, Material.NETHERITE_HOE);

        kit.addLevelDescriptions(1, plugin.getLanguageStringFormatted("kits.reaper.description",
                "III"));
        kit.addLevelDescriptions(2, plugin.getLanguageStringFormatted("kits.reaper.description",
                "V"));
        kit.addLevelDescriptions(3, plugin.getLanguageStringFormatted("kits.reaper.description",
                "VIII"));

        kit.addPrice(1, 750);
        kit.addPrice(2, 2000);
        kit.addPrice(3, 4000);

        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DAMAGE_ALL, 3);
        kit.addItems(1, new ItemStack[]{
                ItemManager.createItem(Material.NETHERITE_HOE,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.reaper.items.scythe")), ItemManager.NORMAL_FLAGS,
                        enchants)});
        enchants.put(Enchantment.DAMAGE_ALL, 5);
        kit.addItems(2, new ItemStack[]{
                ItemManager.createItem(Material.NETHERITE_HOE,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.reaper.items.scythe")), ItemManager.NORMAL_FLAGS,
                        enchants)});
        enchants.put(Enchantment.DAMAGE_ALL, 8);
        kit.addItems(3, new ItemStack[]{
                ItemManager.createItem(Material.NETHERITE_HOE,
                        CommunicationManager.format(ChatColor.GREEN,
                                plugin.getLanguageString("kits.reaper.items.scythe")), ItemManager.NORMAL_FLAGS,
                        enchants)});

        return kit;
    }
    public static Kit phantom() {
        Kit kit = new Kit(plugin.getLanguageString("kits.phantom.name"), KitType.GIFT, Material.PHANTOM_MEMBRANE);
        kit.addMasterDescription(plugin.getLanguageStringFormatted("kits.phantom.description",
                CommunicationManager.format("&b/vd select&7")));
        kit.addPrice(1, 6000);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }

    // Ability Kits
    public static Kit mage() {
        Kit kit = new Kit(plugin.getLanguageString("kits.mage.name"), KitType.ABILITY, Material.FIRE_CHARGE);

        kit.addMasterDescription(plugin.getLanguageString("kits.mage.description"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 3500);
        kit.addPrice(2, 7500);
        kit.addPrice(3, 13000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.mage()});

        return kit;
    }
    public static Kit ninja() {
        Kit kit = new Kit(plugin.getLanguageString("kits.ninja.name"), KitType.ABILITY, Material.CHAIN);

        kit.addMasterDescription(plugin.getLanguageString("kits.ninja.description"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4000);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 14000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.ninja()});

        return kit;
    }
    public static Kit templar() {
        Kit kit = new Kit(plugin.getLanguageString("kits.templar.name"), KitType.ABILITY, Material.GOLDEN_SWORD);

        kit.addMasterDescription(plugin.getLanguageString("kits.templar.description"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 3500);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 12500);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.templar()});

        return kit;
    }
    public static Kit warrior() {
        Kit kit = new Kit(plugin.getLanguageString("kits.warrior.name"), KitType.ABILITY,
                Material.NETHERITE_HELMET);

        kit.addMasterDescription(plugin.getLanguageString("kits.warrior.description"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 5000);
        kit.addPrice(2, 9000);
        kit.addPrice(3, 14000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.warrior()});

        return kit;
    }
    public static Kit knight() {
        Kit kit = new Kit(plugin.getLanguageString("kits.knight.name"), KitType.ABILITY, Material.SHIELD);

        kit.addMasterDescription(plugin.getLanguageString("kits.knight.description"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4500);
        kit.addPrice(2, 8500);
        kit.addPrice(3, 13000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.knight()});

        return kit;
    }
    public static Kit priest() {
        Kit kit = new Kit(plugin.getLanguageString("kits.priest.name"), KitType.ABILITY,
                Material.TOTEM_OF_UNDYING);

        kit.addMasterDescription(plugin.getLanguageString("kits.priest.description"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 5000);
        kit.addPrice(2, 9000);
        kit.addPrice(3, 15000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.priest()});

        return kit;
    }
    public static Kit siren() {
        Kit kit = new Kit(plugin.getLanguageString("kits.siren.name"), KitType.ABILITY, Material.COBWEB);

        kit.addMasterDescription(plugin.getLanguageString("kits.siren.description"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4000);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 13500);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.siren()});

        return kit;
    }
    public static Kit monk() {
        Kit kit = new Kit(plugin.getLanguageString("kits.monk.name"), KitType.ABILITY, Material.BELL);

        kit.addMasterDescription(plugin.getLanguageString("kits.monk.description"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 3000);
        kit.addPrice(2, 7000);
        kit.addPrice(3, 11000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.monk()});

        return kit;
    }
    public static Kit messenger() {
        Kit kit = new Kit(plugin.getLanguageString("kits.messenger.name"), KitType.ABILITY, Material.FEATHER);

        kit.addMasterDescription(plugin.getLanguageString("kits.messenger.description"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4000);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 12000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.messenger()});

        return kit;
    }

    // Effect Kits
    public static Kit blacksmith() {
        Kit kit = new Kit(plugin.getLanguageString("kits.blacksmith.name"), KitType.EFFECT, Material.ANVIL);
        kit.addMasterDescription(plugin.getLanguageString("kits.blacksmith.description"));
        kit.addPrice(1, 7500);
        kit.addItems(1, new ItemStack[]{ItemManager.makeUnbreakable(new ItemStack(Material.WOODEN_SWORD))});
        return kit;
    }
    public static Kit witch() {
        Kit kit = new Kit(plugin.getLanguageString("kits.witch.name"), KitType.EFFECT, Material.CAULDRON);
        kit.addMasterDescription(plugin.getLanguageString("kits.witch.description"));
        kit.addPrice(1, 2500);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit merchant() {
        Kit kit = new Kit(plugin.getLanguageString("kits.merchant.name"), KitType.EFFECT, Material.EMERALD_BLOCK);
        kit.addMasterDescription(plugin.getLanguageStringFormatted("kits.merchant.description", "10%"));
        kit.addPrice(1, 4000);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit vampire() {
        Kit kit = new Kit(plugin.getLanguageString("kits.vampire.name"), KitType.EFFECT, Material.GHAST_TEAR);
        kit.addMasterDescription(plugin.getLanguageString("kits.vampire.description"));
        kit.addPrice(1, 6000);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit giant() {
        Kit kit = new Kit(plugin.getLanguageString("kits.giant.name"), KitType.EFFECT, Material.DARK_OAK_SAPLING);

        kit.addLevelDescriptions(1, plugin.getLanguageStringFormatted("kits.giant.description",
                "10%"));
        kit.addLevelDescriptions(2, plugin.getLanguageStringFormatted("kits.giant.description",
                "20%"));

        kit.addPrice(1, 5000);
        kit.addPrice(2, 8000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});

        return kit;
    }
}
