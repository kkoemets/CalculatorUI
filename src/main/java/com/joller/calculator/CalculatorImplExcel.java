package com.joller.calculator;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 *  Calculator implementation in Excel
 */
class CalculatorImplExcel {

    private final String DIRECTORY_XLSX_FILE = "poi-generated-file.xlsx";

    public double calculate(String formula) throws IOException {
        Workbook workbook = getWorkbook();
        int rowIndex = 0;
        int columnIndex = 0;

        Row row = createSheetAndGetRow(workbook, rowIndex);
        setFormulaIntoCell(formula, row, columnIndex);

        writeOutXlsxFile(workbook, DIRECTORY_XLSX_FILE);

        Workbook updatedWorkbook = readInXlsxFileAndUpdateAndGetWorkbook(DIRECTORY_XLSX_FILE);

        Sheet updatedSheet = updatedWorkbook.getSheetAt(0);
        Row updatedRow = updatedSheet.getRow(rowIndex);
        // getting the calculated value and returning it
        return updatedRow.getCell(columnIndex).getNumericCellValue();
    }

    public double[] calculate(String[] formulas) throws IOException {
        Workbook workbook = getWorkbook();
        int rowIndex = 0;

        Row row = createSheetAndGetRow(workbook, rowIndex);

        for (int i = 0; i < formulas.length; i++) {
            setFormulaIntoCell(formulas[i], row, i);
        }

        writeOutXlsxFile(workbook, DIRECTORY_XLSX_FILE);

        Workbook updatedWorkbook = readInXlsxFileAndUpdateAndGetWorkbook(DIRECTORY_XLSX_FILE);

        Sheet updatedSheet = updatedWorkbook.getSheetAt(0);
        Row updatedRow = updatedSheet.getRow(rowIndex);

        double[] answers = new double[formulas.length];
        for (int i = 0; i < formulas.length; i++) {
            answers[i] = updatedRow.getCell(i).getNumericCellValue();
        }
        return answers;
    }


    private Workbook readInXlsxFileAndUpdateAndGetWorkbook(String directory) throws IOException {
        // reading the xlsx file with file input stream
        FileInputStream file = new FileInputStream(new File(DIRECTORY_XLSX_FILE));
        // setting the xlsx workbook
        Workbook workbook = new XSSFWorkbook(file);
        file.close();
        // recalculating formulas
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();
        return workbook;
    }


    private void writeOutXlsxFile(Workbook workbook, String directory) throws IOException {
        // creating file output stream to xlsx file
        FileOutputStream fileOut = new FileOutputStream(directory);
        // writing to xlsx file
        workbook.write(fileOut);
        // closing the stream
        fileOut.close();
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
