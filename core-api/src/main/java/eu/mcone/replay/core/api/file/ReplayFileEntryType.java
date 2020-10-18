package eu.mcone.replay.core.api.file;

import lombok.Getter;

public enum ReplayFileEntryType {

    REPLAY_METADATA("metadata"),
    REPLAY_DATA("data"),
    REPLAY_CHUNK("chunk:");

    @Getter
    private final String name;

    ReplayFileEntryType(String name) {
        this.name = name;
    }
}
