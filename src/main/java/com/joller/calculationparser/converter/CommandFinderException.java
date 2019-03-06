package com.joller.calculationparser.converter;

public class CommandFinderException extends RuntimeException {

    private static final long serialVersionUID = -349662272827325125L;

    private static final String errorTextOnLine = " <<< Error: ";

    public CommandFinderException(final String faultyLine, final String message) {
        super(faultyLine + errorTextOnLine + message);
    }

    public CommandFinderException(final String message) {
        super(message);
    }

    public CommandFinderException(final Throwable cause) {
        super(cause);
    }

    public CommandFinderException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
