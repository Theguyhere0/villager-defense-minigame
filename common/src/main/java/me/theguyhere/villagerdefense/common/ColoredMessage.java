package me.theguyhere.villagerdefense.common;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * A class to represent a message and its desired color before formatting into a proper message.
 */
@Getter
public class ColoredMessage {
    private final @NotNull ChatColor base;
    private final @NotNull String message;

    public ColoredMessage(ChatColor base, String message) {
        this.base = base == null ? ChatColor.WHITE : base;
        this.message = message == null ? "" : message;
    }

    public ColoredMessage(String message) {
        base = ChatColor.WHITE;
        this.message = message == null ? "" : message;
    }

    public @NotNull String toString() {
        return base + message;
    }
}
