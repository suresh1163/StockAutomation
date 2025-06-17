package utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {

    public static List<Object[]> readStockData(String excelPath) {
        List<Object[]> testData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(excelPath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            rows.next(); // skip header row

            while (rows.hasNext()) {
                Row row = rows.next();
                String testCaseId = row.getCell(0).getStringCellValue();
                String stockName = row.getCell(1).getStringCellValue();
                double expectedMin = row.getCell(2).getNumericCellValue();
                double expectedMax = row.getCell(3).getNumericCellValue();
                String notes = row.getCell(4).getStringCellValue();

                testData.add(new Object[]{testCaseId, stockName, expectedMin, expectedMax, notes});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return testData;
    }
}
