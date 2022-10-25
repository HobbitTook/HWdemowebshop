package com.tricentis.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.tricentis.helpers.AllureAttach;
import com.tricentis.helpers.DriverConfiguration;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public class TestsConfiguration {
    @BeforeAll
    static void settings() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
        DriverConfiguration.configure();
        RestAssured.baseURI = "https://demowebshop.tricentis.com";
        Configuration.baseUrl = "https://demowebshop.tricentis.com";
    }

    @AfterEach
    void addAttachments() {
        AllureAttach.screenshotAs("Screenshot");
        AllureAttach.pageSource();
        AllureAttach.browserConsoleLogs();
        if ((System.getProperty("selenide.remote") != null)) {
            AllureAttach.addVideo();
        }
    }
}