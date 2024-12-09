package model.exceptions;

public class ExporterException extends Exception {
    public ExporterException(String message) {
        super(message);
    }

    public ExporterException(String message, Throwable cause) {
        super(message, cause);
    }
}
