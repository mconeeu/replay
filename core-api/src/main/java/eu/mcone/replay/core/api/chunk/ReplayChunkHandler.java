package eu.mcone.replay.core.api.chunk;

public interface ReplayChunkHandler {

    int getChunkLength();

    void preLoad();

    ReplayChunk createNewChunk(int ID);

    ReplayChunk getChunk(int tick);

    ReplayChunk getChunkByID(int chunkID);
}
