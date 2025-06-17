package pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utilities.WaitUtil;

public class stockPageObject {

    private WebDriver driver;

    // Constructor
    public stockPageObject(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ==== Elements ====

    @FindBy(xpath = "//input[contains(@class, 'rbt-input-main')]")
    private WebElement searchBox;

    @FindBy(id = "quoteLtp")
    private WebElement stockPrice;

    @FindBy(id = "week52highVal")
    private WebElement week52High;

    @FindBy(id = "week52lowVal")
    private WebElement week52Low;

    @FindBy(id = "logoURL")
    private WebElement logo;

    // ==== Actions ====

    public WebElement getLogo() {
        return logo;
    }

    public WebElement getWeek52Low() {
        return week52Low;
    }

    public WebElement getWeek52High() {
        return week52High;
    }

    public WebElement getStockPrice() {
        return stockPrice;
    }

    public WebElement getSearchBox() {
        return searchBox;
    }

    public void enterStockName(String stockName) throws InterruptedException {
        WaitUtil.waitForClickability(driver, searchBox);
        searchBox.sendKeys(stockName);
    }

    public WebElement stockFromSuggestions(String stockName) throws InterruptedException {
        return driver.findElement(By.xpath("//a[@role='option']//span[text()='" + stockName + "']"));
    }

    public void searchStock(String stockName) throws InterruptedException {
        enterStockName(stockName);
        WaitUtil.waitForPresence(driver, By.xpath("//a[@role='option']//span[text()='"+stockName+"']"));
        stockFromSuggestions(stockName).click();
    }

    public void clickLogoToNavigateHome() {
        new Actions(driver).moveToElement(logo).click().perform();
    }
}
