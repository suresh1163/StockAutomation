package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.MediaEntityModelProvider;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;
import pageobject.stockPageObject;
import utilities.ExtendReport;
import utilities.ScreenshotUtil;
import utilities.WaitUtil;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class StockInfoTest {

    WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(StockInfoTest.class);
    private ExtentReports extent = ExtendReport.getInstance();
    private ExtentTest report;
    String stockName;
    stockPageObject stockPage = new stockPageObject(driver);


    @Parameters("browser")
    @BeforeClass
    public void setup(String browser) throws IOException {
        report = extent.createTest("tests.StockInfoTest");
        report.info("Setting up WebDriver and ChromeOptions");
        logger.info("Initializing WebDriver and ChromeOptions");
        switch (browser.toLowerCase()) {
            case "chrome": WebDriverManager.chromedriver().setup();
                // Set ChromeOptions for custom capabilities
                ChromeOptions options = new ChromeOptions();
                options.setBinary("C:/Users/suresdev/Downloads/chrome-win64/chrome-win64/chrome.exe");
                driver = new ChromeDriver(options);
                break;
            case "firefox": WebDriverManager.firefoxdriver().setup(); driver = new FirefoxDriver(); break;
            case "edge": WebDriverManager.edgedriver().setup(); driver = new EdgeDriver(); break;
            default: throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        logger.info(browser+" Browser launched successfully");
        report.info(browser+" Browser launched successfully");

        report.info("Navigating to NSE India");
        logger.info("Navigating to NSE India");
        driver.get("https://www.nseindia.com/");
        stockPage = new stockPageObject(driver);

        String preScreenshot = ScreenshotUtil.takeScreenshot(driver, "preScreenshot");
        report.pass("Homepage Screenshot",MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, "preScreenshot")).build());
    }

    @BeforeMethod
    public void openStock() throws IOException, InterruptedException {
    //    stockName = "TATAMOTORS";
     //    stockName = "RCOM";
        stockName = "TATAMTRDVR";
        logger.info("Searching for "+stockName);
        report.info("Searching for "+stockName);
        stockPage.searchStock(stockName);
        WaitUtil.waitForVisibility(driver, stockPage.getLogo());
        new Actions(driver).moveToElement(stockPage.getLogo()).perform();
    }

    @Test
    public void verifyStockPrice() throws InterruptedException, IOException {
        // Validate stock information is displayed
        String stockPrice = stockPage.getStockPrice().getText();
        if (stockPrice.isBlank()||stockPrice.contains("-")) {
            logger.error("Stock price not found!");
            report.fail("Stock price not found!",MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, "postScreenshot")).build());
            Assert.fail("Stock price is blank or not available.");
        } else {
            logger.info("Stock price for "+stockName+": {}", stockPrice);
            report.pass("Stock price for "+stockName+": "+ stockPrice,MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, "postScreenshot")).build());
            Assert.assertTrue(stockPrice.matches("[\\d,]+(\\.\\d+)?"), "Stock price available");
        }

    }

    @Test
    public void verifyStock52WeekHigh() throws InterruptedException, IOException {
        Thread.sleep(1000);
        WaitUtil.waitForVisibility(driver, stockPage.getWeek52High());
        String high52week = stockPage.getWeek52High().getText();
        if (high52week.isBlank()||high52week.contains("-")) {
            logger.error("52 week high price is missing!");
            report.fail("52 week high price is missing!",MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, "postScreenshot")).build());
            Assert.fail("52 week high price is blank or not available.");
        } else {
            logger.info("52 week high price for "+stockName+": {}", high52week);
            report.pass("52 week high price for "+stockName+ ": "+ high52week, MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, "postScreenshot")).build());
            Assert.assertTrue(high52week.matches("[\\d,]+(\\.\\d+)?"), "52 week high price available");
        }

    }

    @Test
    public void verifyStock52WeekLow() throws InterruptedException, IOException {
        WaitUtil.waitForVisibility(driver, stockPage.getWeek52Low());
        String low52week = stockPage.getWeek52Low().getText();
        if (low52week.isBlank()||low52week.contains("-")) {
            logger.error("52 week low price is missing!");
            report.fail("52 week low price is missing!",MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, "postScreenshot")).build());
            Assert.fail("52 week low price is blank or not available.");
        } else {
            logger.info("52 week low price for "+stockName+": {}", low52week);
            report.pass("52 week low price for "+stockName+ ": "+ low52week, MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, "postScreenshot")).build());
            Assert.assertTrue(low52week.matches("[\\d,]+(\\.\\d+)?"), "52 week low price available");
        }
    }

    @AfterMethod
    public void takeFinalScreenshotAndNavigateToHomePage() throws IOException {
        ScreenshotUtil.takeScreenshot(driver, "postScreenshot");
        extent.flush();
        stockPage.clickLogoToNavigateHome();
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) {
            report.info("Closing browser");
            logger.info("Closing the browser");
            driver.quit();
        }
        extent.flush();  // Write everything to the report
    }
}
