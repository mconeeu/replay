package eu.mcone.replay.core.api.exception;

public class NoReplayDataFoundException extends Exception {

    public NoReplayDataFoundException() {
        super();
    }

    public NoReplayDataFoundException(String message) {
        super(message);
    }

    public NoReplayDataFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoReplayDataFoundException(Throwable cause) {
        super(cause);
    }
}
