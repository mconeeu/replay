package eu.mcone.replay.core.chunk;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.gameapi.api.GamePlugin;
import eu.mcone.replay.core.CoreReplay;
import eu.mcone.replay.core.api.event.chunk.ReplayChunkLoadedEvent;
import eu.mcone.replay.core.api.event.chunk.ReplayChunkUnloadedEvent;
import eu.mcone.replay.core.api.file.ReplayData;
import eu.mcone.replay.core.file.ReplayFile;
import eu.mcone.replay.core.api.file.ReplayFileEntryType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ReplayChunkHandler implements eu.mcone.replay.core.api.chunk.ReplayChunkHandler {

    private final CorePlugin plugin;
    private final CodecRegistry codecRegistry;

    @Getter
    private final CoreReplay replay;
    @Getter
    private final ReplayFile<CoreReplay, ReplayData<?>> replayFile;

    @Getter
    private final int chunkLength;
    private final Map<Integer, ReplayChunk> chunks;
    private int currentChunkID;
    private int lastChunkID;
    private transient BukkitTask unloadChunkTask;

    private final HashMap<Integer, byte[]> toMigrate;

    public ReplayChunkHandler(CodecRegistry codecRegistry, ReplayFile<?, ?> replayFile) {
        this(codecRegistry, replayFile, 600);
    }

    public ReplayChunkHandler(CodecRegistry codecRegistry, ReplayFile<?, ?> replayFile, int chunkLength) {
        this.plugin = replayFile.getCorePlugin();
        this.codecRegistry = codecRegistry;
        this.replay = replayFile.getReplay(CoreReplay.class);
        this.replayFile = (ReplayFile<CoreReplay, ReplayData<?>>) replayFile;
        this.chunkLength = chunkLength;
        this.chunks = new HashMap<>();
        lastChunkID = replay.getLastTick() / 600;
        toMigrate = new HashMap<>();
    }

    public ReplayChunk createNewChunk(int ID) {
        return new ReplayChunk(ID, new ReplayChunk.ChunkData(codecRegistry));
    }

    private void unloadChunk() {
        if (unloadChunkTask == null) {
            unloadChunkTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                if (currentChunkID > 2) {
                    List<Integer> whitelist = new ArrayList<>();
                    whitelist.add(currentChunkID - 1);
                    whitelist.add(currentChunkID);
                    whitelist.add(currentChunkID + 1);

                    for (int i = 0; i < chunks.size(); i++) {
                        if (chunks.containsKey(i) && !whitelist.contains(i)) {
                            chunks.remove(i);
                            Bukkit.getPluginManager().callEvent(new ReplayChunkUnloadedEvent(replay.getID(), "CHUNK:" + i));
                            System.out.println("REMOVE Chunk: " + i);
                        }
                    }
                }
            }, 120, 120);
        }
    }

    public void preLoad() {
        System.out.println("pre load chunks");
        lastChunkID = currentChunkID;

        if (currentChunkID < 2) {
            for (int i = 0; i <= 2; i++) {
                loadChunk(i);
            }
        }
    }

    public ReplayChunk getChunk(int tick) {
        currentChunkID = tick / chunkLength;

        System.out.println("CURRENT ID: " + currentChunkID);
        int nextChunk = currentChunkID + 1;
        if (tick != replay.getLastTick()) {
            if (!chunks.containsKey(nextChunk) && nextChunk <= lastChunkID) {
                System.out.println("Load next CHUNK " + nextChunk);
                loadChunk(nextChunk);
            }

        } else {
            Bukkit.getScheduler().cancelTask(unloadChunkTask.getTaskId());
        }

        return chunks.get(currentChunkID);
    }

    public ReplayChunk getChunkByID(int chunkID) {
        lastChunkID = currentChunkID;
        currentChunkID = chunkID;
        System.out.println("CURRENT ID: " + currentChunkID);
        int nextChunk = currentChunkID + 1;
        if (!chunks.containsKey(nextChunk) && nextChunk <= lastChunkID) {
            System.out.println("Load next CHUNK " + nextChunk);
            loadChunk(nextChunk);
        }

        return chunks.get(currentChunkID);
    }

    public void writeChunksInZip(ZipOutputStream zipOutputStream) {
        if (!CoreSystem.getInstance().getWorldManager().existsWorldInDatabase(replay.getWorld())) {
            CoreSystem.getInstance().getWorldManager().upload(CoreSystem.getInstance().getWorldManager().getWorld(replay.getWorld()), (uploaded) -> {
                if (uploaded) {
                    System.out.println("World upload Succeeded, create and save now the replay file...");
                    Bukkit.getScheduler().runTaskAsynchronously(GamePlugin.getGamePlugin(), () -> {
                        try {
                            for (Map.Entry<Integer, ReplayChunk> entry : chunks.entrySet()) {
                                try {
                                    ZipEntry zipEntry = new ZipEntry("CHUNK:" + entry.getKey());
                                    zipOutputStream.putNextEntry(zipEntry);
                                    zipOutputStream.write(entry.getValue().getChunkData().serialize());

                                    zipOutputStream.closeEntry();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            zipOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
    }

    public File save(File file) {
        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file));
            writeChunksInZip(zipOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return file;
    }

    private void loadChunk(int chunkID) {
        if (!chunks.containsKey(chunkID)) {

//            Bukkit.getScheduler().runTask(GameAPIPlugin.getInstance(), () -> {
            if (replayFile.getReplayFile().exists()) {
                FileInputStream fileInputStream = null;
                ZipInputStream zipInputStream = null;

                try {
                    fileInputStream = new FileInputStream(replayFile.getReplayFile());
                    zipInputStream = new ZipInputStream(fileInputStream);

                    for (ZipEntry zipEntry; (zipEntry = zipInputStream.getNextEntry()) != null; ) {
                        if (zipEntry.getName().equalsIgnoreCase(ReplayFileEntryType.REPLAY_CHUNK.getName() + chunkID)) {
                            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                                final byte[] buf = new byte[1024];
                                int length;
                                while ((length = zipInputStream.read(buf, 0, buf.length)) >= 0) {
                                    byteArrayOutputStream.write(buf, 0, length);
                                }

                                ReplayChunk chunk = new ReplayChunk(chunkID, new ReplayChunk.ChunkData(this.codecRegistry, byteArrayOutputStream.toByteArray()));
                                byte[] migrated = chunk.getChunkData().deserialize();

                                if (migrated != null) {
                                    toMigrate.put(chunkID, migrated);
                                }

                                chunks.put(chunkID, chunk);
                                System.out.println("chunk " + chunkID + " loaded!");
                                Bukkit.getPluginManager().callEvent(new ReplayChunkLoadedEvent(replay.getID(), ReplayFileEntryType.REPLAY_CHUNK.getName() + chunkID));

                                unloadChunk();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    zipInputStream.close();
                    fileInputStream.close();
                    System.gc();

                    migrate();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (zipInputStream != null) {
                            zipInputStream.close();
                        }

                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new NullPointerException("Replay file not found");
            }
//            });
        }
    }

    private void migrate() {
        if (toMigrate.size() > 0) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.sendConsoleMessage("§aStarting migration...");

                List<byte[]> data = new ArrayList<>();

                //Read file
                FileInputStream fileInputStream = null;
                ZipInputStream zipInputStream = null;

                try {
                    fileInputStream = new FileInputStream(replayFile.getReplayFile());
                    zipInputStream = new ZipInputStream(fileInputStream);

                    for (ZipEntry zipEntry; (zipEntry = zipInputStream.getNextEntry()) != null; ) {
                        for (Map.Entry<Integer, byte[]> migrationEntry : toMigrate.entrySet()) {
                            if (zipEntry.getName().equalsIgnoreCase(ReplayFileEntryType.REPLAY_CHUNK.getName() + migrationEntry.getKey())) {
                                data.add(migrationEntry.getValue());
                            } else {
                                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                                    final byte[] buf = new byte[1024];
                                    int length;
                                    while ((length = zipInputStream.read(buf, 0, buf.length)) >= 0) {
                                        byteArrayOutputStream.write(buf, 0, length);
                                    }

                                    data.add(byteArrayOutputStream.toByteArray());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }

                    zipInputStream.close();
                    fileInputStream.close();
                    System.gc();

                    migrate();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (zipInputStream != null) {
                            zipInputStream.close();
                        }

                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (!data.isEmpty()) {
                    //Write new file
                    plugin.sendConsoleMessage("§aWriting migrated chunks to file...");

                    if (replayFile.getReplayFile().exists()) {
                        if (replayFile.getReplayFile().delete()) {
                            plugin.sendConsoleMessage("§aDeleted old replay file!");
                        }
                    }

                    try {
                        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(replayFile.getReplayFile()));

                        int chunkID = 0;
                        for (byte[] chunkData : data) {
                            ZipEntry zipEntry = new ZipEntry("CHUNK:" + chunkID);
                            zipOut.putNextEntry(zipEntry);
                            zipOut.write(chunkData);

                            zipOut.closeEntry();

                            chunkID++;
                        }

                        zipOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    plugin.sendConsoleMessage("§aMigration completed!");
                } else {
                    plugin.sendConsoleMessage("§cError by migrating Chunk data to newer Version, ID " + replay.getID() + " (empty chunk data!)");
                }
            });
        }
    }
}
