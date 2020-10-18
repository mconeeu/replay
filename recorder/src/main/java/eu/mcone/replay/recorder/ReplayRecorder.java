package eu.mcone.replay.recorder;

import eu.mcone.replay.recorder.manager.ReplayManager;
import lombok.Getter;
import sun.dc.pr.PRError;

public class ReplayRecorder extends eu.mcone.replay.recorder.api.ReplayRecorder {

    @Getter
    private ReplayManager replayManager;

    private Replay replay;

    @Override
    public void onEnable() {
        sendConsoleMessage("Starting replay recorder...");
        replayManager = new ReplayManager();
    }

    @Override
    public void onDisable() {

    }
}
