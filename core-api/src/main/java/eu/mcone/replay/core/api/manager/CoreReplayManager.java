package eu.mcone.replay.core.api.manager;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.replay.core.api.CoreReplay;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface CoreReplayManager {

    MongoDatabase getReplayDatabase();

    CodecRegistry getCodecRegistry();

    <T> T getReplay(final String replayID, Class<T> clazz);

    <T> FindIterable<T> getReplays(Gamemode gamemode, Class<T> clazz);

    <T> FindIterable<T> getReplays(Gamemode gamemode, final int skip, final int limit, Class<T> clazz);

    <T> FindIterable<T> getReplays(final int skip, final int limit, Class<T> clazz);

    <T> FindIterable<T> getReplays(Player player, Gamemode gamemode, final int skip, final int limit, Class<T> clazz);

    <T> FindIterable<T> getReplaysForPlayer(final UUID uuid, final int skip, final int limit, Class<T> clazz);

    <T> T insertReplay(Class<T> clazz, CoreReplay coreReplay);

    long getReplaySize();

    boolean existsReplay(final String id);

    boolean deleteReplay(final String ID);

    long countReplaysForGamemodeAndPlayer(Player player, Gamemode gamemode);

    long countReplaysForGamemode(Gamemode gamemode);

    long countReplaysForPlayer(final Player player);

}
