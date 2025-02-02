package me.theguyhere.villagerdefense.common;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

public class CommunicationManager {
    /**
     * The amount of debug information to display in the console.
     *
     * 3 (Developer) - All errors and information tracked will be displayed. Certain behavior will be overridden.
     * 2 (Verbose) - All errors and information tracked will be displayed.
     * 1 (Normal) - Errors that drastically reduce performance and important information will be displayed.
     * 0 (Quiet) - Only the most urgent error messages will be displayed.
     */
    @Getter
    private static int debugLevel = 0;

    /**
     * Sets the new debug level for the plugin. Values below or above the proper range will be capped at the limits.
     *
     * @param newDebugLevel New debug level to be set for the plugin.
     */
    public static void setDebugLevel(int newDebugLevel) {
        debugLevel = Math.max(Math.min(newDebugLevel, 3), 0);
    }

    /**
     * Translates color codes that use "&" into their proper form to be displayed by Bukkit.
     *
     * @param msg Message to be translated.
     * @return Properly translated message.
     */
    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Formats a long description into an array of colored lines, split according to the set character limit.
     *
     * @param base Base color for the description.
     * @param description The description to be formatted.
     * @param charLimit The character limit per line.
     * @return Formatted description.
     */
    public static String[] formatDescriptionArr(ChatColor base, String description, int charLimit) {
        return formatDescriptionList(base, description, charLimit).toArray(new String[0]);
    }

    /**
     * Formats a long description into a list of colored lines, split according to the set character limit.
     *
     * @param base Base color for the description.
     * @param description The description to be formatted.
     * @param charLimit The character limit per line.
     * @return Formatted description.
     */
    public static List<String> formatDescriptionList(ChatColor base, String description, int charLimit) {
        // Get a proper ChatColor base to work with
        ChatColor properBase = base == null ? ChatColor.WHITE : base;

        // The visible character limit per line
        int realCharLimit = charLimit + properBase.toString().length();

        // Split the description into words
        String[] descArray = description.split(" ");

        List<String> descLines = new ArrayList<>();
        StringBuilder line = new StringBuilder(properBase.toString());

        for (String s : descArray) {
            // Always add to a line if empty or line remains under character limit
            if (line.length() == 0 || line.length() + s.length() <= realCharLimit)
                line.append(s).append(" ");

            // Start new line if next word makes line longer than character limit
            else {
                line.deleteCharAt(line.length() - 1);
                descLines.add(format(line.toString()));
                line = new StringBuilder(properBase.toString()).append(s).append(" ");
            }
        }

        // Add last line
        line.deleteCharAt(line.length() - 1);
        descLines.add(line.toString());

        return descLines;
    }

    /**
     * Replaces placeholders in the base colored message with colored replacements.
     *
     * @param base Base colored message.
     * @param replacements Replacement colored messages.
     * @return Properly formatted combined colored message.
     */
    public static String format(ColoredMessage base, ColoredMessage... replacements) {
        try {
            String formattedString = base.toString();
            for (ColoredMessage replacement : replacements) {
                formattedString = formattedString.replaceFirst("%s",
                        replacement.getBase() + replacement.getMessage() + base.getBase());
            }
            return formattedString;
        } catch (IllegalFormatException e) {
            debugError("The number of replacements is likely incorrect when formatting a message!", 0,
                    true, e);
        } catch (Exception e) {
            debugError("Something unexpected happened when formatting messages!", 0, true, e);
        }

        return "";
    }

    /**
     * Replaces placeholders in the base colored message with aqua colored replacements.
     *
     * @param base Base colored message.
     * @param replacements Replacement messages.
     * @return Properly formatted combined colored message.
     */
    public static String format(ColoredMessage base, String... replacements) {
        try {
            String formattedString = base.toString();
            for (String replacement : replacements)
                formattedString = formattedString.replaceFirst("%s",
                        ChatColor.AQUA + replacement + base.getBase());
            return formattedString;
        } catch (IllegalFormatException e) {
            debugError("The number of replacements is likely incorrect when formatting a message!", 0,
                    true, e);
        } catch (Exception e) {
            debugError("Something unexpected happened when formatting messages!", 0, true, e);
        }

        return "";
    }

    /**
     * Formats plugin notifications to players.
     *
     * @param msg Raw message to send to player.
     * @return Formatted message prepared to be sent to the player.
     */
    public static String notify(String msg) {
        return format("&2[VD] &f" + msg);
    }

    /**
     * Formats plugin notifications from entities with names.
     *
     * @param name Colored name of the entity.
     * @param msg Raw message to send to player.
     * @return Formatted message prepared to be sent to the player.
     */
    public static String namedNotify(ColoredMessage name, String msg) {
        return format("&2[VD] " + name.toString() + ":&f " + msg);
    }

    public static void debugError(String msg, int debugLevel) {
        debugError(msg, debugLevel, false);
    }

    public static void debugError(String msg, int debugLevel, boolean stackTrace) {
        if (CommunicationManager.debugLevel >= debugLevel) {
            Log.warning(msg);

            if (stackTrace)
                Thread.dumpStack();
        }
    }

    public static void debugError(String msg, int debugLevel, boolean stackTrace, Exception e) {
        if (CommunicationManager.debugLevel >= debugLevel) {
            Log.warning(msg);

            if (stackTrace)
                e.printStackTrace();
        }
    }

    public static void debugError(String base, int debugLevel, String... replacements) {
        debugError(base, debugLevel, false, replacements);
    }

    public static void debugError(String base, int debugLevel, boolean stackTrace, String... replacements) {
        if (CommunicationManager.debugLevel >= debugLevel) {
            String formattedMessage = base;
            for (String replacement : replacements)
                formattedMessage = formattedMessage.replaceFirst("%s",
                        ChatColor.BLUE + replacement + ChatColor.RED);
            Log.warning(formattedMessage);

            if (stackTrace)
                Thread.dumpStack();
        }
    }

    public static void debugErrorShouldNotHappen() {
        debugError("This should not be happening!", 0, true);
    }

    public static void debugInfo(String msg, int debugLevel) {
        debugInfo(msg, debugLevel, false);
    }

    public static void debugInfo(String msg, int debugLevel, boolean stackTrace) {
        if (CommunicationManager.debugLevel >= debugLevel) {
            Log.info(msg);

            if (stackTrace)
                Thread.dumpStack();
        }
    }

    public static void debugInfo(String base, int debugLevel, String... replacements) {
        debugInfo(base, debugLevel, false, replacements);
    }

    public static void debugInfo(String base, int debugLevel, boolean stackTrace, String... replacements) {
        if (CommunicationManager.debugLevel >= debugLevel) {
            String formattedMessage = base;
            for (String replacement : replacements)
                formattedMessage = formattedMessage.replaceFirst("%s",
                        ChatColor.BLUE + replacement + ChatColor.WHITE);
            Log.info(formattedMessage);

            if (stackTrace)
                Thread.dumpStack();
        }
    }

    public static void debugConfirm(String msg, int debugLevel) {
        if (CommunicationManager.debugLevel >= debugLevel)
            Log.confirm(msg);
    }
}
