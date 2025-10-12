package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildConfigurationPage extends CreateBasePage {
    private static final String BUILD_TYPE_SHOW_MODE = "createBuildTypeMenu";

    private final SelenideElement buildConfigurationNameInput = $("#buildTypeName");
    @Getter
    public SelenideElement buildConfigurationNameInputError = $("#error_buildTypeName");

    @Step("Open create build configuration page")
    public static CreateBuildConfigurationPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_TYPE_SHOW_MODE), CreateBuildConfigurationPage.class);
    }

    @Step("Create form")
    public CreateBuildConfigurationPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    @Step("Set build configuration name")
    public CreateBuildConfigurationPage setBuildConfigurationName(String buildName) {
        buildConfigurationNameInput.val(buildName);
        submitButton.click();
        return this;
    }


}
