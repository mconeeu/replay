package eu.mcone.replay.sever.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.replay.viewer.api.ReplayViewer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ReplayNpcInventory extends CoreInventory {

    public ReplayNpcInventory(Player player) {
        super("§e§lReplays", player, InventorySlot.ROW_4, InventoryOption.FILL_EMPTY_SLOTS);

        long playerDocuments = ReplayViewer.getInstance().getReplayManager().countReplaysForPlayer(player);

        setItem(InventorySlot.ROW_1_SLOT_5, new Skull(player.getName()).setDisplayName("§7Eigene Replays").lore(
                (playerDocuments > 0 ? "§a§l" + playerDocuments : "§c0") + " §7Replays verfügbar"
        ).getItemStack(), e -> {
            if (playerDocuments > 0) {
                ReplayInventory.openInventory(player);
            } else {
                ReplayViewer.getInstance().getMessenger().send(player, "§cEs sind keine Replays verfügbar!");
            }
        });

        long documentCount = ReplayViewer.getInstance().getReplayManager().getReplaySize();
        setItem(InventorySlot.ROW_3_SLOT_6, new ItemBuilder(Material.WATCH).displayName("§eReplays").create(), e -> {
            if (documentCount > 0) {
                ReplayInventory.openInventory(player);
            } else {
                ReplayViewer.getInstance().getMessenger().send(player, "§cEs sind keine Replays verfügbar!");
            }
        });

        openInventory();
    }
}
