package dev.oguzok.itemRestrictor.listeners;

import dev.oguzok.itemRestrictor.ItemRestrictor;
import dev.oguzok.itemRestrictor.configuration.LoadValues;
import dev.oguzok.itemRestrictor.utilities.ParseUtil;
import dev.oguzok.itemRestrictor.utilities.PermissionUtil;
import dev.oguzok.itemRestrictor.utilities.item.ItemUtil;
import dev.oguzok.itemRestrictor.utilities.serializer.ColorUtil;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

                ParseUtil.parseMessage(player, ColorUtil.toHex(
                        message.replace("%amount%", String.valueOf(maxAmount))
                ));
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

    /*@EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        ItemStack cursorItem = e.getCursor();

        if (cursorItem == null || cursorItem.getType().isAir()) return;

        Integer maxAmount = ItemUtil.getMaxAmount(cursorItem);
        if (maxAmount == null) return;

        List<String> storages = ItemUtil.getStorageTypes(cursorItem);
        if (storages.contains(e.getInventory().getType().name())) return;

        String storageMessage = ItemUtil.getStorageMessage(cursorItem);

        for (int slot : e.getRawSlots()) {
            if (slot >= 0 && slot < e.getInventory().getSize()) {
                e.setCancelled(true);

                if (storageMessage == null || storageMessage.isEmpty()) {
                    ItemRestrictor.delayedMessage((Player) e.getWhoClicked(), ColorUtil.toHex(storageMessage));
                }
                return;
            }
        }
    }*/
}
