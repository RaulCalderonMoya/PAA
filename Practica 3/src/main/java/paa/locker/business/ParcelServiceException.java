package paa.locker.business;

public class ParcelServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ParcelServiceException(String message) {
        super(message);
    }

    public ParcelServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParcelServiceException(Throwable cause) {
        super(cause);
    }


}
