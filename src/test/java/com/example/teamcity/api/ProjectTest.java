package com.example.teamcity.api;

import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.validators.ValidationResponseSpecifications;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.example.teamcity.api.generators.RandomData.getString;
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

    @Test(description = "User should be able to create a project if id includes repeating symbols", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithRepeatingIdSymbolsTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = generate(Project.class);
        project.setId("aaa111aaa111");
        userCheckRequests.<Project>getRequest(PROJECTS).create(project);

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(project.getId());
        softy.assertEquals(createdProject, project);
    }

    @Test(description = "User should be able to create a project if id has 225 symbols", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWith225IdSymbolsTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = generate(Project.class);
        project.setId("a".repeat(225));
        userCheckRequests.<Project>getRequest(PROJECTS).create(project);

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(project.getId());
        softy.assertEquals(createdProject, project);
    }

    @Test(description = "User should be able to create a project if id includes latin letters, digits", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithLatinAndDigitsIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = generate(Project.class);
        project.setId("abc123XYZ789");
        userCheckRequests.<Project>getRequest(PROJECTS).create(project);

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(project.getId());
        softy.assertEquals(createdProject, project);
    }

    @Test(description = "User should be able to create a project if id includes 1 valid symbol", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithOneSymbolIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = generate(Project.class);
        project.setId("a");
        userCheckRequests.<Project>getRequest(PROJECTS).create(project);

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(project.getId());
        softy.assertEquals(createdProject, project);
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

    @Test(description = "User should be able to create project with long name", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithLongNameTest() {
        var longNameProject = generate(Project.class);
        longNameProject.setName(generate() + "x".repeat(256));

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(longNameProject);

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(longNameProject.getId());

        softy.assertEquals(longNameProject.getName(), createdProject.getName(), "Project name is not correct for long name");
    }

    @Test(description = "User should be able to create a project if name has cyrillic symbols", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithCyrillicNameTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = generate(Project.class);
        project.setName("ПроектТест");
        userCheckRequests.<Project>getRequest(PROJECTS).create(project);

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(project.getId());
        softy.assertEquals(createdProject, project);
    }

    @Test(description = "User should be able to create a project with 'copyAllAssociatedSettings' false", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithCopyAllNullTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = generate(Project.class);
        project.setCopyAllAssociatedSettings(null);
        userCheckRequests.<Project>getRequest(PROJECTS).create(project);

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(project.getId());
        softy.assertEquals(createdProject, project);
    }

    @Test(description = "User should be able to create a copy of a project", groups = {"Positive", "CRUD"})
    public void userCreatesProjectCopyTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var sourceProject = testData.getProject();
        userCheckRequests.<Project>getRequest(PROJECTS).create(sourceProject);

        var copyProject = generate(Project.class);
        copyProject.setId(getString());
        copyProject.setName(sourceProject.getName() + "_copy");
        userCheckRequests.<Project>getRequest(PROJECTS).create(copyProject);

        var createdCopyProject = userCheckRequests.<Project>getRequest(PROJECTS).read(copyProject.getId());
        softy.assertEquals(createdCopyProject, copyProject);
    }

    @Test(description = "User should not be able to create a project with empty id", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithEmptyIdTest() {
        var emptyIdProject = generate(Project.class);
        emptyIdProject.setId("");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(emptyIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithEmptyId());
    }

    @Test(description = "User should not be able to create a project if id starts with number", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithIdStartingWithNumberTest() {
        var invalidIdProject = generate(Project.class);
        invalidIdProject.setId("1abc");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithStartWithNonLetterId("1abc"));
    }

    @Test(description = "User should not be able to create a project if id includes invalid symbols", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithInvalidSymbolsIdTest() {
        var invalidIdProject = generate(Project.class);
        invalidIdProject.setId("test@id");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithInvalidSymbolId("test@id"));
    }

    @Test(description = "User should not be able to create a project if id cyrillic symbols", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithCyrillicIdTest() {
        var invalidIdProject = generate(Project.class);
        invalidIdProject.setId("тест");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithNonLatinId("тест"));
    }

    @Test(description = "User should not be able to create a project if id has more than 225 symbols", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithOver225IdSymbolsTest() {
        var invalidIdProject = generate(Project.class);
        invalidIdProject.setId("a".repeat(226)); // Более 225 символов

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWith256Id("a".repeat(226)));
    }

    @Test(description = "User should not be able to create a project if id starts with _", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithIdStartingWithUnderscoreTest() {
        var invalidIdProject = generate(Project.class);
        invalidIdProject.setId("_test");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectNonLetterId("_test"));
    }

    @Test(description = "User should not be able to create a project with empty id and name", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithEmptyIdAndNameTest() {
        var emptyProject = generate(Project.class);
        emptyProject.setId("");
        emptyProject.setName("");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(emptyProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithEmptyName());
    }

    @Test(description = "User should not be able to create a project with invalid id and empty name", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithInvalidIdAndEmptyNameTest() {
        var invalidProject = generate(Project.class);
        invalidProject.setId("invalid#id");
        invalidProject.setName("");

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithEmptyProjectName("invalid#id"));
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
