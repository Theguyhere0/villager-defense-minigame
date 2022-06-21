package me.theguyhere.villagerdefense.plugin.inventories;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class InventoryFactory {
    public static Inventory createFixedSizeInventory(
            @NotNull InventoryMeta meta,
            @NotNull String formattedName,
            int lines,
            boolean exitButton,
            @NotNull List<ItemStack> buttons
    ) {
        int invSize = lines * 9;
        int fullSizedLines = buttons.size() / 9;
        int remainingFullSizedLines = lines - fullSizedLines;
        int hangingButtons = buttons.size() % 9;
        Iterator<ItemStack> buttonIterator = buttons.iterator();

        // Ensure valid number of lines
        if (lines < 1 || lines > 6)
            return null;

        // Ensure valid number of buttons
        if (buttons.size() < 1 || buttons.size() > invSize - (exitButton ? 1 : 0))
            return null;

        // Create inventory
        Inventory inv = Bukkit.createInventory(meta, invSize, formattedName);

        // Set buttons
        switch (buttons.size()) {
            case 1:
                inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + 4, buttonIterator.next());
                break;
            case 2:
                inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + 2, buttonIterator.next());
                if (lines > 1 || !exitButton)
                    inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + 6, buttonIterator.next());
                else inv.setItem(5, buttonIterator.next());
                break;
            case 3:
                if (lines > 1 || !exitButton) {
                    inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + 2, buttonIterator.next());
                    inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + 4, buttonIterator.next());
                    inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + 6, buttonIterator.next());
                } else {
                    inv.setItem(1, buttonIterator.next());
                    inv.setItem(3, buttonIterator.next());
                    inv.setItem(5, buttonIterator.next());
                }
                break;
            case 4:
                if (lines > 1 || !exitButton) {
                    for (int i = 0; i < buttons.size(); i++)
                        inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + i * 2 + 1, buttonIterator.next());
                } else {
                    for (int i = 0; i < buttons.size(); i++)
                        inv.setItem(i * 2, buttonIterator.next());
                }
                break;
            case 5:
                if (lines > 1 || !exitButton) {
                    for (int i = 0; i < buttons.size(); i++)
                        inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + i * 2, buttonIterator.next());
                } else {
                    for (int i = 0; i < buttons.size(); i++)
                        inv.setItem(i + 1, buttonIterator.next());
                }
                break;
            case 6:
                for (int i = 0; i < buttons.size(); i++)
                    inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + i + 1, buttonIterator.next());
                break;
            case 7:
                if (lines > 1 || !exitButton) {
                    for (int i = 0; i < buttons.size(); i++)
                        inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + i + 1, buttonIterator.next());
                } else {
                    for (int i = 0; i < buttons.size(); i++)
                        inv.setItem(i, buttonIterator.next());
                }
                break;
            case 8:
                for (int i = 0; i < buttons.size(); i++)
                    inv.setItem(((remainingFullSizedLines - 1) / 2) * 9 + i, buttonIterator.next());
                break;
            default:
                for (int i = 0; i < fullSizedLines; i++)
                    for (int j = 0; j < 9; j++)
                        inv.setItem(((remainingFullSizedLines - 1) / 2 + i) * 9 + j, buttonIterator.next());
                for (int i = 0; i < hangingButtons; i++)
                    inv.setItem(
                            ((remainingFullSizedLines - 1) / 2 + fullSizedLines) * 9 + (9 - hangingButtons) / 2 + i,
                            buttonIterator.next()
                    );
        }

        // Set exit button
        if (exitButton)
            inv.setItem(invSize - 1, Buttons.exit());

        return inv;
    }
    
    public static Inventory createDynamicSizeInventory(
            @NotNull InventoryMeta meta,
            @NotNull String formattedName,
            boolean exitButton,
            @NotNull List<ItemStack> buttons
    ) {
        int lines = (buttons.size() + 8 + (exitButton ? 1 : 0)) / 9;
        int invSize = lines * 9;
        int fullSizedLines = buttons.size() / 9;
        int hangingButtons = buttons.size() % 9;
        Iterator<ItemStack> buttonIterator = buttons.iterator();

        // Ensure valid number of lines
        if (lines > 6)
            return null;

        // Create inventory
        Inventory inv = Bukkit.createInventory(meta, invSize, formattedName);

        for (int i = 0; i < fullSizedLines; i++)
            for (int j = 0; j < 9; j++)
                inv.setItem(i * 9 + j, buttonIterator.next());
        for (int i = 0; i < hangingButtons; i++)
            inv.setItem((lines - 1) * 9 + (9 - hangingButtons) / 2 + i, buttonIterator.next());

        // Set exit button
        if (exitButton) {
            if (hangingButtons == 0)
                inv.setItem(invSize - 5, Buttons.exit());
            else
                inv.setItem(invSize - 1, Buttons.exit());
        }

        return inv;
    }

    public static Inventory createDynamicSizeBottomNavInventory(
            @NotNull InventoryMeta meta,
            @NotNull String formattedName,
            boolean exitButton,
            boolean newButton,
            @NotNull String dataStructureName,
            @NotNull List<ItemStack> buttons
    ) {
        int pages = (buttons.size() + 44) / 45;
        int page = meta.getPage();
        List<ItemStack> visibleButtons = buttons.subList(
                Math.max((Math.min(page, pages) - 1) * 45, 0),
                Math.min((Math.min(page, pages)) * 45, buttons.size())
        );

        int lines = (visibleButtons.size() + 8) / 9 + 1;
        int invSize = lines * 9;
        int fullSizedLines = visibleButtons.size() / 9;
        int hangingButtons = visibleButtons.size() % 9;
        Iterator<ItemStack> buttonIterator = visibleButtons.iterator();

        // Create inventory
        Inventory inv = Bukkit.createInventory(meta, invSize, formattedName);

        for (int i = 0; i < fullSizedLines; i++)
            for (int j = 0; j < 9; j++)
                inv.setItem(i * 9 + j, buttonIterator.next());
        for (int i = 0; i < hangingButtons; i++)
            inv.setItem((lines - 2) * 9 + (9 - hangingButtons) / 2 + i, buttonIterator.next());

        // Set exit button
        if (exitButton) {
            if (newButton)
                inv.setItem(invSize - 1, Buttons.exit());
            else
                inv.setItem(invSize - 5, Buttons.exit());
        }

        // Set new button
        if (newButton)
            inv.setItem(invSize - 5, Buttons.newAdd(dataStructureName));

        // Set page navigation buttons
        if (page > 1)
            inv.setItem(invSize - 7, Buttons.previousPage());

        if (page < pages)
            inv.setItem(invSize - 3, Buttons.nextPage());

        return inv;
    }

    @SafeVarargs
    public static Inventory createDynamicSizeBottomNavFreezeRowInventory(
            @NotNull InventoryMeta meta,
            @NotNull String formattedName,
            boolean exitButton,
            boolean newButton,
            @NotNull String dataStructureName,
            @NotNull List<ItemStack> buttons,
            int frozenRows,
            List<ItemStack>... frozenRowButtons
    ) {
        // Ensure valid number of frozen rows
        if (frozenRows > 5)
            return null;

        int freeSpaces = ((5 - frozenRows) * 9);
        int pages = (buttons.size() + freeSpaces - 1) / freeSpaces;
        int page = meta.getPage();
        List<ItemStack> visibleButtons = buttons.subList(
                Math.max((Math.min(page, pages) - 1) * freeSpaces, 0),
                Math.min((Math.min(page, pages)) * freeSpaces, buttons.size())
        );


        int lines = (visibleButtons.size() + 8) / 9 + 1 + frozenRows;
        int invSize = lines * 9;
        int fullSizedLines = visibleButtons.size() / 9;
        int hangingButtons = visibleButtons.size() % 9;
        Iterator<ItemStack> buttonIterator = visibleButtons.iterator();

        // Ensure valid lists for the frozen rows
        if (frozenRows != frozenRowButtons.length)
            return null;

        // Create inventory
        Inventory inv = Bukkit.createInventory(meta, invSize, formattedName);

        // Set buttons
        for (int i = 0; i < fullSizedLines; i++)
            for (int j = 0; j < 9; j++)
                inv.setItem(i * 9 + j, buttonIterator.next());
        for (int i = 0; i < hangingButtons; i++)
            inv.setItem((lines - 2 - frozenRows) * 9 + (9 - hangingButtons) / 2 + i, buttonIterator.next());

        // Set frozen row buttons
        for (int i = 0; i < frozenRows; i++) {
            Iterator<ItemStack> freezeButtonIterator = frozenRowButtons[i].iterator();
            int frozenButtonsNum = frozenRowButtons[i].size();

            switch (frozenButtonsNum) {
                case 1:
                    inv.setItem((lines - 1 - frozenRows + i) * 9 + 4, freezeButtonIterator.next());
                    break;
                case 2:
                    inv.setItem((lines - 1 - frozenRows + i) * 9 + 2, freezeButtonIterator.next());
                    inv.setItem((lines - 1 - frozenRows + i) * 9 + 6, freezeButtonIterator.next());
                    break;
                case 3:
                    inv.setItem((lines - 1 - frozenRows + i) * 9 + 2, freezeButtonIterator.next());
                    inv.setItem((lines - 1 - frozenRows + i) * 9 + 4, freezeButtonIterator.next());
                    inv.setItem((lines - 1 - frozenRows + i) * 9 + 6, freezeButtonIterator.next());
                    break;
                case 4:
                    for (int j = 0; j < frozenButtonsNum; j++)
                        inv.setItem((lines - 1 - frozenRows + i) * 9 + j * 2 + 1, freezeButtonIterator.next());
                    break;
                case 5:
                    for (int j = 0; j < frozenButtonsNum; j++)
                        inv.setItem((lines - 1 - frozenRows + i) * 9 + j * 2, freezeButtonIterator.next());
                    break;
                case 6:
                case 7:
                    for (int j = 0; j < frozenButtonsNum; j++)
                        inv.setItem((lines - 1 - frozenRows + i) * 9 + j + 1, freezeButtonIterator.next());
                    break;
                default:
                    for (int j = 0; j < frozenButtonsNum; j++)
                        inv.setItem((lines - 1 - frozenRows + i) * 9 + j, freezeButtonIterator.next());
            }
        }

        // Set exit button
        if (exitButton) {
            if (newButton)
                inv.setItem(invSize - 1, Buttons.exit());
            else
                inv.setItem(invSize - 5, Buttons.exit());
        }

        // Set new button
        if (newButton)
            inv.setItem(invSize - 5, Buttons.newAdd(dataStructureName));

        // Set page navigation buttons
        if (page > 1)
            inv.setItem(invSize - 7, Buttons.previousPage());

        if (page < pages)
            inv.setItem(invSize - 3, Buttons.nextPage());

        return inv;
    }

    public static Inventory createLocationMenu(
            @NotNull InventoryID inventoryID,
            Arena arena,
            int id,
            @NotNull String formattedName,
            boolean locationExists,
            @NotNull String locationName
    ) {
        List<ItemStack> buttons = new ArrayList<>();

        // Option to create or relocate the location
        if (!locationExists)
            buttons.add(Buttons.create(locationName));
        else buttons.add(Buttons.relocate(locationName));

        // Option to teleport to the location
        buttons.add(Buttons.teleport(locationName));

        // Option to center the location
        buttons.add(Buttons.center(locationName));

        // Option to remove the location
        buttons.add(Buttons.remove(locationName.toUpperCase()));

        return createFixedSizeInventory(
                new InventoryMeta(inventoryID, InventoryType.MENU, arena, id),
                formattedName,
                1,
                true,
                buttons);
    }

    public static Inventory createLocationMenu(
            @NotNull InventoryID inventoryID,
            Arena arena,
            @NotNull String formattedName,
            boolean locationExists,
            @NotNull String locationName
    ) {
        return createLocationMenu(inventoryID, arena, 0, formattedName, locationExists, locationName);
    }

    public static Inventory createLocationMenu(
            @NotNull InventoryID inventoryID,
            int id,
            @NotNull String formattedName,
            boolean locationExists,
            @NotNull String locationName
    ) {
        return createLocationMenu(inventoryID, null, id, formattedName, locationExists, locationName);
    }

    public static Inventory createLocationMenu(
            @NotNull InventoryID inventoryID,
            @NotNull String formattedName,
            boolean locationExists,
            @NotNull String locationName
    ) {
        return createLocationMenu(inventoryID, null, 0, formattedName, locationExists, locationName);
    }

    public static Inventory createSimpleLocationMenu(
            @NotNull InventoryID inventoryID,
            Arena arena,
            @NotNull String formattedName,
            boolean locationExists,
            @NotNull String locationName
    ) {
        return createSimpleLocationMenu(inventoryID, arena, 0, formattedName, locationExists, locationName);
    }

    public static Inventory createSimpleLocationMenu(
            @NotNull InventoryID inventoryID,
            Arena arena,
            int id,
            @NotNull String formattedName,
            boolean locationExists,
            @NotNull String locationName
    ) {
        List<ItemStack> buttons = new ArrayList<>();

        // Option to create or relocate the location
        if (!locationExists)
            buttons.add(Buttons.create(locationName));
        else buttons.add(Buttons.relocate(locationName));

        // Option to teleport to the location
        buttons.add(Buttons.teleport(locationName));

        // Option to remove the location
        buttons.add(Buttons.remove(locationName.toUpperCase()));

        return createFixedSizeInventory(
                new InventoryMeta(inventoryID, InventoryType.MENU, arena, id),
                formattedName,
                1,
                true,
                buttons);
    }

    public static Inventory createConfirmationMenu(
            @NotNull InventoryID inventoryID,
            UUID playerID,
            Arena arena,
            int id,
            @NotNull String formattedName
    ) {
        List<ItemStack> buttons = new ArrayList<>();

        // "No" option
        buttons.add(Buttons.no());

        // "Yes" option
        buttons.add(Buttons.yes());

        return createFixedSizeInventory(
                new InventoryMeta(inventoryID, InventoryType.MENU, playerID, arena, id),
                formattedName,
                1,
                false,
                buttons
        );
    }

    public static Inventory createConfirmationMenu(
            @NotNull InventoryID inventoryID,
            Arena arena,
            @NotNull String formattedName
    ) {
        return createConfirmationMenu(inventoryID, null, arena, 0, formattedName);
    }

    public static Inventory createConfirmationMenu(
            @NotNull InventoryID inventoryID,
            int id,
            @NotNull String formattedName
    ) {
        return createConfirmationMenu(inventoryID, null, null, id, formattedName);
    }

    public static Inventory createConfirmationMenu(@NotNull InventoryID inventoryID, @NotNull String formattedName) {
        return createConfirmationMenu(inventoryID, null, null, 0, formattedName);
    }

    public static Inventory createConfirmationMenu(
            @NotNull InventoryID inventoryID,
            UUID playerID,
            @NotNull String formattedName) {
        return createConfirmationMenu(inventoryID, playerID, null, 0, formattedName);
    }

    public static Inventory createIncrementorMenu(
            @NotNull InventoryID inventoryID,
            Arena arena,
            int id,
            @NotNull String formattedName
    ) {
        List<ItemStack> buttons = new ArrayList<>();

        // Decrement button
        buttons.add(ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&lDecrease")));

        // Increment button
        buttons.add(ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&2&lIncrease")));

        return createFixedSizeInventory(
                new InventoryMeta(inventoryID, InventoryType.MENU, arena, id),
                formattedName,
                1,
                true,
                buttons
        );
    }

    public static Inventory createIncrementorMenu(
            @NotNull InventoryID inventoryID,
            Arena arena,
            @NotNull String formattedName
    ) {
        return createIncrementorMenu(inventoryID, arena, 0, formattedName);
    }

    public static Inventory createAdvancedIncrementorMenu(
            @NotNull InventoryID inventoryID,
            Arena arena,
            int id,
            @NotNull String formattedName
    ) {
        List<ItemStack> buttons = new ArrayList<>();

        // Decrement button
        buttons.add(ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&lDecrease")));

        // Unlimited button
        buttons.add(
                ItemManager.createItem(Material.ORANGE_CONCRETE,
                CommunicationManager.format("&6&lUnlimited"))
        );

        // Reset button
        buttons.add(
                ItemManager.createItem(Material.LIGHT_BLUE_CONCRETE,
                CommunicationManager.format("&3&lReset to 1"))
        );

        // Increment button
        buttons.add(ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&2&lIncrease")));

        return createFixedSizeInventory(
                new InventoryMeta(inventoryID, InventoryType.MENU, arena, id),
                formattedName,
                1,
                true,
                buttons
        );
    }

    public static Inventory createAdvancedIncrementorMenu(
            @NotNull InventoryID inventoryID,
            Arena arena,
            @NotNull String formattedName
    ) {
        return createAdvancedIncrementorMenu(inventoryID, arena, 0, formattedName);
    }
}
