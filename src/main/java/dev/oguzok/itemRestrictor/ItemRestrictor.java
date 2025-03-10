package dev.oguzok.itemRestrictor;

import dev.oguzok.itemRestrictor.commands.Commands;
import dev.oguzok.itemRestrictor.configuration.LoadValues;
import dev.oguzok.itemRestrictor.listeners.Inventory;

import dev.oguzok.itemRestrictor.utilities.ParseUtil;
import dev.oguzok.itemRestrictor.utilities.item.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemRestrictor extends JavaPlugin {

    private static ItemRestrictor instance;
    public static final Map<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();

    @Override public void onEnable() {
        instance = this;

        saveDefaultConfig();
        LoadValues.getInstance().loadConfig(this);
        getCommand("itemrestrictor").setExecutor(new Commands());

        getServer().getPluginManager().registerEvents(new Inventory(), this);
    }

    public static ItemRestrictor getInstance() {
        return instance;
    }

    public static void startCheck() {
        long interval = LoadValues.getInstance().getCheckInvInterval();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) ItemUtil.checkInventory(player);
            }
        }.runTaskTimer(ItemRestrictor.getInstance(), 0, interval);
    }

    public static void delayedMessage(Player player, String message) {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastMessageTime.getOrDefault(player.getUniqueId(), 0L);
        long messageInterval = LoadValues.getInstance().getMessageInterval();

        if (currentTime - lastTime >= messageInterval) {
            ParseUtil.parseMessage(player, message);
            lastMessageTime.put(player.getUniqueId(), currentTime);
        }
    }
}
