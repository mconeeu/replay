package eu.mcone.replay.viewer.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.viewer.ReplayViewer;
import eu.mcone.replay.viewer.container.ReplayContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReplayPlayerInteractInventory extends CoreInventory {

    public static final ItemStack CAMERA = Skull.fromUrl("http://textures.minecraft.net/texture/8e5aa048cdda7d7bc7dddd59fe43e0c6c7d5567d14518ee54a722d8a59198").setDisplayName("§7Ansicht des Spielers").getItemStack();

    public ReplayPlayerInteractInventory(ReplayContainer container, ReplayPlayer replayPlayer, Player player) {
        super(replayPlayer.getDisplayName(), player, InventorySlot.ROW_4, InventoryOption.FILL_EMPTY_SLOTS);

        try {
            OfflineCorePlayer cp = CoreSystem.getInstance().getOfflineCorePlayer(replayPlayer.getUuid());

            Skull skull = new Skull(replayPlayer.getName());
            setItem(InventorySlot.ROW_1_SLOT_5, skull.lore(
                    "§7Name: §f" + replayPlayer.getName(),
                    "§7Anzeigename: " + replayPlayer.getDisplayName(),
                    "§7Rang: §f" + cp.getMainGroup().getPrefix() + cp.getMainGroup().getName(),
                    "§7Onlinetime: §f" + cp.getOnlinetime(),
                    "§7Coins: §f" + cp.getCoins(),
                    "§7Status: " + cp.getState().getName(),
                    "§7Herzen: §c" + replayPlayer.getHealth(),
                    "§7Reportet: " + (replayPlayer.isReported() ? "§cJa: " : "§aNein"),
                    "",
                    "§7Freundschaftsanfrage senden? §8(§7Klick§8)"
            ).getItemStack(), e -> CoreSystem.getInstance().getChannelHandler().createSetRequest(player, "CMD", "friend add " + replayPlayer.getName()));

            setItem(InventorySlot.ROW_3_SLOT_3, new ItemBuilder(Material.DIAMOND_SWORD, 1).displayName("§cStats").lore(
                    "§7Kills: §f" + replayPlayer.getStats().getKills(),
                    "§7Tode: §f" + replayPlayer.getStats().getDeaths(),
                    "§7Goals: §f" + replayPlayer.getStats().getGoals(),
                    "§7K/D: §f" + (replayPlayer.getStats().getKills() != 0 || replayPlayer.getStats().getDeaths() != 0 ? (Math.max(replayPlayer.getStats().getKills() / replayPlayer.getStats().getDeaths(), 0)) : 0)
            ).create());

            setItem(InventorySlot.ROW_3_SLOT_4, new ItemBuilder(Material.DIAMOND_CHESTPLATE, 1).displayName("§7Rüstung").create(), e -> new ReplayPlayerArmorInventory(container, replayPlayer, player));
            setItem(InventorySlot.ROW_3_SLOT_5, new ItemBuilder(Material.CHEST, 1).displayName("§eInventar").create(), e -> replayPlayer.addInventoryViewer(player, new ReplayPlayerInventory(player, container, replayPlayer, replayPlayer.getInventoryItems())));
            setItem(InventorySlot.ROW_3_SLOT_6, CAMERA, e -> {
                CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(player);
                container.joinCamera(player, replayPlayer.getNpc());

                if (replayPlayer.getScoreboard() != null) {
                    corePlayer.getScoreboard().setNewObjective(replayPlayer.getScoreboard());
                }

                ReplayViewer.getInstance().getMessenger().send(player, "§7Du bist nun in der Ansicht des Spielers §f§l" + replayPlayer.getName() + "§7, sneake um die Ansicht wieder zu verlassen.");
            });
        } catch (PlayerNotResolvedException e) {
            setItem(InventorySlot.ROW_1_SLOT_5, new ItemBuilder(Material.BARRIER, 1).displayName("§cFEHLER").lore(
                    "§cDer spieler konnte in der Datenbank",
                    "§cnicht gefunden werden"
            ).create());
        }

        openInventory();
    }
}
