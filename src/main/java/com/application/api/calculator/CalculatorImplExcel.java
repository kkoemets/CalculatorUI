package com.application.api.calculator;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CalculatorImplExcel {

    private final String DIRECTORY_XLSX_FILE = "src\\main\\java\\com\\application\\api\\calculator\\poi-generated-file.xlsx";

    public double calculate(String string) throws IOException {
        // creating new xlsx workbook
        Workbook workbook = new XSSFWorkbook();
        // creating a new sheet
        Sheet sheet = workbook.createSheet("Sheet1");
        Row row = sheet.createRow(0);
        // setting the to be calculated formula to cell(0,0)
        row.createCell(0).setCellFormula(string);
        // creating file output stream to xlsx file
        FileOutputStream fileOut = new FileOutputStream(DIRECTORY_XLSX_FILE);
        // writing to xlsx file
        workbook.write(fileOut);
        // closing the stream
        fileOut.close();
        // reading the xlsx file with file input stream
        FileInputStream file = new FileInputStream(new File(DIRECTORY_XLSX_FILE));
        // setting the xlsx workbook
        workbook = new XSSFWorkbook(file);
        // recalculating formulas
        FormulaEvaluator evaluator=workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();
        // getting the calculated value and returning it
        Sheet sheet1 = workbook.getSheetAt(0);
        Row row1 = sheet1.getRow(0);
        file.close();
        return row1.getCell(0).getNumericCellValue();
    }
}
