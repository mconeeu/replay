package eu.mcone.replay.viewer.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.viewer.ReplayViewer;
import eu.mcone.replay.viewer.api.Replay;
import org.bukkit.entity.Player;

public class ReplaySpectatorInventory extends CoreInventory {

    public ReplaySpectatorInventory(Replay replay, Player p) {
        super("§eZuschauen", p, InventorySlot.ROW_6, InventoryOption.FILL_EMPTY_SLOTS);

        int slot = 0;
        for (ReplayPlayer rPlayer : replay.getPlayers()) {
            setItem(slot, new Skull(rPlayer.getName()).setDisplayName(rPlayer.getDisplayName()).getItemStack(), e -> {
                p.teleport((rPlayer.getNpc() != null ? rPlayer.getNpc().getLocation() : rPlayer.getSpawnLocation().bukkit()));
                ReplayViewer.getInstance().getMessenger().send(p, "§aDu wurdest zum Spieler " + rPlayer.getDisplayName() + " §ateleportiert!");
            });
            slot++;
        }

        openInventory();
    }
}
