package eu.mcone.replay.core.api.file;

import eu.mcone.replay.core.api.chunk.ReplayChunkHandler;

import java.io.File;

public interface ReplayFile<R, D> {

    File getReplayFile();

    ReplayMetadata getReplayMetadata();

    <T> ReplayData<T> getReplayData(Class<T> clazz);

    ReplayChunkHandler getReplayChunkHandler();

    void save();

    void load();

    void deleteEntry(ReplayFileEntryType entryType);

    <T> T getReplay(Class<T> clazz);

    void addReplayData(ReplayData<D> replayData);
}
