package com.joller.calculationparser.converter;

public class ResultContainer {

    private final String precision;
    private final String unit;
    private final String variableName;
    private final String unCalculatedEquationWithVariables;
    private final String unCalculatedEquationWithoutVariables;

    public ResultContainer(String precision, String unit, String variableName,
                           String unCalculatedEquationWithVariables,
                           String UnCalculatedEquationWithoutVariables) {
        this.precision = precision;
        this.unit = unit;
        this.variableName = variableName;
        this.unCalculatedEquationWithVariables = unCalculatedEquationWithVariables;
        this.unCalculatedEquationWithoutVariables = UnCalculatedEquationWithoutVariables;
    }

    public String getPrecision() {
        return precision;
    }

    public String getUnit() {
        return unit;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getUnCalculatedEquationWithVariables() {
        return unCalculatedEquationWithVariables;
    }

    public String getUnCalculatedEquationWithoutVariables() {
        return unCalculatedEquationWithoutVariables;
    }
}
