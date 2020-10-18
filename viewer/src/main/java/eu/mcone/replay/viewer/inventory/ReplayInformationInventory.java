package eu.mcone.replay.viewer.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.inventory.category.CategoryInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.replay.viewer.Replay;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReplayInformationInventory extends CoreInventory {

    public ReplayInformationInventory(Player player, Replay replay) {
        super("§eReplay", player, InventorySlot.ROW_3, InventoryOption.FILL_EMPTY_SLOTS);

        if (replay.getGameHistory().getGamemode() != null) {
            setItem(InventorySlot.ROW_1_SLOT_5, new ItemBuilder(replay.getGameHistory().getGamemode().getItem(), 1).displayName("§eReplay ID: §f§l" + replay.getID()).create());
        } else {
            setItem(InventorySlot.ROW_1_SLOT_5, new ItemBuilder(Material.COMMAND, 1).displayName("§eReplay ID: §f§l" + replay.getID()).create());
        }

        setItem(InventorySlot.ROW_3_SLOT_3, new ItemBuilder(Material.GRASS, 1).displayName("§eMap").lore("§7§l" + replay.getWorld()).create());

        if (replay.getGameHistory().getWinner() != null) {
            setItem(InventorySlot.ROW_3_SLOT_4, new ItemBuilder(Material.BARRIER, 1).displayName("§cGewinner").create());
        } else {
            setItem(InventorySlot.ROW_3_SLOT_4, new ItemBuilder(Material.BED, 1).displayName("§eGewinner").lore("§f§l" + replay.getGameHistory().getWinner()).create());
        }

        setItem(InventorySlot.ROW_3_SLOT_5, ItemBuilder.wrap(CategoryInventory.UP_ITEM).displayName("§eServer gestartet").lore("§f§l" + new SimpleDateFormat("HH:mm").format(new Date(replay.getGameHistory().getStarted() * 1000))).create());
        setItem(InventorySlot.ROW_3_SLOT_6, ItemBuilder.wrap(CategoryInventory.DOWN_ITEM).displayName("§eServer gestoppt").lore("§f§l" + new SimpleDateFormat("HH:mm").format(new Date(replay.getGameHistory().getStopped() * 1000))).create());
        setItem(InventorySlot.ROW_3_SLOT_7, new ItemBuilder(Material.WATCH).displayName("§eLänge").lore("§f§l" + getLength(replay.getLastTick())).create());

        openInventory();
    }

    public static String getLength(float lastTick) {
        double seconds = lastTick / 20;
        if (seconds < 60) {
            return (int) seconds + " §7Sekunden";
        } else {
            double time = Double.parseDouble(String.format(Locale.ENGLISH, "%1.2f", seconds / 60));

            if (time >= 1) {
                return time + " §7Minute";
            } else {
                return time + " §7Minuten";
            }
        }
    }
}
