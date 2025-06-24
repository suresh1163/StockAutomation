package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
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
import utilities.ExcelReader;
import utilities.ExtendReport;
import utilities.ScreenshotUtil;
import utilities.WaitUtil;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class StockInfoTest {



    WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(StockInfoTest.class);
    private ExtentReports extent = ExtendReport.getInstance();
    private ExtentTest report;
    String stockName;
    stockPageObject stockPage = new stockPageObject(driver);
    private Map<String, String> testData;
    private String browser;

        public StockInfoTest(Map<String, String> testData,String browser) {
            this.testData = testData;
            this.browser = browser;
            this.stockName = testData.get("Stock_Name");
        }

        @BeforeClass
        @Parameters("browser")
        public void setup(String browser) throws IOException, InterruptedException {
            report = extent.createTest(browser.toUpperCase() + " | Stock: " + stockName);

            logger.info("Initializing browser for: {}", stockName);

            switch (browser.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
               //     ChromeOptions options = new ChromeOptions();
                //    options.setBinary("C:/Users/suresdev/Downloads/chrome-win64/chrome-win64/chrome.exe");
                //    driver = new ChromeDriver(options);
                    driver = new ChromeDriver();
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup(); driver = new FirefoxDriver(); break;
                case "edge":
                    WebDriverManager.edgedriver().setup(); driver = new EdgeDriver(); break;
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
            logger.info(browser+" Browser launched successfully");
            report.info(browser+" Browser launched successfully");

            driver.get("https://www.nseindia.com/");
            report.pass("Homepage Screenshot",MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, "preScreenshot")).build());
            String testCaseId = testData.get("Test_Case_ID");
            logger.info("Executing Test Case: " + testCaseId + " | Stock: " + stockName);
            report.info("Executing Test Case: " + testCaseId + " | Stock: " + stockName);
            stockPage = new stockPageObject(driver);
            stockPage.searchStock(stockName);
            WaitUtil.waitForVisibility(driver, stockPage.getLogo());
            new Actions(driver).moveToElement(stockPage.getLogo()).perform();
        }

        @Test(priority = 1)
        public void verifyStockPrice() throws IOException {
            // Validate stock Price field is displayed and contains numeric values
            WaitUtil.waitForNonEmptyText(driver, stockPage.getStockPriceLocator());
            String stockPrice = stockPage.getStockPrice().getText();
            if (stockPrice.isBlank()||stockPrice.contains("-")) {
                logger.error(stockName+" stock price not found!");
                report.fail(stockName+" stock price not found!",MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, stockName)).build());
                Assert.fail(stockName+" stock price is blank");
            } else {
                Assert.assertTrue(stockPrice.matches("[\\d,]+(\\.\\d+)?"), stockName+" stock price available");
                logger.info("Stock price for "+stockName+": {}", stockPrice);
                report.pass("Stock price for "+stockName+": "+ stockPrice,MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, stockName)).build());

            }
        }

    @Test(priority = 2)
        public void verifyStock52WeekHigh() throws IOException {
            // Validate stock 52 week high price field is displayed and contains numeric values
            WaitUtil.waitForNonEmptyText(driver, stockPage.getStockPriceLocator());
            String high52week = stockPage.getWeek52High().getText();
            if (high52week.isBlank()||high52week.contains("-")) {
                logger.error(stockName+" 52 week high price is missing!");
                report.fail(stockName+" 52 week high price is missing!",MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, stockName)).build());
                Assert.fail(stockName+" 52 week high price is blank");
            } else {
                Assert.assertTrue(high52week.matches("[\\d,]+(\\.\\d+)?"), stockName+" 52 week high price available");
                logger.info(stockName+" 52 week high price for "+stockName+": {}", high52week);
                report.pass(stockName+" 52 week high price for "+stockName+ ": "+ high52week, MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, stockName)).build());
            }
        }

        @Test(priority = 3)
        public void verifyStock52WeekLow() throws IOException {
            // Validate stock 52 week low price field is displayed and contains numeric values
            WaitUtil.waitForNonEmptyText(driver, stockPage.getStockPriceLocator());
            String low52week = stockPage.getWeek52Low().getText();
            if (low52week.isBlank()||low52week.contains("-")) {
                logger.error("52 week low price is missing!");
                report.fail("52 week low price is missing!",MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, stockName)).build());
                Assert.fail("52 week low price is blank");
            } else {
                Assert.assertTrue(low52week.matches("[\\d,]+(\\.\\d+)?"), "52 week low price available");
                logger.info("52 week low price for "+stockName+": {}", low52week);
                report.pass("52 week low price for "+stockName+ ": "+ low52week, MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, stockName)).build());

            }
        }

    @Test(priority = 4)
    public void validateProfitOrLoss() throws IOException {
        WaitUtil.waitForNonEmptyText(driver, stockPage.getStockPriceLocator());
        String currentPriceStr = stockPage.getStockPrice().getText().replace(",", "");

        try {
            double currentPrice = Double.parseDouble(currentPriceStr);
            double purchasedValue = Double.parseDouble(testData.get("Purchased_Value"));
            int quantity = Integer.parseInt(testData.get("Quantity"));

            double profitOrLoss = (currentPrice - purchasedValue) * quantity;
            String result = profitOrLoss >= 0 ? "Profit" : "Loss";

            String coloredResult = result.equals("Profit")
                    ? "<b><span style='color:green'>Profit: " + profitOrLoss + "</span></b>"
                    : "<b><span style='color:red'>Loss: " + profitOrLoss + "</span></b>";

            logger.info("Stock: {} | Purchased at: {} | Current Price: {} | Quantity: {} | {}: {}",
                    stockName, purchasedValue, currentPrice, quantity, result, profitOrLoss);

            report.pass(result + " calculated for " + stockName + ": " +coloredResult,
                    MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, stockName + "_ProfitLoss")).build());

            // Add assert for reporting
            Assert.assertTrue(true, result + ": " + profitOrLoss);

        } catch (NumberFormatException e) {
            logger.error("Failed to parse price or quantity: " + e.getMessage());
            report.fail("Error parsing input values for " + stockName,
                    MediaEntityBuilder.createScreenCaptureFromPath(ScreenshotUtil.takeScreenshot(driver, stockName + "_ParseError")).build());
            Assert.fail("Invalid data for calculation");
        }
    }

    @AfterMethod
    public void takeFinalScreenshotAndNavigateToHomePage() throws IOException {
        extent.flush();
    }

        @AfterClass(alwaysRun = true)
        public void tearDown() {
            if (driver != null) {
                report.info("Closing browser for " + stockName);
                logger.info("Closing the browserfor "+stockName);
                driver.quit();
            }
        }

    }
