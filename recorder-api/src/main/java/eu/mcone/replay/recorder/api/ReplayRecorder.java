package eu.mcone.replay.recorder.api;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.replay.recorder.api.manager.ReplayManager;
import org.bukkit.ChatColor;

public abstract class ReplayRecorder extends CorePlugin {

    public static ReplayRecorder instance;

    public ReplayRecorder() {
        super("replay.recorder", ChatColor.RED, "replay.prefix");
        instance = this;
    }

    public static ReplayRecorder getInstance() {
        return instance;
    }

    public abstract ReplayManager getReplayManager();
}
