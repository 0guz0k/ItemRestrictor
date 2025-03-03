package dev.oguzok.itemRestrictor.utilities;

import dev.oguzok.itemRestrictor.utilities.serializer.ColorUtil;

import org.bukkit.entity.Player;

public class ParseUtil {

    public static void parseMessage(Player player, String s) {
        if (s == null || !s.contains(";")) return;

        String[] parts = s.split(";");
        String message = parts[0];
        String type = parts[1];

        switch (type) {
            case "chat":
                player.sendMessage(ColorUtil.toHex(message));
                break;
            case "action_bar":
                player.sendActionBar(ColorUtil.toHex(message));
                break;
            default:
                player.sendMessage(ColorUtil.toHex(message));
                break;
        }
    }
}
