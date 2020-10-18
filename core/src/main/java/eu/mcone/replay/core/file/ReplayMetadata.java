package eu.mcone.replay.core.file;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReplayMetadata implements Serializable, eu.mcone.replay.core.api.file.ReplayMetadata {

    private List<String> dependencies = new ArrayList<>();

    public void addDependency(String dependency) {
        this.dependencies.add(dependency);
    }
}
