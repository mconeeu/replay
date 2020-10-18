package eu.mcone.replay.viewer.api;

import eu.mcone.replay.core.api.CoreReplay;
import eu.mcone.replay.core.api.file.ReplayData;
import eu.mcone.replay.core.api.file.ReplayFile;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface Replay extends CoreReplay {

    ReplayFile<Replay, ReplayData<?>> getReplayFile();

    ReplayContainer createContainer();

    Collection<ReplayContainer> getContainers();

    void removeContainer(UUID uuid);

    ReplayContainer getContainer(UUID uuid);

    ReplayContainer getContainer(Player player);

    void openInformationInventory(Player player);
}
