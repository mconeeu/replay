package eu.mcone.replay.viewer.api;

import eu.mcone.replay.viewer.api.container.ReplayContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface ReplayViewManager {

    /**
     * Returns the spawn location.
     *
     * @return Location
     */
    Location getSpawnLocation();

    /**
     * Sets the spawn location where the player gets teleportet if the replay ends.
     *
     * @param spawn Location
     */
    void setSpawnLocation(Location spawn);

    /**
     * This method adds the player to the give replay, but...
     * NOTE: If you use this method you have to take care yourself that the player is added to a ReplayContainer.
     *
     * @param player Player instance
     * @param replay Replay instance
     */
    void joinReplay(Player player, Replay replay);

    /**
     * This method removes the player from the registered replay, but...
     * NOTE: If you use this method you have to remove the player yourself from the respective ReplayContainer and check if the container can be deleted.
     *
     * @param player Player instance
     */
    void leaveReplay(Player player);

    Replay getReplay(String id);

    Replay getReplay(Player player);

    ReplayContainer getContainer(UUID containerUUID);

    ReplayContainer getContainer(Player player);

    Collection<Replay> getCachedReplays();

}
