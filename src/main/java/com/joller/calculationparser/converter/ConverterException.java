package com.joller.calculationparser.converter;

public class ConverterException extends RuntimeException {

    private static final long serialVersionUID = -349662842439325125L;

    private static final String errorTextOnLine = " <<< Error: ";

    public ConverterException(final String faultyLine, final String message) {
        super(faultyLine + errorTextOnLine + message);
    }

    public ConverterException(final String message) {
        super(message);
    }

    public ConverterException(final Throwable cause) {
        super(cause);
    }

    public ConverterException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
