package eu.mcone.replay.viewer;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.replay.core.api.file.ReplayData;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.core.CoreReplay;
import eu.mcone.replay.core.file.ReplayFile;
import eu.mcone.replay.viewer.api.event.ReplayWorldUnloadedEvent;
import eu.mcone.replay.viewer.api.event.container.ReplayContainerCreatedEvent;
import eu.mcone.replay.viewer.api.event.container.ReplayContainerRemovedEvent;
import eu.mcone.replay.viewer.container.ReplayContainer;
import eu.mcone.replay.viewer.inventory.ReplayInformationInventory;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Replay extends CoreReplay implements eu.mcone.replay.viewer.api.Replay {

    private final Map<UUID, eu.mcone.replay.viewer.api.container.ReplayContainer> containers;
    @Getter
    private final eu.mcone.replay.core.api.file.ReplayFile<eu.mcone.replay.viewer.api.Replay, ReplayData<?>> replayFile;

    public Replay(String gameID, String world) {
        super(gameID, world);
        containers = new HashMap<>();
        replayFile = new ReplayFile<>(ReplayViewer.getInstance(), ReplayViewer.getInstance().getReplayManager().getCodecRegistry(), this);
    }

    @BsonIgnore
    public ReplayContainer createContainer() {
        ReplayContainer container = new ReplayContainer(this);
        containers.put(container.getContainerUUID(), container);
        Bukkit.getPluginManager().callEvent(new ReplayContainerCreatedEvent(container.getContainerUUID()));
        return container;
    }

    @BsonIgnore
    public Collection<eu.mcone.replay.viewer.api.container.ReplayContainer> getContainers() {
        return containers.values();
    }

    @BsonIgnore
    public void removeContainer(UUID uuid) {
        if (containers.containsKey(uuid)) {
            ReplayViewer.getInstance().sendConsoleMessage("§aRemoving Container §f" + uuid);
            for (ReplayPlayer player : getPlayers()) {
                if (player.getNpc() != null) {
                    CoreSystem.getInstance().getNpcManager().removeNPC(player.getNpc());
                }
            }

            containers.remove(uuid);
            Bukkit.getPluginManager().callEvent(new ReplayContainerRemovedEvent(getID(), uuid));

            if (containers.size() == 0) {
//                Bukkit.unloadWorld(world, false);
                ReplayViewer.getInstance().sendConsoleMessage("§aReplay world unloaded!");
                Bukkit.getPluginManager().callEvent(new ReplayWorldUnloadedEvent(getID(), getWorld()));
            }
        }
    }

    @BsonIgnore
    public eu.mcone.replay.viewer.api.container.ReplayContainer getContainer(UUID uuid) {
        return containers.getOrDefault(uuid, null);
    }

    @BsonIgnore
    public eu.mcone.replay.viewer.api.container.ReplayContainer getContainer(Player player) {
        for (eu.mcone.replay.viewer.api.container.ReplayContainer container : containers.values()) {
            if (container.getViewers().contains(player)) {
                return container;
            }
        }

        return null;
    }

    @BsonIgnore
    public void openInformationInventory(Player player) {
        new ReplayInformationInventory(player, this);
    }
}
