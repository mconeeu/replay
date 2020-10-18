package eu.mcone.replay.sever.inventory;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.category.CategoryInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.replay.core.CoreReplay;
import eu.mcone.replay.viewer.api.Replay;
import eu.mcone.replay.viewer.api.ReplayViewer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.manager.ReplayManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReplayInventory extends CategoryInventory {

    private static final Map<Gamemode, ItemStack> ITEMS = new HashMap<>();
    private static final ReplayManager MANAGER = ReplayViewer.getInstance().getReplayManager();

    static {
        for (Gamemode gamemode : Gamemode.values()) {
            ITEMS.put(gamemode, new ItemBuilder(gamemode.getItem()).displayName(gamemode.getLabel()).create());
        }
    }

    private final Gamemode gamemode;

    private ReplayInventory(Gamemode gamemode, Player player) {
        super(gamemode.getLabel(), player, ITEMS.get(gamemode));
        this.gamemode = gamemode;

        for (Gamemode mode : Gamemode.values()) {
            addCategory(ITEMS.get(mode));
        }

        openInventory();
    }

    @Override
    protected void openCategoryInventory(ItemStack itemStack, Player player) {
        new ReplayInventory(Gamemode.getGamemodeByMaterial(itemStack.getType()), player);
    }

    @Override
    public int setPaginatedItems(int skip, int limit, List<CategoryInvItem> items) {
        for (Replay replay : MANAGER.getReplays(gamemode, skip, limit, Replay.class)) {
            items.add(new CategoryInvItem(getReplayItem(replay), e -> {
                ReplayContainer container = ReplayViewer.getInstance().getReplayViewManager().getContainer(player);

                if (container != null) {
                    container.removeViewers(player);
                }

                replay.createContainer().addViewers(player);
            }));
        }

        return (int) MANAGER.countReplaysForGamemodeAndPlayer(player, gamemode);
    }

    public static void openInventory(Player p) {
        openInventory(p, Gamemode.values()[0]);
    }

    public static void openInventory(Player player, Gamemode gamemode) {
        new ReplayInventory(gamemode, player);
    }

    private ItemStack getReplayItem(Replay replay) {
        Material material = Material.PAPER;
        Gamemode gamemode = null;
        if (replay.getGameHistory().getGamemode() != null) {
            gamemode = replay.getGameHistory().getGamemode();
            material = gamemode.getItem();
        }

        return new ItemBuilder(material, 1).displayName("§7ID: §f" + replay.getID()).lore(
                "§7Spielmodus: " + (gamemode != null ? gamemode.getColor() + gamemode.getName() : "§c✘"),
                "§7Länge: §f§l" + getLength(replay.getLastTick()),
                "§7Spieler: §f§l" + replay.getPlayers().size(),
                "§7Gewinnerteam: §f§l" + replay.getGameHistory().getWinner(),
                "§7Vom: §f§l" + new SimpleDateFormat("dd.MM.yyyy").format(new Date(replay.getGameHistory().getStopped() * 1000))
        ).create();
    }

    private String getLength(int lastTick) {
        double seconds = (double) lastTick / 20;
        if (seconds < 60) {
            return seconds + " §7Sekunden";
        } else {
            return Double.parseDouble(String.format(Locale.ENGLISH, "%1.2f", seconds / 60)) + " §7Minuten";
        }
    }

}
