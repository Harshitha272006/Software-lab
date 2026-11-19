package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductsPage {

    WebDriver driver;

    By productTitle = By.className("title");
    By backpackButton = By.id("add-to-cart-sauce-labs-backpack");
    By cartBadge = By.className("shopping_cart_badge");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void addBackpackToCart() {
        driver.findElement(backpackButton).click();
    }

    public String getProductTitle() {
        return driver.findElement(productTitle).getText();
    }

    public String getCartCount() {
        return driver.findElement(cartBadge).getText();
    }
}