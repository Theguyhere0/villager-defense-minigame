package me.theguyhere.villagerdefense.game.models.kits;

import me.theguyhere.villagerdefense.game.models.GameItems;
import me.theguyhere.villagerdefense.tools.Utils;
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
    private final List<String> masterDescription = new ArrayList<>();
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

    public Kit(String name, KitType kitType, Material buttonMaterial) {
        this.name = name;
        this.kitType = kitType;
        this.buttonMaterial = buttonMaterial;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
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
            default: return "";
        }
    }

    /**
     * Sets the descriptions map to a common map used for ability kits.
     */
    private void setAbilityKitDescriptions() {
        List<String> description = new ArrayList<>();
        description.add(Utils.format("&7Up to ability level 10"));
        addLevelDescriptions(1, description);
        description = new ArrayList<>();
        description.add(Utils.format("&7Up to ability level 20"));
        addLevelDescriptions(2, description);
        description = new ArrayList<>();
        description.add(Utils.format("&7Up to ability level 30"));
        addLevelDescriptions(3, description);
    }

    /**
     * Adds a line into the master description for the kit.
     * @param line Line to add to the description.
     */
    public void addMasterDescription(String line) {
        masterDescription.add(line);
    }

    /**
     * Adds a kit level-description pair into the descriptions map.
     * @param level Kit level.
     * @param description Kit description.
     */
    public void addLevelDescriptions(int level, List<String> description) {
        descriptionsMap.put(level, description);
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
            return Utils.createItem(buttonMaterial,
                    Utils.format(getKitColor(kitType) + name), Utils.BUTTON_FLAGS, null);
        }
        else if (isMultiLevel()) {
            List<String> lores = new ArrayList<>();
            if (purchasedLevel == 0) {
                lores.addAll(masterDescription);
                lores.add(Utils.format("&cLevel 1"));
                lores.addAll(getLevelDescription(1));
                lores.add(purchaseMode ? Utils.format("&cPurchase: &b" + getPrice(1) + " Crystals") :
                        Utils.format("&cUnavailable"));
            }
            else if (purchasedLevel == pricesMap.size()) {
//                System.out.println(pricesMap.size());
                lores.addAll(masterDescription);
                lores.add(Utils.format("&aLevel " + pricesMap.size()));
                lores.addAll(getLevelDescription(pricesMap.size()));
                lores.add(purchaseMode ? Utils.format("&aPurchased!") : Utils.format("&aAvailable"));
            }
            else if (purchasedLevel == -1) {
                lores.addAll(masterDescription);
                descriptionsMap.forEach((level, description) -> {
                    System.out.println("level " + level + " description: " + description);
                    lores.add(Utils.format("&fLevel " + level));
                    lores.addAll(description);
                });
                return Utils.createItem(buttonMaterial,
                        Utils.format(getKitColor(kitType) + name), Utils.BUTTON_FLAGS,
                        purchaseMode ? enchants : null, lores);
            }
            else {
                lores.addAll(masterDescription);
                lores.add(Utils.format("&aLevel " + purchasedLevel));
                lores.addAll(getLevelDescription(purchasedLevel));
                if (purchaseMode) {
                    lores.add(Utils.format("&cLevel " + ++purchasedLevel));
                    lores.addAll(getLevelDescription(purchasedLevel));
                    lores.add(Utils.format("&cPurchase: &b" + getPrice(purchasedLevel) + " Crystals"));
                } else lores.add(Utils.format("&aAvailable"));
            }

            return Utils.createItem(buttonMaterial,
                    Utils.format(getKitColor(kitType) + name), Utils.BUTTON_FLAGS, null, lores);
        } else {
            if (purchasedLevel == -1)
                return Utils.createItem(buttonMaterial,
                        Utils.format(getKitColor(kitType) + name), Utils.BUTTON_FLAGS,
                        purchaseMode ? enchants : null, masterDescription);
            else if (pricesMap.get(1) == 0)
                return Utils.createItem(buttonMaterial,
                        Utils.format(getKitColor(kitType) + name), Utils.BUTTON_FLAGS, null,
                        masterDescription, purchaseMode ? Utils.format("&aFree!") :
                                Utils.format("&aAvailable"));
            else return Utils.createItem(buttonMaterial,
                    Utils.format(getKitColor(kitType) + name), Utils.BUTTON_FLAGS, null,
                    masterDescription, purchasedLevel == 1 ?
                            (purchaseMode ? Utils.format("&aPurchased!") : Utils.format("&aAvailable")) :
                            (purchaseMode ? Utils.format("&cPurchase: &b" + getPrice(1) + " Crystals") :
                                    Utils.format("&cUnavailable")));
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
        Kit kit = new Kit("None", KitType.NONE, Material.LIGHT_GRAY_CONCRETE);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }

    // Gift Kits
    public static Kit orc() {
        Kit kit = new Kit("Orc", KitType.GIFT, Material.STICK);
        kit.addMasterDescription(Utils.format("&7Start with a knockback V stick"));
        kit.addPrice(1, 0);

        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.KNOCKBACK, 5);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                Utils.createItem(Material.STICK, Utils.format("&aOrc's Club"), Utils.NORMAL_FLAGS, enchants)});
        return kit;
    }
    public static Kit farmer() {
        Kit kit = new Kit("Farmer", KitType.GIFT, Material.CARROT);
        kit.addMasterDescription(Utils.format("&7Start with 5 carrots"));
        kit.addPrice(1, 0);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                Utils.createItems(Material.CARROT, 5, Utils.format("&aFarmer's Carrots"))});
        return kit;
    }
    public static Kit soldier() {
        Kit kit = new Kit("Soldier", KitType.GIFT, Material.STONE_SWORD);
        kit.addMasterDescription(Utils.format("&7Start with a stone sword"));
        kit.addPrice(1, 250);
        kit.addItems(1, new ItemStack[]{
                Utils.createItem(Material.STONE_SWORD, Utils.format("&aSoldier's Sword"))});
        return kit;
    }
    public static Kit alchemist() {
        Kit kit = new Kit("Alchemist", KitType.GIFT, Material.BREWING_STAND);
        kit.addMasterDescription(Utils.format("&7Start with 1 speed and 2 healing"));
        kit.addMasterDescription(Utils.format("&7splash potions"));
        kit.addPrice(1, 300);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                Utils.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.SPEED),
                        Utils.format("&aAlchemist's Speed Potion")),
                Utils.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.INSTANT_HEAL),
                        Utils.format("&aAlchemist's Health Potion")),
                Utils.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.INSTANT_HEAL),
                        Utils.format("&aAlchemist's Health Potion"))});
        return kit;
    }
    public static Kit tailor() {
        Kit kit = new Kit("Tailor", KitType.GIFT, Material.LEATHER_CHESTPLATE);
        kit.addMasterDescription(Utils.format("&7Start with a full leather armor set"));
        kit.addPrice(1, 400);
        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                Utils.createItem(Material.LEATHER_HELMET, Utils.format("&aTailor's Helmet")),
                Utils.createItem(Material.LEATHER_CHESTPLATE, Utils.format("&aTailor's Chestplate")),
                Utils.createItem(Material.LEATHER_LEGGINGS, Utils.format("&aTailor's Leggings")),
                Utils.createItem(Material.LEATHER_BOOTS, Utils.format("&aTailor's Boots"))});
        return kit;
    }
    public static Kit trader() {
        Kit kit = new Kit("Trader", KitType.GIFT, Material.EMERALD);
        kit.addMasterDescription(Utils.format("&7Start with 200 gems"));
        kit.addPrice(1, 500);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit summoner() {
        Kit kit = new Kit("Summoner", KitType.GIFT, Material.POLAR_BEAR_SPAWN_EGG);

        List<String> description = new ArrayList<>();
        description.add(Utils.format("&7Start with a wolf spawn"));
        kit.addLevelDescriptions(1, description);
        description = new ArrayList<>();
        description.add(Utils.format("&7Start with 2 wolf spawns"));
        kit.addLevelDescriptions(2, description);
        description = new ArrayList<>();
        description.add(Utils.format("&7Start with an iron golem spawn"));
        kit.addLevelDescriptions(3, description);

        kit.addPrice(1, 750);
        kit.addPrice(2, 1750);
        kit.addPrice(3, 4500);

        kit.addItems(1, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                Utils.createItem(Material.WOLF_SPAWN_EGG, Utils.format("&aSummoner's Wolf Spawn Egg"))});
        kit.addItems(2, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                Utils.createItems(Material.WOLF_SPAWN_EGG, 2, Utils.format("&aSummoner's Wolf Spawn Egg"))
        });
        kit.addItems(3, new ItemStack[]{
                new ItemStack(Material.WOODEN_SWORD),
                Utils.createItem(Material.GHAST_SPAWN_EGG, Utils.format("&aSummoner's Iron Golem Spawn Egg"))});

        return kit;
    }
    public static Kit reaper() {
        Kit kit = new Kit("Reaper", KitType.GIFT, Material.NETHERITE_HOE);

        List<String> description = new ArrayList<>();
        description.add(Utils.format("&7Start with a sharpness III netherite hoe"));
        kit.addLevelDescriptions(1, description);
        description = new ArrayList<>();
        description.add(Utils.format("&7Start with a sharpness V netherite hoe"));
        kit.addLevelDescriptions(2, description);
        description = new ArrayList<>();
        description.add(Utils.format("&7Start with a sharpness VIII netherite hoe"));
        kit.addLevelDescriptions(3, description);

        kit.addPrice(1, 750);
        kit.addPrice(2, 2000);
        kit.addPrice(3, 4000);

        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DAMAGE_ALL, 3);
        kit.addItems(1, new ItemStack[]{
                Utils.createItem(Material.NETHERITE_HOE, Utils.format("&aReaper's Scythe"), Utils.NORMAL_FLAGS,
                        enchants)});
        enchants.put(Enchantment.DAMAGE_ALL, 5);
        kit.addItems(2, new ItemStack[]{
                Utils.createItem(Material.NETHERITE_HOE, Utils.format("&aReaper's Scythe"), Utils.NORMAL_FLAGS,
                        enchants)});
        enchants.put(Enchantment.DAMAGE_ALL, 8);
        kit.addItems(3, new ItemStack[]{
                Utils.createItem(Material.NETHERITE_HOE, Utils.format("&aReaper's Scythe"), Utils.NORMAL_FLAGS,
                        enchants)});

        return kit;
    }
    public static Kit phantom() {
        Kit kit = new Kit("Phantom", KitType.GIFT, Material.PHANTOM_MEMBRANE);
        kit.addMasterDescription(Utils.format("&7Join as a player in any non-full game"));
        kit.addMasterDescription(Utils.format("&7using &b/vd select"));
        kit.addPrice(1, 6000);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }

    // Ability Kits
    public static Kit mage() {
        Kit kit = new Kit("Mage", KitType.ABILITY, Material.FIRE_CHARGE);

        kit.addMasterDescription(Utils.format("&7Shoot fireballs"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 3500);
        kit.addPrice(2, 7500);
        kit.addPrice(3, 13000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.mage()});

        return kit;
    }
    public static Kit ninja() {
        Kit kit = new Kit("Ninja", KitType.ABILITY, Material.CHAIN);

        kit.addMasterDescription(Utils.format("&7You and your pets become invisible"));
        kit.addMasterDescription(Utils.format("&7and stun monsters"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4000);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 14000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.ninja()});

        return kit;
    }
    public static Kit templar() {
        Kit kit = new Kit("Templar", KitType.ABILITY, Material.GOLDEN_SWORD);

        kit.addMasterDescription(Utils.format("&7Give yourself and nearby allies absorption"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 3500);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 12500);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.templar()});

        return kit;
    }
    public static Kit warrior() {
        Kit kit = new Kit("Warrior", KitType.ABILITY, Material.NETHERITE_HELMET);

        kit.addMasterDescription(Utils.format("&7Give yourself and nearby allies strength"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 5000);
        kit.addPrice(2, 9000);
        kit.addPrice(3, 14000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.warrior()});

        return kit;
    }
    public static Kit knight() {
        Kit kit = new Kit("Knight", KitType.ABILITY, Material.SHIELD);

        kit.addMasterDescription(Utils.format("&7Give yourself and nearby allies resistance"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4500);
        kit.addPrice(2, 8500);
        kit.addPrice(3, 13000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.knight()});

        return kit;
    }
    public static Kit priest() {
        Kit kit = new Kit("Priest", KitType.ABILITY, Material.TOTEM_OF_UNDYING);

        kit.addMasterDescription(Utils.format("&7Give yourself and nearby allies regeneration"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 5000);
        kit.addPrice(2, 9000);
        kit.addPrice(3, 15000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.priest()});

        return kit;
    }
    public static Kit siren() {
        Kit kit = new Kit("Siren", KitType.ABILITY, Material.COBWEB);

        kit.addMasterDescription(Utils.format("&7Slow and even weaken nearby monsters"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4000);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 13500);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.siren()});

        return kit;
    }
    public static Kit monk() {
        Kit kit = new Kit("Monk", KitType.ABILITY, Material.BELL);

        kit.addMasterDescription(Utils.format("&7Give yourself and nearby allies haste"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 3000);
        kit.addPrice(2, 7000);
        kit.addPrice(3, 11000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.monk()});

        return kit;
    }
    public static Kit messenger() {
        Kit kit = new Kit("Messenger", KitType.ABILITY, Material.FEATHER);

        kit.addMasterDescription(Utils.format("&7Give yourself and nearby allies speed"));
        kit.setAbilityKitDescriptions();

        kit.addPrice(1, 4000);
        kit.addPrice(2, 8000);
        kit.addPrice(3, 12000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD), GameItems.messenger()});

        return kit;
    }

    // Effect Kits
    public static Kit blacksmith() {
        Kit kit = new Kit("Blacksmith", KitType.EFFECT, Material.ANVIL);
        kit.addMasterDescription(Utils.format("&7All equipment purchased are unbreakable"));
        kit.addPrice(1, 7500);
        kit.addItems(1, new ItemStack[]{Utils.makeUnbreakable(new ItemStack(Material.WOODEN_SWORD))});
        return kit;
    }
    public static Kit witch() {
        Kit kit = new Kit("Witch", KitType.EFFECT, Material.CAULDRON);
        kit.addMasterDescription(Utils.format("&7All purchased potions become splash potions"));
        kit.addPrice(1, 2500);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit merchant() {
        Kit kit = new Kit("Merchant", KitType.EFFECT, Material.EMERALD_BLOCK);
        kit.addMasterDescription(Utils.format("&7Earn a 10% rebate on all purchases"));
        kit.addPrice(1, 4000);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit vampire() {
        Kit kit = new Kit("Vampire", KitType.EFFECT, Material.GHAST_TEAR);
        kit.addMasterDescription(Utils.format("&7Dealing x damage has an x% chance"));
        kit.addMasterDescription(Utils.format("&7of healing half a heart"));
        kit.addPrice(1, 6000);
        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});
        return kit;
    }
    public static Kit giant() {
        Kit kit = new Kit("Giant", KitType.EFFECT, Material.DARK_OAK_SAPLING);

        List<String> description = new ArrayList<>();
        description.add(Utils.format("&7Permanent 10% health boost"));
        kit.addLevelDescriptions(1, description);
        description = new ArrayList<>();
        description.add(Utils.format("&7Permanent 20% health boost"));
        kit.addLevelDescriptions(2, description);
        description = new ArrayList<>();

        kit.addPrice(1, 5000);
        kit.addPrice(2, 8000);

        kit.addItems(1, new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)});

        return kit;
    }
}
