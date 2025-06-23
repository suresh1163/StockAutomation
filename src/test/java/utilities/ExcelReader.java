package utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.*;

public class ExcelReader {

    private static final String EXCEL_PATH = "src/test/resources/testdata/StockTestData.xlsx";
    private static final String SHEET_NAME = "Stock";

    // Map<TestCaseID, Map<ColumnName, CellValue>>
    public static Map<String, Map<String, String>> getExcelData() {
        Map<String, Map<String, String>> dataMap = new LinkedHashMap<>();
        try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            Row headerRow = sheet.getRow(0);

            // Map<ColumnName, ColumnIndex>
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                columnIndexMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String testCaseId = getCellValue(row.getCell(columnIndexMap.get("Test_Case_ID")));
                if (testCaseId.isEmpty()) continue;

                Map<String, String> rowData = new HashMap<>();
                for (String colName : columnIndexMap.keySet()) {
                    Cell cell = row.getCell(columnIndexMap.get(colName));
                    rowData.put(colName, getCellValue(cell));
                }

                dataMap.put(testCaseId, rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataMap;
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            case BLANK:
            default: return "";
        }
    }

    // Returns only TestCaseIds for the DataProvider
    public static Object[][] getTestCaseIds() {
        Map<String, Map<String, String>> fullData = getExcelData();
        Object[][] result = new Object[fullData.size()][1];
        int i = 0;
        for (String testCaseId : fullData.keySet()) {
            result[i++][0] = testCaseId;
        }
        return result;
    }

    // Fetch row data map by test case ID
    public static Map<String, String> getRowDataByTestCaseId(String testCaseId) {
        return getExcelData().get(testCaseId);
    }
}
