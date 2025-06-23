package tests;

import org.testng.annotations.Factory;
import org.testng.annotations.Parameters;
import utilities.ExcelReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockTestFactory {
//    @Factory
//    public Object[] createInstances() {
//        Map<String, Map<String, String>> fullData = ExcelReader.getExcelData();
//
//        return fullData.values().stream()
//                .map(StockInfoTest::new)
//                .toArray(Object[]::new);
//    }
        @Factory
        @Parameters("browser")
        public Object[] createInstances(String browser) {
            Map<String, Map<String, String>> fullData = ExcelReader.getExcelData();
            List<Object> testInstances = new ArrayList<>();
            for (Map<String, String> data : fullData.values()) {
                testInstances.add(new StockInfoTest(data, browser));
            }

            return testInstances.toArray();
        }

}
