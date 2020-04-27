package com.cb.tools.excel;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelParser {

  private ExcelParser() {}

  public static ExcelParser getParserInstance() {
    return new ExcelParser();
  }

  public XSSFWorkbook readXlsxFile(String path) throws IOException {
    return new XSSFWorkbook(path);
  }

  public XSSFSheet fetchSheetByName(XSSFWorkbook workbook, String sheetName) {
    return workbook.getSheet(sheetName);
  }

  public String readCellValueAt(XSSFSheet sheet, String cellId) {
    CellAddress cellAddress = new CellAddress(cellId);
    Row row = sheet.getRow(cellAddress.getRow());
    Cell cell = row.getCell(cellAddress.getColumn());

    return cell.getStringCellValue();
  }

}
