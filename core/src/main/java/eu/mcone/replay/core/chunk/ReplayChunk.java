package eu.mcone.replay.core.chunk;

import eu.mcone.coresystem.api.bukkit.codec.*;
import eu.mcone.coresystem.api.bukkit.packets.Chunk;
import group.onegaming.networkmanager.core.api.util.UUIDUtils;
import lombok.Getter;

import java.io.*;
import java.util.*;

public class ReplayChunk extends Chunk implements Serializable, eu.mcone.replay.core.api.chunk.ReplayChunk {

    @Getter
    private final ChunkData chunkData;
    @Getter
    private final int ID;

    public ReplayChunk(int ID, ChunkData chunkData) {
        this.ID = ID;
        this.chunkData = chunkData;
    }

    @Override
    public void addPlayerCodec(UUID uuid, int tick, Codec<?, ?> codec) {
        if (codec != null) {
            if (chunkData.getPlayerCodecs().containsKey(uuid)) {
                if (!chunkData.getPlayerCodecs().get(uuid).containsKey(tick)) {
                    chunkData.getPlayerCodecs().get(uuid).put(tick, new ArrayList<>());
                }
            } else {
                chunkData.getPlayerCodecs().put(uuid, new HashMap<Integer, List<Codec<?, ?>>>() {{
                    put(tick, new ArrayList<>());
                }});
            }

            chunkData.getPlayerCodecs().get(uuid).get(tick).add(codec);
        }
    }

    @Override
    public void addServerCodec(int tick, Codec<?, ?> codec) {
        if (codec != null) {
            if (chunkData.getServerCodecs().containsKey(tick)) {
                chunkData.getServerCodecs().put(tick, new ArrayList<>());
            }

            chunkData.getServerCodecs().get(tick).add(codec);
        }
    }

    @Override
    public Codec<?, ?> getLastPlayerCodecInRange(UUID uuid, int start, int end) {
        if (this.chunkData.getPlayerCodecs().containsKey(uuid)) {
            int i = start;
            Codec<?, ?> found = null;

            if (start < end) {
                while (true) {
                    if (i < end) {
                        if (chunkData.getPlayerCodecs().get(uuid).containsKey(i)) {
                            List<Codec<?, ?>> codecs = chunkData.getPlayerCodecs().get(uuid).get(i);
                            found = codecs.get(codecs.size() - 1);
                            i++;
                        }
                    } else {
                        return found;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Codec<?, ?> getLastServerCodecInRange(int start, int end) {
        Codec<?, ?> found = null;
        int i = start;

        while (true) {
            if (start < end) {
                while (true) {
                    if (i < end) {
                        if (chunkData.getServerCodecs().containsKey(i)) {
                            List<Codec<?, ?>> codecs = chunkData.getServerCodecs().get(i);
                            found = codecs.get(codecs.size() - 1);
                            i++;
                        }
                    } else {
                        return found;
                    }
                }
            }
        }
    }

    @Override
    public Map<Integer, List<Codec<?, ?>>> getPlayerCodecs(UUID uuid) {
        return chunkData.getPlayerCodecs().getOrDefault(uuid, null);
    }

    @Override
    public List<Codec<?, ?>> getServerCodecs(final int tick) {
        return chunkData.getServerCodecs().getOrDefault(tick, null);
    }

    @Override
    public Collection<UUID> getPlayers() {
        return chunkData.getPlayerCodecs().keySet();
    }

    @Getter
    public static class ChunkData extends eu.mcone.coresystem.api.bukkit.packets.ChunkData implements eu.mcone.replay.core.api.chunk.ReplayChunk.ChunkData {

        private final CodecRegistry codecRegistry;

        private final transient Map<UUID, Map<Integer, List<Codec<?, ?>>>> playerCodecs = new HashMap<>();
        private final transient Map<Integer, List<Codec<?, ?>>> serverCodecs = new HashMap<>();

        private byte[] serialized;

        public ChunkData(CodecRegistry codecRegistry) {
            this.codecRegistry = codecRegistry;
        }

        public ChunkData(CodecRegistry codecRegistry, byte[] serialized) {
            this.serialized = serialized;
            this.codecRegistry = codecRegistry;
        }

        public byte[] serialize() {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

                CodecOutputStream codecOutputStream = new CodecOutputStream();

                //Write size of player codecs
                dataOutputStream.writeInt(playerCodecs.size());
                for (Map.Entry<UUID, Map<Integer, List<Codec<?, ?>>>> entry : playerCodecs.entrySet()) {
                    //Write UUID to stream
                    UUIDUtils.toByteArray(entry.getKey(), dataOutputStream);
                    //Write codec list size to stream
                    dataOutputStream.writeInt(entry.getValue().size());

                    for (Map.Entry<Integer, List<Codec<?, ?>>> innerEntry : entry.getValue().entrySet()) {
                        //Write size of codecs to stream
                        dataOutputStream.writeInt(innerEntry.getKey());
                        //Write list of codecs
                        codecOutputStream.write(innerEntry.getValue(), dataOutputStream);
                    }
                }

                //Write size fo server codecs
                dataOutputStream.writeInt(serverCodecs.size());
                for (Map.Entry<Integer, List<Codec<?, ?>>> entry : serverCodecs.entrySet()) {
                    //Write tick
                    dataOutputStream.writeInt(entry.getKey());
                    //Write list of codecs
                    codecOutputStream.write(entry.getValue(), dataOutputStream);
                }

                return (serialized = byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public byte[] deserialize() {
            CodecOutputStream codecOutputStream = new CodecOutputStream();
            CodecInputStream codecInputStream = new CodecInputStream(this.codecRegistry);
            boolean migrate = false;

            //Write migrated codecs
            ByteArrayOutputStream migratedByteOutput = new ByteArrayOutputStream();
            DataOutputStream migratedDataOutput = new DataOutputStream(migratedByteOutput);

            //Read serialized codecs
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialized);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

            try {
                //Read player codecs from byte array
                //Read size of player codecs
                int sizePlayerCodecs = dataInputStream.readInt();
                migratedDataOutput.writeInt(sizePlayerCodecs);
                for (int i = 0; i < sizePlayerCodecs; i++) {
                    //Read player UUID
                    UUID uuid = UUIDUtils.toUUID(dataInputStream);
                    UUIDUtils.toByteArray(uuid, migratedDataOutput);

                    playerCodecs.put(uuid, new HashMap<>());

                    //Read size of codecs
                    int innerSize = dataInputStream.readInt();
                    migratedDataOutput.writeInt(innerSize);
                    for (int innerI = 0; innerI < innerSize; innerI++) {
                        //Read tick
                        int tick = dataInputStream.readInt();
                        //Read all codecs and return it as List of Codecs
                        MultipleCodecCallback multipleCodecCallback = codecInputStream.readAsList(dataInputStream);
                        playerCodecs.get(uuid).put(tick, multipleCodecCallback.getCodecs());

                        if (multipleCodecCallback.getMigrated() > 0) {
                            migratedDataOutput.write(multipleCodecCallback.getMigratedCodecs());
                            migrate = true;
                        } else {
                            codecOutputStream.write(multipleCodecCallback.getCodecs(), migratedDataOutput);
                        }
                    }
                }

                //Read server codecs
                int sizeServerCodecs = dataInputStream.readInt();
                for (int i = 0; i < sizeServerCodecs; i++) {
                    int tick = dataInputStream.readInt();
                    migratedDataOutput.writeInt(tick);

                    MultipleCodecCallback callback = codecInputStream.readAsList(dataInputStream);
                    serverCodecs.put(tick, callback.getCodecs());

                    if (callback.getMigrated() > 0) {
                        migratedDataOutput.write(callback.getMigratedCodecs());
                        migrate = true;
                    } else {
                        codecOutputStream.write(callback.getCodecs(), migratedDataOutput);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.gc();

            if (migrate) {
                return migratedByteOutput.toByteArray();
            } else {
                return null;
            }
        }

        @Override
        public int getLength() {
            return playerCodecs.size() + serverCodecs.size();
        }
    }
}