package eu.mcone.replay.viewer;

import eu.mcone.replay.viewer.manager.ReplayManager;
import lombok.Getter;

public class ReplayViewer extends eu.mcone.replay.viewer.api.ReplayViewer {

    @Getter
    private ReplayManager replayManager;
    @Getter
    private ReplayViewManager replayViewManager;

    @Override
    public void onEnable() {
        sendConsoleMessage("Starting replay viewer...");
        replayManager = new ReplayManager();
        replayViewManager = new ReplayViewManager();
    }

    @Override
    public void onDisable() {

    }
}
