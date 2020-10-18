package eu.mcone.replay.core.api.file;

import java.util.List;

public interface ReplayMetadata {

    List<String> getDependencies();

    void addDependency(String dependency);
}
