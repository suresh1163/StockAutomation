package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtendReport {

        private static ExtentReports extent;

        public static ExtentReports getInstance() {
            if (extent == null) {
                String currentDir = System.getProperty("user.dir");
                String reportPath = currentDir + "/test-output/extent-report.html";

             //   String reportPath = "C:/Users/Administrator/Desktop/Suresh/StockAutomation/test-output/extent-report.html";

                ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportPath);
                htmlReporter.config().setDocumentTitle("Automation Test Report");
                htmlReporter.config().setReportName("Selenium Test Results");
                htmlReporter.config().setTheme(Theme.STANDARD);

                extent = new ExtentReports();
                extent.attachReporter(htmlReporter);
                extent.setSystemInfo("OS", System.getProperty("os.name"));
                extent.setSystemInfo("Tester", "Suresh");
     //           extent.setSystemInfo("Browser", "Chrome");
            }
            return extent;
        }
}
