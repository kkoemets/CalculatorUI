package com.joller.calculationparser;

public final class ParserException extends RuntimeException {

    private static final long serialVersionUID = -649662195239325125L;

    private static final String errorTextOnLine = " <<< Error: ";


    public ParserException(final String message) {
        super(message);
    }

    public ParserException(final String faultyLine, final String message) {
        super(faultyLine + errorTextOnLine + message);
    }

    public ParserException(final Throwable cause) {
        super(cause);
    }

    public ParserException(final String message, final Throwable cause) {
        super(message, cause);
    }


}
