package eu.mcone.replay.core.api.file;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ReplayData<T> implements Serializable {

    private T data;

}
