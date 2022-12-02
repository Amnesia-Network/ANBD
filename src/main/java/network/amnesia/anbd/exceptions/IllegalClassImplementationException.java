package network.amnesia.anbd.exceptions;

public class IllegalClassImplementationException extends RuntimeException {

    public IllegalClassImplementationException() {
        super();
    }

    public IllegalClassImplementationException(String message) {
        super(message);
    }

    public IllegalClassImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    protected IllegalClassImplementationException(String message, Throwable cause, boolean enableSuppression,
                                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IllegalClassImplementationException(Throwable cause) {
        super(cause);
    }
}
