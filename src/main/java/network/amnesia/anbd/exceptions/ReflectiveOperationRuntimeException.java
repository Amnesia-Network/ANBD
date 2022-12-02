package network.amnesia.anbd.exceptions;

public class ReflectiveOperationRuntimeException extends RuntimeException {
    public ReflectiveOperationRuntimeException(Throwable cause) {
        super(cause);
    }

    public ReflectiveOperationRuntimeException() {
        super();
    }

    public ReflectiveOperationRuntimeException(String message) {
        super(message);
    }

    public ReflectiveOperationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ReflectiveOperationRuntimeException(String message, Throwable cause, boolean enableSuppression,
                                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
