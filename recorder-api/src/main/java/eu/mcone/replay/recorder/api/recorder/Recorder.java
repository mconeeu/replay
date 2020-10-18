package eu.mcone.replay.recorder.api.recorder;

import eu.mcone.replay.core.api.chunk.ReplayChunk;

import java.util.Map;

public interface Recorder {

    Map<Integer, ReplayChunk> getChunks();

    String getRecorderID();

    String getWorld();

    long getStarted();

    long getStopped();

    int getTicks();

    boolean isStop();

    void record();

    void stop();

    int getLastTick();
}
