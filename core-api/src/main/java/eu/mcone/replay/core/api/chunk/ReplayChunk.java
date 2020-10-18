package eu.mcone.replay.core.api.chunk;

import eu.mcone.coresystem.api.bukkit.codec.Codec;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReplayChunk {

    int getID();

    ChunkData getChunkData();

    void addPlayerCodec(UUID uuid, int tick, Codec<?, ?> wrapper);

    void addServerCodec(int tick, Codec<?, ?> wrapper);

    Codec<?, ?> getLastPlayerCodecInRange(UUID uuid, int startTick, int endTick);

    Codec<?, ?> getLastServerCodecInRange(int start, int end);

    Map<Integer, List<Codec<?, ?>>> getPlayerCodecs(UUID uuid);

    List<Codec<?, ?>> getServerCodecs(int tick);

    Collection<UUID> getPlayers();

    interface ChunkData {

        Map<UUID, Map<Integer, List<Codec<?, ?>>>> getPlayerCodecs();

        Map<Integer, List<Codec<?, ?>>> getServerCodecs();

        byte[] serialize();

        byte[] deserialize();

        int getLength();
    }
}
