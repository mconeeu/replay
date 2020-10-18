package eu.mcone.replay.viewer.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.npc.data.PlayerNpcData;
import eu.mcone.coresystem.api.bukkit.npc.enums.EquipmentPosition;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.viewer.container.ReplayContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ReplayPlayerArmorInventory extends CoreInventory {

    public ReplayPlayerArmorInventory(ReplayContainer container, ReplayPlayer replayPlayer, Player player) {
        super(replayPlayer.getDisplayName(), player, InventorySlot.ROW_1, InventoryOption.FILL_EMPTY_SLOTS);

        PlayerNpcData data = replayPlayer.getNpc().getEntityData();
        if (data.getEquipment().size() > 0) {
            int slot = 2;
            for (Map.Entry<EquipmentPosition, ItemStack> entry : ((PlayerNpcData) replayPlayer.getNpc().getEntityData()).getEquipment().entrySet()) {
                setItem(slot, entry.getValue());
                slot++;
            }
        } else {
            setItem(InventorySlot.ROW_1_SLOT_4, new ItemBuilder(Material.BARRIER, 1).displayName("§cDer Spieler hat monentan keine Rüstung an!").create());
        }

        setItem(InventorySlot.ROW_1_SLOT_9, new ItemBuilder(Material.IRON_DOOR, 1).displayName("§7Zurück").create(), e -> new ReplayPlayerInteractInventory(container, replayPlayer, player));
        openInventory();
    }
}
