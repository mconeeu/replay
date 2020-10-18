package eu.mcone.replay.recorder.api.manager;

import eu.mcone.replay.core.api.manager.CoreReplayManager;
import eu.mcone.replay.recorder.api.Replay;

import java.util.List;

public interface ReplayManager extends CoreReplayManager {

    Replay createReplay(String gameID, String world);

    Replay createReplay(String ID, String gameID, String world);

    void registerReportMethod();

    List<Replay> getLoadedReplays();
}
