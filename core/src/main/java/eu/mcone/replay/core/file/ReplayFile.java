package eu.mcone.replay.core.file;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.gameapi.api.GamePlugin;
import eu.mcone.replay.core.api.exception.NoReplayDataFoundException;
import eu.mcone.replay.core.api.file.ReplayData;
import eu.mcone.replay.core.api.file.ReplayFileEntryType;
import eu.mcone.replay.core.chunk.ReplayChunkHandler;
import eu.mcone.replay.core.CoreReplay;
import group.onegaming.networkmanager.core.api.util.GenericUtils;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ReplayFile<R, D> implements eu.mcone.replay.core.api.file.ReplayFile<R, D> {

    @Getter
    private final CorePlugin corePlugin;
    private final CodecRegistry codecRegistry;

    private final CoreReplay coreReplay;

    @Getter
    private final File replayFile;

    @Getter
    private ReplayMetadata replayMetadata;
    private ReplayData<D> replayData;
    @Getter
    private final ReplayChunkHandler replayChunkHandler;

    public ReplayFile(CorePlugin corePlugin, CodecRegistry codecRegistry, CoreReplay replay) {
        this.corePlugin = corePlugin;
        this.codecRegistry = codecRegistry;
        this.coreReplay = replay;
        this.replayFile = new File(corePlugin.getDataFolder(), this.coreReplay.getID() + ".replay");
        replayMetadata = new ReplayMetadata();
        replayChunkHandler = new ReplayChunkHandler(codecRegistry, this);
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(GamePlugin.getGamePlugin(), () -> {
            try {
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(replayFile));

                // write metadata
                ZipEntry metadataEntry = new ZipEntry("metadata");
                zipOutputStream.putNextEntry(metadataEntry);
                zipOutputStream.write(GenericUtils.serialize(replayMetadata));
                zipOutputStream.closeEntry();

                // write replay data
                ZipEntry dataEntry = new ZipEntry("data");
                zipOutputStream.putNextEntry(dataEntry);
                zipOutputStream.write(CoreSystem.getInstance().getGson().toJson(replayData).getBytes());
                zipOutputStream.closeEntry();

                replayChunkHandler.writeChunksInZip(zipOutputStream);

                zipOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void load() {
        Bukkit.getScheduler().runTaskAsynchronously(GamePlugin.getGamePlugin(), () -> {
            if (replayFile.exists()) {
                FileInputStream fileInputStream = null;
                ZipInputStream zipInputStream = null;

                try {
                    fileInputStream = new FileInputStream(replayFile);
                    zipInputStream = new ZipInputStream(fileInputStream);

                    for (ZipEntry zipEntry; (zipEntry = zipInputStream.getNextEntry()) != null; ) {
                        if (zipEntry.getName().equalsIgnoreCase(ReplayFileEntryType.REPLAY_METADATA.getName())) {
                            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                                final byte[] buf = new byte[1024];
                                int length;
                                while ((length = zipInputStream.read(buf, 0, buf.length)) >= 0) {
                                    byteArrayOutputStream.write(buf, 0, length);
                                }

                                replayMetadata = CoreSystem.getInstance().getGson().fromJson(new String(byteArrayOutputStream.toByteArray()), ReplayMetadata.class);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else if (zipEntry.getName().equalsIgnoreCase(ReplayFileEntryType.REPLAY_DATA.getName())) {
                            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                                final byte[] buf = new byte[1024];
                                int length;
                                while ((length = zipInputStream.read(buf, 0, buf.length)) >= 0) {
                                    byteArrayOutputStream.write(buf, 0, length);
                                }

                                GsonBuilder gson = new GsonBuilder();
                                Type collectionType = new TypeToken<ReplayData<D>>() {
                                }.getType();
                                replayData = gson.create().fromJson(new String(byteArrayOutputStream.toByteArray()), collectionType);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    zipInputStream.close();
                    fileInputStream.close();
                    System.gc();
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
        });
    }

    public void deleteEntry(ReplayFileEntryType entryType) {
        if (!entryType.equals(ReplayFileEntryType.REPLAY_CHUNK)) {
            Map<String, String> zip_properties = new HashMap<>();
            zip_properties.put("create", "false");

            try (FileSystem fileSystem = FileSystems.newFileSystem(this.replayFile.toURI(), zip_properties)) {
                Path filePath = fileSystem.getPath(entryType.getName());
                Files.delete(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T getReplay(Class<T> clazz) {
        return clazz.cast(coreReplay);
    }

    public void addReplayData(ReplayData<D> replayData) {
        this.replayData = replayData;
    }

    public <T> ReplayData<T> getReplayData(Class<T> clazz) {
        try {
            if (replayData != null) {
                return (ReplayData<T>) replayData;
            } else {
                throw new NoReplayDataFoundException();
            }
        } catch (NoReplayDataFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
