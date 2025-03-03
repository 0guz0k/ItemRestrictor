package dev.oguzok.itemRestrictor.utilities;

import org.bukkit.entity.Player;

public class PermissionUtil {

    public static boolean hasBypassPermission(Player player) {
        return player.hasPermission("itemrestrictor.bypass") || player.isOp();
    }

    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission("itemrestrictor." + permission) || player.isOp();
    }
}
