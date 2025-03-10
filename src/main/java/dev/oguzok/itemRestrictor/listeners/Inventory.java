package dev.oguzok.itemRestrictor.listeners;

import dev.oguzok.itemRestrictor.ItemRestrictor;
import dev.oguzok.itemRestrictor.configuration.LoadValues;
import dev.oguzok.itemRestrictor.utilities.ParseUtil;
import dev.oguzok.itemRestrictor.utilities.PermissionUtil;
import dev.oguzok.itemRestrictor.utilities.item.ItemUtil;
import dev.oguzok.itemRestrictor.utilities.serializer.ColorUtil;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class Inventory implements Listener {

    @EventHandler public void on(PlayerAttemptPickupItemEvent e) {
        if (!LoadValues.getInstance().getStatus()) return;

        final Player player = e.getPlayer();

        if (PermissionUtil.hasBypassPermission(player) || player.getGameMode() == GameMode.CREATIVE) return;
        if (LoadValues.getInstance().getDisabledWorlds().contains(player.getWorld().getName())) return;

        ItemStack currentItem = e.getItem().getItemStack();

        Integer maxAmount = ItemUtil.getMaxAmount(currentItem);
        if (maxAmount == null) return;

        int currentItemAmount = currentItem.getAmount();
        int currentAmount = ItemUtil.getCurrentAmount(player, currentItem);

        String message = ItemUtil.getMessage(currentItem);

        if (currentAmount >= maxAmount) {
            e.setCancelled(true);

            ItemRestrictor.delayedMessage(player, ColorUtil.toHex(message).replace("%amount%", String.valueOf(maxAmount)));
            return;
        }

        int allowedToPickup  = maxAmount - currentAmount;

        PlayerInventory playerInv = player.getInventory();

        if (currentItemAmount > allowedToPickup) {
            e.setCancelled(true);

            ItemStack pickupItem = currentItem.clone();
            pickupItem.setAmount(allowedToPickup);
            playerInv.addItem(pickupItem);
            currentItem.setAmount(currentItemAmount - allowedToPickup);

            ItemRestrictor.delayedMessage(player, ColorUtil.toHex(message).replace("%amount%", String.valueOf(maxAmount)));
        }
    }

    @EventHandler public void on(InventoryClickEvent e) {
        if (!LoadValues.getInstance().getStatus()) return;

        final Player player = (Player) e.getWhoClicked();
        if (PermissionUtil.hasBypassPermission(player) || player.getGameMode() == GameMode.CREATIVE) return;
        if (LoadValues.getInstance().getDisabledWorlds().contains(player.getWorld().getName())) return;

        ItemStack clickedItem = e.getCurrentItem();
        ItemStack cursorItem = player.getItemOnCursor();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        PlayerInventory playerInv = player.getInventory();
        if (e.getClickedInventory().equals(playerInv)) return;

        Integer maxAmount = ItemUtil.getMaxAmount(clickedItem);
        if (maxAmount == null) return;


        int currentAmount = ItemUtil.getCurrentAmount(player, clickedItem);
        int currentItemAmount = clickedItem.getAmount();
        int cursorAmount = cursorItem.getAmount();
        int totalAmount = currentAmount + cursorAmount + currentItemAmount;

        String message = ItemUtil.getMessage(clickedItem);

        if (totalAmount > maxAmount) {
            if (e.getClick() == ClickType.DROP || e.getClick() == ClickType.CONTROL_DROP) {
                e.setCancelled(false);

            } else if (e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || e.getClick().isKeyboardClick()) {
                e.setCancelled(true);

                ItemRestrictor.delayedMessage(player, ColorUtil.toHex(message).replace("%amount%", String.valueOf(maxAmount)));
            } else if (e.isShiftClick()) {
                e.setCancelled(true);

                int allowedToCollect = maxAmount - currentAmount - cursorAmount;

                if (allowedToCollect > 0) {
                    ItemStack collectItemNew = clickedItem.clone();
                    collectItemNew.setAmount(allowedToCollect);
                    playerInv.addItem(collectItemNew);
                    clickedItem.setAmount(clickedItem.getAmount() - allowedToCollect);
                    e.setCurrentItem(clickedItem);

                    ItemRestrictor.delayedMessage(player, ColorUtil.toHex(message).replace("%amount%", String.valueOf(maxAmount)));
                } else {
                    e.setCancelled(true);

                    ItemRestrictor.delayedMessage(player, ColorUtil.toHex(message).replace("%amount%", String.valueOf(maxAmount)));
                }
            } else {
                e.setCancelled(true);

                ItemRestrictor.delayedMessage(player, ColorUtil.toHex(message).replace("%amount%", String.valueOf(maxAmount)));
            }
        }
    }

    @EventHandler public void on(InventoryCloseEvent e) {
        if (!LoadValues.getInstance().getStatus()) return;
        final Player player = (Player) e.getPlayer();
        if (PermissionUtil.hasBypassPermission(player) || player.getGameMode() == GameMode.CREATIVE) return;
        if (LoadValues.getInstance().getDisabledWorlds().contains(player.getWorld().getName())) return;
        if (LoadValues.getInstance().getCheckMode() == 1) {
            Bukkit.getScheduler().runTaskLater(ItemRestrictor.getInstance(), () -> {
                ItemUtil.checkInventory((Player) e.getPlayer());
            }, 1L);
        }
    }
}
