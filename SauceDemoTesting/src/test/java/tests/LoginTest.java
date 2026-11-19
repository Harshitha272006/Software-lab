package tests;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import pages.LoginPage;
import pages.ProductsPage;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    WebDriver driver;
    LoginPage loginPage;
    ProductsPage productsPage;

    // Screenshot method
    public void takeScreenshot(String fileName) {
        try {
            Files.copy(
                ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE).toPath(),
                Paths.get(System.getProperty("user.dir"), fileName),
                StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println(fileName + " saved successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");

        // Login page screenshot
        takeScreenshot("LoginPage.png");

        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void validLoginTest() {
        loginPage.login("standard_user", "secret_sauce");

        assertEquals("Products", productsPage.getProductTitle());

        // Products page screenshot
        takeScreenshot("ProductsPage.png");
    }

    @Test
    public void addBackpackToCartTest() {
        loginPage.login("standard_user", "secret_sauce");
        productsPage.addBackpackToCart();
        assertEquals("1", productsPage.getCartCount());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid_user", "locked_out_user"})
    public void invalidUsernameTest(String username) {
        loginPage.login(username, "secret_sauce");
        assertTrue(loginPage.getErrorMessage().length() > 0);
    }

    @ParameterizedTest
    @CsvSource({
        "standard_user, wrong_password",
        "wrong_user, secret_sauce",
        "wrong_user, wrong_password"
    })
    public void invalidCredentialsTest(String username, String password) {
        loginPage.login(username, password);
        assertTrue(loginPage.getErrorMessage().length() > 0);
    }
    @Test
    public void validLoginTest() {
        loginPage.login("standard_user", "secret_sauce");

        assertEquals("Products", productsPage.getProductTitle());

        // Wait until product images are loaded
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        wait.until(d -> (Boolean) ((org.openqa.selenium.JavascriptExecutor) d)
            .executeScript(
                "return Array.from(document.querySelectorAll('.inventory_item_img img'))" +
                ".length > 0 && Array.from(document.querySelectorAll('.inventory_item_img img'))" +
                ".every(img => img.complete && img.naturalWidth > 0);"
            ));

        takeScreenshot("ProductsPage.png");
    }
}