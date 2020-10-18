package eu.mcone.replay.core.api.exception;

public class ReplaySessionNotFoundException extends Exception {

    public ReplaySessionNotFoundException() {
        super();
    }

    public ReplaySessionNotFoundException(String message) {
        super(message);
    }

    public ReplaySessionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReplaySessionNotFoundException(Throwable cause) {
        super(cause);
    }
}
