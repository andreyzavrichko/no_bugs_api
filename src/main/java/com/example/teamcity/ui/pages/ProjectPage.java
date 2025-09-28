package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

public class ProjectPage extends BasePage {
    private static final String PROJECT_URL = "/project/%s";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final SelenideElement title = $("span[class*='ProjectPageHeader']");

    @Step("Open project page")
    public static ProjectPage open(String projectId) {
        return Selenide.open(PROJECT_URL.formatted(projectId), ProjectPage.class);
    }

    @Step("Get project title")
    public SelenideElement getTitle() {
        return title.shouldBe(Condition.visible, TIMEOUT);
    }


}
