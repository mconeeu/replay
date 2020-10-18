package eu.mcone.replay.viewer.api.world;

import java.util.Collection;

public interface WorldDownloader {

    void runDownloader();

    Collection<String> getDownloadedWorlds();

    void stop();
}
