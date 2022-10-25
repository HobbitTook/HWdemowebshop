package com.tricentis.helpers;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.remote.DesiredCapabilities;

public class DriverConfiguration {
    public static void configure() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", true);
        Configuration.browserCapabilities = capabilities;


        Configuration.browser = System.getProperty("browser_name", "chrome");
        Configuration.browserVersion = System.getProperty("browser_version", "100.0");
        Configuration.browserSize = System.getProperty("browser_size", "2560x1440");

        if (System.getProperty("selenide.remote") != null) {
            Configuration.remote = System.getProperty("selenide.remote");
        }
    }
}
