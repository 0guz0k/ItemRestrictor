package dev.oguzok.itemRestrictor.utilities.item;

import dev.oguzok.itemRestrictor.configuration.LoadValues;
import dev.oguzok.itemRestrictor.configuration.ValuesData;

import dev.oguzok.itemRestrictor.utilities.PermissionUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;
import java.util.Map;

public class ItemUtil {

    public static int getCurrentAmount(Player player, ItemStack currentItem) {
        int amount = 0;

        Map<String, ValuesData> values = LoadValues.getInstance().getValuesData();
        for (ValuesData valuesData : values.values()) {

            for (ItemStack invItem : player.getInventory().getContents()) {
                if (invItem == null || invItem.getType().isAir()) continue;

                if (!containsMaterial(invItem, valuesData.materials())) continue;
                if (!containsMaterial(currentItem, valuesData.materials())) continue;

                if (!containsEnchantments(invItem, valuesData.enchantments())) continue;
                if (!containsEnchantments(currentItem, valuesData.enchantments())) continue;

                if (!containsPotionEffects(invItem, valuesData.potionTypes())) continue;
                if (!containsPotionEffects(currentItem, valuesData.potionTypes())) continue;

                if (!containsCustomModelData(invItem, valuesData.customModelData())) continue;
                if (!containsCustomModelData(currentItem, valuesData.customModelData())) continue;

                amount += invItem.getAmount();
            }
        }

        return amount;
    }

    public static Integer getMaxAmount(ItemStack currentItem) {
        Map<String, ValuesData> values = LoadValues.getInstance().getValuesData();

        for (ValuesData valuesData : values.values()) {

            if (!containsMaterial(currentItem, valuesData.materials())) continue;
            if (!containsEnchantments(currentItem, valuesData.enchantments())) continue;
            if (!containsPotionEffects(currentItem, valuesData.potionTypes())) continue;
            if (!containsCustomModelData(currentItem, valuesData.customModelData())) continue;

            return valuesData.maxAmount();
        }

        return null;
    }

    public static String getMessage(ItemStack currentItem) {
        Map<String, ValuesData> values = LoadValues.getInstance().getValuesData();

        for (ValuesData valuesData : values.values()) {

            if (!containsMaterial(currentItem, valuesData.materials())) continue;
            if (!containsEnchantments(currentItem, valuesData.enchantments())) continue;
            if (!containsPotionEffects(currentItem, valuesData.potionTypes())) continue;
            if (!containsCustomModelData(currentItem, valuesData.customModelData())) continue;

            return valuesData.message();
        }

        return null;
    }

    public static boolean containsMaterial(ItemStack currentItem, List<String> materials) {
        if (materials == null || materials.isEmpty()) return false;

        return materials.contains(currentItem.getType().name());
    }

    public static boolean containsEnchantments(ItemStack currentItem, List<String> enchantments) {
        if (enchantments == null || enchantments.isEmpty()) return true;

        Map<Enchantment, Integer> currentEnchants = currentItem.getEnchantments();

        for (String s : enchantments) {
            String[] parts = s.split(";");

            if (parts.length != 2) return false;

            String stringEnchantment = parts[0].trim().toUpperCase();
            int level = Integer.parseInt(parts[1].trim());

            Enchantment enchantment = Enchantment.getByName(stringEnchantment);

            if (enchantment == null) return false;

            if (!currentEnchants.containsKey(enchantment) || currentEnchants.get(enchantment) != level) return false;
        }

        return true;
    }

    public static boolean containsPotionEffects(ItemStack item, List<String> potionTypes) {
        if (potionTypes == null || potionTypes.isEmpty()) return true;

        if (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION || item.getType() == Material.TIPPED_ARROW) {

            if (item.getItemMeta() instanceof PotionMeta potionMeta) {
                return potionTypes.contains(potionMeta.getBasePotionType().name());
            }
        }

        return true;
    }

    public static boolean containsCustomModelData(ItemStack currentItem, int customModelData) {
        if (customModelData == -1) return true;

        ItemMeta currentItemMeta = currentItem.getItemMeta();

        if (!currentItemMeta.hasCustomModelData()) return false;

        return currentItemMeta.getCustomModelData() == customModelData;
    }

    public static void checkInventory(Player player) {
        if (!LoadValues.getInstance().getStatus()) return;

        if (PermissionUtil.hasBypassPermission(player) || player.getGameMode() == GameMode.CREATIVE) return;
        if (LoadValues.getInstance().getDisabledWorlds().contains(player.getWorld().getName())) return;

        PlayerInventory playerInventory = player.getInventory();
        Map<String, ValuesData> values = LoadValues.getInstance().getValuesData();

        for (ValuesData valuesData : values.values()) {
            for (int i = 0; i < playerInventory.getSize(); i++) {
                ItemStack currentItem = playerInventory.getItem(i);

                if (currentItem == null || currentItem.getType().isAir()) continue;

                if (!containsMaterial(currentItem, valuesData.materials())) continue;
                if (!containsEnchantments(currentItem, valuesData.enchantments())) continue;
                if (!containsPotionEffects(currentItem, valuesData.potionTypes())) continue;
                if (!containsCustomModelData(currentItem, valuesData.customModelData())) continue;

                int currentAmount = getCurrentAmount(player, currentItem);
                int maxAmount = valuesData.maxAmount();

                if (currentAmount > maxAmount) {
                    playerInventory.setItem(i, null);
                    player.getWorld().dropItemNaturally(player.getLocation(), currentItem);

                    currentItem.setAmount(maxAmount);
                }
            }
        }
    }
}
