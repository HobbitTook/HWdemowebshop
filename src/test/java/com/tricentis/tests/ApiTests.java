package com.tricentis.tests;


import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class ApiTests extends TestsConfiguration {
    Faker faker = new Faker();

    public String
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName(),
            email = faker.internet().emailAddress(),
            password = faker.internet().password();
    final String tokenName = "__RequestVerificationToken";
    final String authCookieName = "NOPCOMMERCE.AUTH";

    @Test
    @DisplayName("Регистрация пользователя через API и проверка через UI")
    void registrationAndCheckApiAndUi() {
        step("Получение токена и регистрация пользователя через API", () -> {
            Response register =
                    given()
                            .log().all()
                            .when()
                            .get("/register")
                            .then()
                            .log().all()
                            .extract()
                            .response();

            String registryToken = register.htmlPath().getString("**.find{it.@name == '__RequestVerificationToken'}.@value");

            String registration =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Gender", "M")
                            .formParam("FirstName", firstName)
                            .formParam("LastName", lastName)
                            .formParam("Email", email)
                            .formParam("Password", password)
                            .formParam("ConfirmPassword", password)
                            .formParam(tokenName, registryToken)
                            .cookies(register.cookies())
                            .when()
                            .post("/register")
                            .then()
                            .statusCode(302)
                            .extract()
                            .cookie(authCookieName);

            step("Open minimal content, because cookie can be set when site is opened", () ->
                    open("/Themes/DefaultClean/Content/images/logo.png"));

            step("Set cookie to to browser", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", registration)));
        });
        step("Open main page", () ->
                open(""));

        step("Verify successful authorization", () ->
                $(".account").shouldHave(text(email)));
    }
    @Test
    @DisplayName("Логин через API и редактирование профиля через UI")
    void editProfileApiAndUi() {
        step("Логин через API, получение cookie", () -> {
            String loginCookie = given()

                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .formParam("Email", "1234@mai.riu")
                    .formParam("Password", "123456")
                    .when()
                    .post("/login")
                    .then()
                    .statusCode(302)
                    .extract()
                    .cookie(authCookieName);
            step("Open minimal content, because cookie can be set when site is opened", () ->
                    open("/Themes/DefaultClean/Content/images/logo.png"));

            step("Set cookie to to browser", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", loginCookie)));
        });
        step("Open edit profile page", () ->
                open("/customer/info"));
        step("Редактирование данных и сохранение", () -> {
            $("#gender-male").click();
            $("#FirstName").setValue(firstName);
            $("#LastName").setValue(lastName);
            $("[value='Save']").click();
        });
        step("Проверка данных после редактирования", () -> {
            $("#FirstName").shouldHave(value(firstName));
            $("#LastName").shouldHave(value(lastName));
        });
    }
}