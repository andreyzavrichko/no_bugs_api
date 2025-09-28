package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.example.teamcity.ui.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.*;

public abstract class CreateBasePage extends BasePage {
    protected static final String CREATE_URL = "/admin/createObjectMenu.html?projectId=%s&showMode=%s";


    protected SelenideElement urlInput = $("#url");
    protected SelenideElement submitButton = $(byAttribute("value", "Proceed"));
    protected SelenideElement buildTypeNameInput = $("#buildTypeName");
    protected SelenideElement connectionSuccessfulMessage = $(".connectionSuccessful");

    @Step("Create base page")
    protected void baseCreateForm(String url) {

        waitForDocumentReady(Duration.ofSeconds(10));


        if (isLoginPageDisplayed() || isAccessDeniedPage()) {
            dumpDebugInfo();
            throw new IllegalStateException("Create page opened not as expected. Maybe not logged in or lack of admin privileges. Current URL: "
                    + WebDriverRunner.getWebDriver().getCurrentUrl());
        }


        SelenideElement realUrl = findUrlInputWithFrames();


        realUrl.shouldBe(Condition.visible, BASE_WAITING);
        realUrl.val(url);
        submitButton.click();
        connectionSuccessfulMessage.should(Condition.appear, BASE_WAITING);
    }

    private void waitForDocumentReady(Duration timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), timeout);
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {

        }
    }

    private boolean isLoginPageDisplayed() {
        // адаптируй селекторы под твою страницу логина
        return $("form[action*='/login']").exists()
                || $("input[name='username']").exists()
                || $("input[name='password']").exists();
    }

    private boolean isAccessDeniedPage() {
        return $$("h1, h2, p").findBy(Condition.matchText("Access Denied|Not authorized|403|Permission")).exists();
    }

    private SelenideElement findUrlInputWithFrames() {

        if (urlInput.exists()) return urlInput;
        if ($("input[name='url']").exists()) return $("input[name='url']");


        ElementsCollection frames = $$("iframe");
        for (SelenideElement frame : frames) {
            try {
                WebDriverRunner.getWebDriver().switchTo().frame(frame.getWrappedElement());
                if ($("#url").exists()) return $("#url");
                if ($("input[name='url']").exists()) return $("input[name='url']");
            } catch (Exception e) {
                // если frame недоступен — пропускаем
            } finally {
                WebDriverRunner.getWebDriver().switchTo().defaultContent();
            }
        }

        return urlInput;
    }

    private void dumpDebugInfo() {
        try {
            System.err.println("=== DEBUG CreateBasePage ===");
            System.err.println("Current URL: " + WebDriverRunner.getWebDriver().getCurrentUrl());
            System.err.println("Title: " + WebDriverRunner.getWebDriver().getTitle());
            String src = WebDriverRunner.getWebDriver().getPageSource();
            assert src != null;
            System.err.println("PageSource length: " + src.length());
            System.err.println("PageSource head (first 2000 chars):");
            System.err.println(src.substring(0, Math.min(2000, src.length())));
            // делаем скриншот в /build/reports/tests (Selenide сохранит картинку)
            screenshot("debug_create_project_page");
        } catch (Exception ex) {
            System.err.println("Failed to dump debug info: " + ex.getMessage());
        }
    }
}
