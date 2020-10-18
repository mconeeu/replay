package eu.mcone.replay.core.api.exception;

public class ReplayPlayerAlreadyExistsException extends Exception {

    public ReplayPlayerAlreadyExistsException() {
        super();
    }

    public ReplayPlayerAlreadyExistsException(String message) {
        super(message);
    }

    public ReplayPlayerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReplayPlayerAlreadyExistsException(Throwable cause) {
        super(cause);
    }

}
