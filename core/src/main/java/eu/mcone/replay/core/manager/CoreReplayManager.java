package eu.mcone.replay.core.manager;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.bson.LocationCodecProvider;
import eu.mcone.coresystem.api.bukkit.event.armor.ArmorEquipEvent;
import eu.mcone.coresystem.api.bukkit.event.objective.CoreObjectiveCreateEvent;
import eu.mcone.coresystem.api.bukkit.event.objective.CoreSidebarObjectiveUpdateEvent;
import eu.mcone.coresystem.api.bukkit.event.player.StatsChangeEvent;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.npc.capture.codecs.*;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.replay.core.api.CoreReplay;
import eu.mcone.replay.core.api.event.PlayerJoinReplayEvent;
import eu.mcone.replay.core.api.event.PlayerQuitReplayEvent;
import eu.mcone.replay.core.api.exception.ReplaySessionNotFoundException;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import eu.mcone.replay.core.api.runner.SimpleServerRunner;
import eu.mcone.replay.core.packets.player.*;
import eu.mcone.replay.core.packets.player.block.BlockBreakEventCodec;
import eu.mcone.replay.core.packets.player.block.BlockPlaceEventCodec;
import eu.mcone.replay.core.packets.player.block.PacketPlayInBlockDigCodec;
import eu.mcone.replay.core.packets.player.block.PlayerInteractEventCodec;
import eu.mcone.replay.core.packets.player.inventory.InventoryCloseEventCodec;
import eu.mcone.replay.core.packets.player.objective.CoreObjectiveCreateEventCodec;
import eu.mcone.replay.core.packets.player.objective.CoreSidebarObjectiveUpdateEventCodec;
import eu.mcone.replay.core.packets.server.EntityExplodeEventCodec;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public abstract class CoreReplayManager implements eu.mcone.replay.core.api.manager.CoreReplayManager {

    public static final String REPLAY_COLLECTION = "replay";

    @Getter
    private final MongoDatabase replayDatabase;

//    private final List<ReplayRecord> replays;
//    private WorldDownloader worldDownloader;

    //    private ReplayViewManager replayViewManager;
    @Getter
    private final CodecRegistry codecRegistry;

    public CoreReplayManager() {
//        replays = new ArrayList<>();

//        if (GamePlugin.getGamePlugin().hasOption(Option.DOWNLOAD_REPLAY_WORLDS)) {
//            GamePlugin.getGamePlugin().sendConsoleMessage("§aStarting world downloader...");
//            worldDownloader = new WorldDownloader();
//            worldDownloader.runDownloader();
//        }

//        if (GamePlugin.getGamePlugin().hasOption(Option.USE_REPLAY_VIEW_MANAGER)) {
//            replayViewManager = new ReplayViewManager();
//        }

        replayDatabase = CoreSystem.getInstance().getMongoDB().withCodecRegistry(
                fromRegistries(getDefaultCodecRegistry(),
                        fromProviders(
                                new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY),
                                new LocationCodecProvider(),
                                PojoCodecProvider.builder().conventions(Conventions.DEFAULT_CONVENTIONS).automatic(true).build()
                        )
                )
        );

        codecRegistry = CoreSystem.getInstance().createCodecRegistry(true);
        registerCodecs();
    }

    private void registerCodecs() {
        //Default Codecs
        codecRegistry.registerCodec((byte) 1, PlayerMoveEventCodec.class, PlayerMoveEvent.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 2, PlayInUseBlockCodec.class, PlayerInteractEvent.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 3, PlayInUseItemCodec.class, PlayerInteractEvent.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 4, ItemSwitchEventCodec.class, PlayerItemHeldEvent.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 5, PlayInEntityActionCodec.class, PacketPlayInEntityAction.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 6, PlayOutAnimationCodec.class, PacketPlayOutAnimation.class, (byte) 2, PlayerNpc.class);

        //eu.mcone.replay.plugin.Replay Codecs - Events
        codecRegistry.registerCodec((byte) 7, PlayerJoinReplayEventCodec.class, PlayerJoinReplayEvent.class, (byte) 3, SimplePlayerRunner.class);
        codecRegistry.registerCodec((byte) 8, PlayerQuitReplayEventCodec.class, PlayerQuitReplayEvent.class, (byte) 2, PlayerNpc.class);

        codecRegistry.registerCodec((byte) 9, BlockBreakEventCodec.class, BlockBreakEvent.class, (byte) 3, SimplePlayerRunner.class);
        codecRegistry.registerCodec((byte) 10, BlockPlaceEventCodec.class, BlockPlaceEvent.class, (byte) 3, SimplePlayerRunner.class);

        codecRegistry.registerCodec((byte) 11, PlayerPickupItemEventCodec.class, PlayerPickupItemEvent.class, (byte) 3, SimplePlayerRunner.class);
        codecRegistry.registerCodec((byte) 12, PlayerDropItemEventCodec.class, PlayerDropItemEvent.class, (byte) 3, SimplePlayerRunner.class);

        codecRegistry.registerCodec((byte) 13, PlayerDeathEventCodec.class, PlayerDeathEvent.class, (byte) 3, SimplePlayerRunner.class);
        codecRegistry.registerCodec((byte) 14, EntityRespawnEventCodec.class, PlayerRespawnEvent.class, (byte) 3, SimplePlayerRunner.class);

        codecRegistry.registerCodec((byte) 15, EntityDamageByEntityEventCodec.class, EntityDamageByEntityEvent.class, (byte) 2, PlayerNpc.class);

        codecRegistry.registerCodec((byte) 16, ArmorEquipEventCodec.class, ArmorEquipEvent.class, (byte) 3, SimplePlayerRunner.class);
        codecRegistry.registerCodec((byte) 17, InventoryCloseEventCodec.class, InventoryCloseEvent.class, (byte) 3, SimplePlayerRunner.class);

        codecRegistry.registerCodec((byte) 18, CoreObjectiveCreateEventCodec.class, CoreObjectiveCreateEvent.class, (byte) 4, ReplayPlayer.class);
        codecRegistry.registerCodec((byte) 19, CoreSidebarObjectiveUpdateEventCodec.class, CoreSidebarObjectiveUpdateEvent.class, (byte) 4, ReplayPlayer.class);

        codecRegistry.registerCodec((byte) 20, PlayerInteractEventCodec.class, PlayerInteractEvent.class, (byte) 3, SimplePlayerRunner.class);

        codecRegistry.registerCodec((byte) 21, PlayerItemConsumeEventCodec.class, PlayerItemConsumeEvent.class, (byte) 3, SimplePlayerRunner.class);

        codecRegistry.registerCodec((byte) 22, ProjectileLaunchEventCodec.class, ProjectileLaunchEvent.class, (byte) 2, PlayerNpc.class);
        codecRegistry.registerCodec((byte) 23, EntityExplodeEventCodec.class, EntityExplodeEvent.class, (byte) 5, SimpleServerRunner.class);
        codecRegistry.registerCodec((byte) 24, StatsChangeEventCodec.class, StatsChangeEvent.class, (byte) 4, ReplayPlayer.class);
        codecRegistry.registerCodec((byte) 25, EntityShootBowEventCodec.class, EntityShootBowEvent.class, (byte) 4, ReplayPlayer.class);

        //eu.mcone.replay.plugin.Replay Codecs - Packets
        codecRegistry.registerCodec((byte) 26, PlayOutUpdateHealthCodec.class, PacketPlayOutUpdateHealth.class, (byte) 4, ReplayPlayer.class);
        codecRegistry.registerCodec((byte) 27, PacketPlayOutNamedSoundEffectCodec.class, PacketPlayOutNamedSoundEffect.class, (byte) 3, SimplePlayerRunner.class);
        codecRegistry.registerCodec((byte) 28, PacketPlayOutEntityEffectCodec.class, PacketPlayOutEntityEffect.class, (byte) 3, SimplePlayerRunner.class);
        codecRegistry.registerCodec((byte) 29, PacketPlayInBlockDigCodec.class, PacketPlayInBlockDig.class, (byte) 3, SimplePlayerRunner.class);
        codecRegistry.registerCodec((byte) 30, PacketPlayOutSpawnEntityCodec.class, PacketPlayOutSpawnEntity.class, (byte) 3, SimplePlayerRunner.class);
    }


//    public eu.mcone.gameapi.api.replay.world.WorldDownloader getWorldDownloader() {
//        return worldDownloader;
//    }
//
//    public eu.mcone.gameapi.api.replay.ReplayViewManager getReplayViewManager() {
//        try {
//            if (GamePlugin.getGamePlugin().hasOption(Option.USE_REPLAY_VIEW_MANAGER)) {
//                return replayViewManager;
//            } else {
//                throw new GameModuleNotActiveException("The game module ReplayViewManager isn`t active!");
//            }
//        } catch (GameModuleNotActiveException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    /**
     * Gets the live version of the replay session from the database
     *
     * @param replayID Unique ID
     * @return ReplaySession interface
     */
    public <T> T getReplay(final String replayID, Class<T> clazz) {
        try {
            T replay = replayDatabase.getCollection(REPLAY_COLLECTION, clazz).find(eq("iD", replayID)).first();

            if (replay != null) {
//                download(replay.getWorld());
                return replay;
            } else {
                throw new ReplaySessionNotFoundException();
            }
        } catch (ReplaySessionNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> FindIterable<T> getReplays(Gamemode gamemode, Class<T> clazz) {
        return replayDatabase.getCollection(REPLAY_COLLECTION, clazz).find(eq("gamemode", gamemode.toString()));
    }

    public <T> FindIterable<T> getReplays(Gamemode gamemode, final int skip, final int limit, Class<T> clazz) {
        return replayDatabase.getCollection(REPLAY_COLLECTION, clazz).find(eq("gamemode", gamemode.toString())).skip(skip).limit(limit);
    }

    public <T> FindIterable<T> getReplays(final int skip, final int limit, Class<T> clazz) {
        return replayDatabase.getCollection(REPLAY_COLLECTION, clazz).find().skip(skip).limit(limit);
    }

    public <T> FindIterable<T> getReplays(Player player, Gamemode gamemode, final int skip, final int limit, Class<T> clazz) {
        return replayDatabase.getCollection(REPLAY_COLLECTION, clazz).find(and(exists("replayPlayers." + player.getUniqueId()), eq("gamemode", gamemode.toString()))).skip(skip).limit(limit);
    }

    /**
     * Returns a List of Replays for the specified UUID (Player) only from the local cache.
     *
     * @param uuid Unique UUID
     * @return List of ReplaySessions
     */
    public <T> FindIterable<T> getReplaysForPlayer(final UUID uuid, final int skip, final int limit, Class<T> clazz) {
        return replayDatabase.getCollection(REPLAY_COLLECTION, clazz).find(exists("replayPlayers." + uuid)).skip(skip).limit(limit);
    }

    public long getReplaySize() {
        return replayDatabase.getCollection(REPLAY_COLLECTION).countDocuments();
    }


    public <T> T insertReplay(Class<T> clazz, CoreReplay coreReplay) {
        T t = clazz.cast(coreReplay);
        getReplayDatabase().getCollection(REPLAY_COLLECTION, clazz).insertOne(t);
        return t;
    }

    /**
     * Checks if the specified sessionID already exists
     *
     * @param id Unique ID
     * @return boolean
     */
    public boolean existsReplay(final String id) {
        return replayDatabase.getCollection(REPLAY_COLLECTION).find(eq("iD", id)).first() != null;
    }

    public boolean deleteReplay(final String ID) {
        return replayDatabase.getCollection(REPLAY_COLLECTION).deleteOne(eq("iD", ID)).getDeletedCount() > 0;
    }

    public long countReplaysForGamemodeAndPlayer(Player player, Gamemode gamemode) {
        return replayDatabase.getCollection(REPLAY_COLLECTION).countDocuments(and(exists("replayPlayers." + player.getUniqueId()), eq("gamemode", gamemode.toString())));
    }

    public long countReplaysForGamemode(Gamemode gamemode) {
        return replayDatabase.getCollection(REPLAY_COLLECTION).countDocuments(eq("gamemode", gamemode.toString()));
    }

    public long countReplaysForPlayer(final Player player) {
        return replayDatabase.getCollection(REPLAY_COLLECTION).countDocuments(exists("replayPlayers." + player.getUniqueId()));
    }

    /**
     * Returns a list of all replay sessions in the database
     *
     * @return eu.mcone.replay.plugin.Replay
     */
//    @Override
//    public List<ReplayRecord> getRecording() {
//        return replays;
//    }

//    private void download(String world) {
//        if (!CoreSystem.getInstance().getWorldManager().existWorld(world)) {
//            if (worldDownloader != null) {
//                worldDownloader.getDownloaded().add(world);
//            }
//
//            CoreSystem.getInstance().getWorldManager().download(world, (succeeded) -> {
//                if (succeeded) {
//                    CoreWorld downloadedWorld = CoreSystem.getInstance().getWorldManager().getWorld(world);
//                    downloadedWorld.setLoadOnStartup(false);
//                    downloadedWorld.save();
//                }
//            });
//        }
//    }
}
