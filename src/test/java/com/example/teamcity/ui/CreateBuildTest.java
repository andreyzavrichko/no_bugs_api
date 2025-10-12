package com.example.teamcity.ui;

import com.example.teamcity.api.annotations.UserSession;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.extensions.UserSessionListener;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.ProjectBuildsPage;
import com.example.teamcity.ui.pages.admin.CreateBuildConfigurationPage;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = {"Regression"})
@Listeners(UserSessionListener.class)
public class CreateBuildTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";
    private static final String ERROR_MESSAGE = "Build configuration name must not be empty";

    @Test(description = "User should be able to create build", groups = {"Positive"})
    @UserSession
    public void shouldCreateBuildConfiguration() {

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

        var buildType = request.getRequest(Endpoint.BUILD_TYPES)
                .readByName(testData.getBuildType().getName());
        softy.assertEquals(((BuildType) buildType).getName(), testData.getBuildType().getName(),
                "Build configuration name should match expected value");
    }

    @Test(description = "User should not be able to create build with empty name", groups = {"Negative"})
    @UserSession
    public void shouldNotCreateBuildWithEmptyName() {
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

        BuildType buildType = (BuildType) request.getRequest(Endpoint.BUILD_TYPES)
                .readByName("");
        softy.assertNull(buildType.getName(), "Build configuration should not exist in API after failed creation");
    }
}
