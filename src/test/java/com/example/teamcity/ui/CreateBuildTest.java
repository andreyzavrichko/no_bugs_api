package com.example.teamcity.ui;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.ProjectBuildsPage;
import com.example.teamcity.ui.pages.admin.CreateBuildConfigurationPage;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = {"Regression"})
public class CreateBuildTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";
    private static final String ERROR_MESSAGE = "Build configuration name must not be empty";

    @Test(description = "User should be able to create build", groups = {"Positive"})
    public void shouldCreateBuildConfiguration() {
        loginAs(testData.getUser());

        var request = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        Project project = request.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());

        CreateBuildConfigurationPage page = CreateBuildConfigurationPage.open(project.getId());
        page.createForm(REPO_URL).setBuildConfigurationName(testData.getBuildType().getName());

        List<String> buildNames = ProjectBuildsPage.open(project.getId())
                .getBuilds()
                .stream()
                .map(b -> b.getName().text())
                .toList();

        softy.assertTrue(buildNames.contains(testData.getBuildType().getName()),
                "Created build should appear in builds list");
    }

    @Test(description = "User should not be able to create build with empty name", groups = {"Negative"})
    public void shouldNotCreateBuildWithEmptyName() {
        loginAs(testData.getUser());

        var request = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        Project project = request.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());

        CreateBuildConfigurationPage page = CreateBuildConfigurationPage.open(project.getId());
        var form = page.createForm(REPO_URL);
        var errorElement = form.setBuildConfigurationName("").getBuildConfigurationNameInputError();

        softy.assertEquals(errorElement.getText(), ERROR_MESSAGE, "Error message should match expected");

        List<String> buildNames = ProjectBuildsPage.open(project.getId())
                .getBuilds()
                .stream()
                .map(b -> b.getName().text())
                .toList();

        softy.assertTrue(buildNames.isEmpty(), "No builds should be created with empty name");
    }
}
