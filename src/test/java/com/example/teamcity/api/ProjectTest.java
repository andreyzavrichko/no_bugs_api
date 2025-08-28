package com.example.teamcity.api;

import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.validators.ValidationResponseSpecifications;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class ProjectTest extends BaseApiTest {

    @Test(description = "User should be able to create project", groups = {"Positive", "CRUD"})
    public void userCreatesProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(testData.getProject().getId());

        softy.assertEquals(createdProject, testData.getProject());
    }

    @Test(description = "User should not be able to create two projects with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoProjectsWithTheSameIdTest() {
        var projectWithSameId = generate(Project.class);
        projectWithSameId.setId(testData.getProject().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithSameId)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithIdAlreadyExist(testData.getProject().getId()));
    }

    @Test(description = "Unauthorized user should not be able to create project", groups = {"Negative", "CRUD"})
    public void unauthorizedUserCreatesProjectTest() {
        new UncheckedBase(Specifications.unauthSpec(), PROJECTS)
                .create(generate(Project.class))
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test(description = "User should not be able to create project with empty name", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithEmptyNameTest() {
        var emptyNameProject = generate(Project.class);
        emptyNameProject.setName("");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(emptyNameProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithEmptyName());
    }

    @Test(description = "User should not be able to create project with space name", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithSpaceNameTest() {
        var emptyNameProject = generate(Project.class);
        emptyNameProject.setName(" ");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(emptyNameProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithSpaceName());
    }

    @Test(description = "User should not be able to create two projects with the same name", groups = {"Negative", "CRUD"})
    public void userCreatesTwoProjectsWithTheSameNameTest() {
        var projectWithSameName = generate(Project.class);
        projectWithSameName.setName(testData.getProject().getName());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithSameName)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithNameAlreadyExist(testData.getProject().getName()));
    }

    @Test(description = "User should be able to create project with long name", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithLongNameTest() {
        var longNameProject = generate(Project.class);
        longNameProject.setName(generate() + "x".repeat(100));

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(longNameProject);

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(longNameProject.getId());

        softy.assertEquals(longNameProject.getName(), createdProject.getName(), "Project name is not correct for long name");
    }

    @Test(description = "User should not be able to create project with invalid ID", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithInvalidIdTest() {
        var invalidIdProject = generate(Project.class);
        invalidIdProject.setId("invalid#id");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithInvalidId("invalid#id"));
    }

}
