package utilities;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.MediaEntityModelProvider;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ScreenshotUtil {

    public static String takeScreenshot(WebDriver driver, String fileName) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        if (!screenshot.exists()) {
            throw new IOException("Screenshot file was not created!");
        }
      //  String destPath = "C:/Users/Administrator/Desktop/Suresh/StockAutomation/test-output/screenshot/" + fileName + ".png";
        String currentDir = System.getProperty("user.dir");
        String destPath = currentDir + "/test-output/screenshot/" + fileName + ".png";
        File destFile = new File(destPath);
        Files.createDirectories(destFile.getParentFile().toPath()); // Ensure folder exists
        Files.copy(screenshot.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return destPath;
    }


}

