package eu.mcone.replay.viewer.api;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.replay.viewer.api.manager.ReplayManager;
import lombok.Getter;
import org.bukkit.ChatColor;

public abstract class ReplayViewer extends CorePlugin {

    @Getter
    public static ReplayViewer instance;

    public ReplayViewer() {
        super("replay.viewer", ChatColor.RED, "replay.prefix");
        instance = this;
    }

    public abstract ReplayManager getReplayManager();

    public abstract ReplayViewManager getReplayViewManager();
}
