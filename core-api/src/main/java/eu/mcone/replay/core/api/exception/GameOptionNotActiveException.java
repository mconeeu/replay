package eu.mcone.replay.core.api.exception;

public class GameOptionNotActiveException extends Exception {

    public GameOptionNotActiveException() {
        super();
    }

    public GameOptionNotActiveException(String message) {
        super(message);
    }

    public GameOptionNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameOptionNotActiveException(Throwable cause) {
        super(cause);
    }

}
