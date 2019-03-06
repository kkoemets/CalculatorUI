package com.joller.calculator;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

/**
 *  Calculator implementation in Excel
 */
class CalculatorImplExcel {

    public double calculate(String formula) throws IOException {
        Workbook workbook = getWorkbook();
        int rowIndex = 0;
        int columnIndex = 0;

        Row row = createSheetAndGetRow(workbook, rowIndex);
        setFormulaIntoCell(formula, row, columnIndex);

        Row updatedRow = updateWorkbookAndGetRows(workbook, rowIndex);
        // getting the calculated value and returning it
        return updatedRow.getCell(columnIndex).getNumericCellValue();
    }

    private Row updateWorkbookAndGetRows(Workbook workbook, int rowIndex) {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();

        Workbook updatedWorkbook = workbook;

        Sheet updatedSheet = updatedWorkbook.getSheetAt(0);
        return updatedSheet.getRow(rowIndex);
    }

    public double[] calculate(String[] formulas) throws IOException {
        Workbook workbook = getWorkbook();
        int rowIndex = 0;

        Row row = createSheetAndGetRow(workbook, rowIndex);

        for (int i = 0; i < formulas.length; i++) {
            setFormulaIntoCell(formulas[i], row, i);
        }

        Row updatedRow = updateWorkbookAndGetRows(workbook, rowIndex);

        double[] answers = new double[formulas.length];
        for (int i = 0; i < formulas.length; i++) {
            answers[i] = updatedRow.getCell(i).getNumericCellValue();
        }
        return answers;
    }

    private void setFormulaIntoCell(String formula, Row row, int columnIndex) {
        // setting the to be calculated formula to cell(0,0)
        row.createCell(columnIndex).setCellFormula(formula);
    }

    private Row createSheetAndGetRow(Workbook workbook, int rowIndex) {
        Sheet sheet = getSheet(workbook);
        return getRow(sheet, rowIndex);
    }

    private Row getRow(Sheet sheet, int rowIndex) {
        return sheet.createRow(rowIndex);
    }

    private Sheet getSheet(Workbook workbook) {
        // creating a new sheet
        return workbook.createSheet("Sheet1");
    }

    private Workbook getWorkbook() {
        // creating new xlsx workbook
        return new XSSFWorkbook();
    }
}