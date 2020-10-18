package eu.mcone.replay.viewer.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.viewer.container.ReplayContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ReplayPlayerInventory extends CoreInventory {

    public ReplayPlayerInventory(Player player, ReplayContainer container, ReplayPlayer replay, Map<Integer, ItemStack> items) {
        super(replay.getDisplayName(), player, InventorySlot.ROW_5);

        if (!items.isEmpty()) {
            for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue());
            }
        } else {
            inventory.setItem(InventorySlot.ROW_2_SLOT_5, new ItemBuilder(Material.BARRIER).displayName("§cKeine Items vorhanden!").create());
        }

        setItem(InventorySlot.ROW_5_SLOT_1, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_3, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_4, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_5, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_6, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_7, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_8, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_9, CoreInventory.BACK_ITEM, e -> new ReplayPlayerInteractInventory(container, replay, player));
        openInventory();
    }
}
