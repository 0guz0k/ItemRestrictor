package dev.oguzok.itemRestrictor.configuration;

import dev.oguzok.itemRestrictor.ItemRestrictor;
import dev.oguzok.itemRestrictor.utilities.serializer.ColorUtil;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadValues {

    public static LoadValues instance = new LoadValues();
    private final Map<String, ValuesData> valuesData = new HashMap<>();

    private Boolean status;
    private Long checkInvInterval;
    private List<String> disabledWorlds;
    private Long messageInterval;
    private String messageReload;
    private String messageUsage;
    private String messageNoPermission;

    public void loadConfig(ItemRestrictor plugin) {
        ItemRestrictor.lastMessageTime.clear();
        valuesData.clear();

        FileConfiguration config = plugin.getConfig();

        status = config.getBoolean("enabled");

        if (!config.contains("restricts")) return;

        int sectionCount = 0;
        if (status) {
            for (String section : config.getConfigurationSection("restricts").getKeys(false)) {

                List<String> materials = config.getStringList("restricts." + section + ".material")
                        .stream()
                        .map(String::toUpperCase)
                        .toList();

                List<String> enchantments = config.getStringList("restricts." + section + ".enchantments")
                        .stream()
                        .map(String::toUpperCase)
                        .toList();

                List<String> potionTypes = config.getStringList("restricts." + section + ".potion_type")
                        .stream()
                        .map(String::toUpperCase)
                        .toList();

                int customModelData = config.contains("restricts." + section + ".custom_model_data") ? config.getInt("restricts." + section + ".custom_model_data") : -1;
                int maxAmount = config.getInt("restricts." + section + ".max_amount");
                String message = config.getString("restricts." + section + ".message");
                String storageMessage = config.getString("restricts." + section + ".storage_message");

                valuesData.put(section, new ValuesData(materials, enchantments, potionTypes, customModelData, maxAmount, message, storageMessage));
            }
        }

        checkInvInterval = config.getLong("check_inventory_interval");
        disabledWorlds = config.getStringList("disabled_worlds");

        messageInterval = config.getLong("message_interval");
        messageReload = config.getString("messages.reload");
        messageUsage = config.getString("messages.usage");
        messageNoPermission = config.getString("messages.no_permission");
    }

    public Map<String, ValuesData> getValuesData() {
        return valuesData;
    }

    public Boolean getStatus() {
        return status;
    }

    public Long getCheckInvInterval() {
        return checkInvInterval;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public Long getMessageInterval() {
        return messageInterval;
    }

    public String getMessageReload() {
        return messageReload;
    }

    public String getMessageUsage() {
        return messageUsage;
    }

    public String getMessageNoPermission() {
        return messageNoPermission;
    }

    public static LoadValues getInstance() {
        return instance;
    }
}
