package eu.mcone.replay.core.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.replay.core.api.objective.ReplayPlayerSidebarObjective;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@BsonDiscriminator
public class ReplayPlayer implements eu.mcone.replay.core.api.player.ReplayPlayer {

    private UUID uuid;
    private String name;
    private String displayName;
    @Setter
    private boolean reported;
    @Setter
    private long joined;
    @Setter
    private long left;
    @Setter
    private CoreLocation spawnLocation;

    @Setter
    private transient Map<Integer, ItemStack> inventoryItems;
    private transient Stats stats;
    @Setter
    private transient int health;
    @Setter
    private transient int food;
    private transient Map<Player, CoreInventory> inventoryViewers;
    @Setter
    private transient ReplayPlayerSidebarObjective scoreboard;
    @Setter
    private transient PlayerNpc npc;

    public ReplayPlayer(final Player player) {
        CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(player);
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.displayName = (corePlayer.isNicked() ? Group.SPIELER.getPrefix() : corePlayer.getMainGroup().getPrefix()) + player.getName();
        this.joined = System.currentTimeMillis() / 1000;
        this.spawnLocation = new CoreLocation(player.getLocation());
        this.inventoryItems = new HashMap<>();
        this.stats = new Stats(0, 0, 0);
        this.health = 10;
        this.food = 20;
        this.inventoryViewers = new HashMap<>();
    }

    @BsonCreator
    public ReplayPlayer(@BsonProperty("uuid") UUID uuid, @BsonProperty("name") String name, @BsonProperty("displayName") String displayName, @BsonProperty("reported") boolean reported,
                        @BsonProperty("joined") long joined, @BsonProperty("left") long left, @BsonProperty("spawnLocation") CoreLocation spawnLocation) {
        this.uuid = uuid;
        this.name = name;
        this.displayName = displayName;
        this.reported = reported;
        this.joined = joined;
        this.left = left;
        this.spawnLocation = spawnLocation;
        this.inventoryItems = new HashMap<>();
        this.stats = new Stats(0, 0, 0);
        this.health = 10;
        this.food = 20;
        this.inventoryViewers = new HashMap<>();
    }

    public void setInventoryItem(int slot, ItemStack itemStack) {
        inventoryItems.put(slot, itemStack);
    }

    public void addInventoryViewer(Player player, CoreInventory coreInventory) {
        inventoryViewers.put(player, coreInventory);
    }

    public void removeInventoryViewer(Player player) {
        inventoryViewers.remove(player);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Stats implements eu.mcone.replay.core.api.player.ReplayPlayer.Stats {
        private transient int kills = 0;
        private transient int deaths = 0;
        private transient int goals = 0;
    }
}
