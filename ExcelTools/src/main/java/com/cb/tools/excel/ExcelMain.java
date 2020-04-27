package com.cb.tools.excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelMain {
  public static final String PATH = "./dummy.xlsx";

  public static void main(String[] args) throws IOException {
    List<String> sheetNames = new ArrayList<>(
        Arrays.asList("sheet 1", "sheet 2", "sheet 3"));

    ExcelParser excelParser = ExcelParser.getParserInstance();
    XSSFWorkbook workbook = excelParser.readXlsxFile(PATH);

    for (String sheetName : sheetNames) {
      XSSFSheet sh = excelParser.fetchSheetByName(workbook, sheetName);
      System.out.println(excelParser.readCellValueAt(sh, "B3"));
    }

  }
}
