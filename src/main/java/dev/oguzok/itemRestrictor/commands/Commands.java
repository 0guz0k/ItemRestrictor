package dev.oguzok.itemRestrictor.commands;

import dev.oguzok.itemRestrictor.ItemRestrictor;
import dev.oguzok.itemRestrictor.configuration.LoadValues;
import dev.oguzok.itemRestrictor.utilities.PermissionUtil;
import dev.oguzok.itemRestrictor.utilities.serializer.ColorUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("itemrestrictor.command.reload")) {
            sender.sendMessage(ColorUtil.toHex(
                    LoadValues.getInstance().getMessageNoPermission()
            ));
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                ItemRestrictor.getInstance().reloadConfig();
                LoadValues.getInstance().loadConfig(ItemRestrictor.getInstance());
                if (LoadValues.getInstance().getCheckMode() == 2) ItemRestrictor.startCheck();
                sender.sendMessage(ColorUtil.toHex(
                        LoadValues.getInstance().getMessageReload()
                ));
                return true;
            }
        }
        sender.sendMessage(ColorUtil.toHex(
                LoadValues.getInstance().getMessageUsage()
        ));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!PermissionUtil.hasPermission((Player) sender, "command.reload")) return List.of();
        if (args.length == 1) return List.of("reload");
        return List.of();
    }
}
