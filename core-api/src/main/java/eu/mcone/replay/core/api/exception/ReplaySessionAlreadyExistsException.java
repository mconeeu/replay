package eu.mcone.replay.core.api.exception;

public class ReplaySessionAlreadyExistsException extends Exception {

    public ReplaySessionAlreadyExistsException() {
        super();
    }

    public ReplaySessionAlreadyExistsException(String message) {
        super(message);
    }

    public ReplaySessionAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReplaySessionAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
