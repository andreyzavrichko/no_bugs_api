package com.example.teamcity.api;

import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.validators.ValidationResponseSpecifications;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

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
        project.setId("aaa111aaa111" + getString());
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
        project.setId("abc123XYZ789" + getString());
        userCheckRequests.<Project>getRequest(PROJECTS).create(project);

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(project.getId());
        softy.assertEquals(createdProject, project);
    }

    @Test(description = "User should be able to create a project if id includes 1 valid symbol", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithOneSymbolIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var project = generate(Project.class);
        project.setId(getString(1));
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

    @Test(description = "User should not be able to create a copy of non existing project", groups = {"Negative", "CRUD"})
    public void userCreatesCopyOfNonExistingProjectTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var copyProject = generate(Project.class);
        copyProject.setId(getString());
        copyProject.setName(getString());
        copyProject.setSourceProject(new SourceProject(getString()));

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(copyProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectNotFoundById(
                        copyProject.getSourceProject().getLocator()
                ));
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
        invalidIdProject.setId("1" + getString());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithStartWithNonLetterId(invalidIdProject.getId()));
    }

    @Test(description = "User should not be able to create a project if id includes invalid symbols", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithInvalidSymbolsIdTest() {
        var invalidIdProject = generate(Project.class);
        invalidIdProject.setId("test@id" + getString());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWithInvalidSymbolId(invalidIdProject.getId()));
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
        invalidIdProject.setId("a".repeat(226));

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectWith256Id("a".repeat(226)));
    }

    @Test(description = "User should not be able to create a project if id starts with _", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithIdStartingWithUnderscoreTest() {
        var invalidIdProject = generate(Project.class);
        invalidIdProject.setId("_test" + getString());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(invalidIdProject)
                .then()
                .spec(ValidationResponseSpecifications.checkProjectNonLetterId(invalidIdProject.getId()));
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


    @DataProvider(name = "roleDataProvider")
    public Object[][] roleDataProvider() {
        return new Object[][]{
                {"PROJECT_VIEWER"},
                {"PROJECT_DEVELOPER"},
                {"AGENT_MANAGER"}
        };
    }

    @Test(description = "User should not be able to create project with limited role", groups = {"Negative", "Roles"},
            dataProvider = "roleDataProvider")
    public void userCreatesProjectWithLimitedRoleTest(String roleId) {

        var user = TestDataGenerator.generate(User.class);
        user.setRoles(new Roles(List.of(new Role(roleId, "g"))));
        superUserCheckRequests.getRequest(USERS).create(user);

        var project = generate(Project.class);
        project.setId(getString());
        new UncheckedBase(Specifications.authSpec(user), PROJECTS)
                .create(project)
                .then()
                .spec(ValidationResponseSpecifications.checkAccessDeniedForCreateProject());
    }

    @Test(description = "Unauthorized user should not be able to create project", groups = {"Negative", "CRUD"})
    public void unauthorizedUserCreatesProjectTest() {
        new UncheckedBase(Specifications.unauthSpec(), PROJECTS)
                .create(generate(Project.class))
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @DataProvider(name = "negativeNameProjectCreationDataProvider")
    public Object[][] negativeNameProjectCreationDataProvider() {
        return new Object[][]{
                {"User should not be able to create project with empty name", getString(), "", ValidationResponseSpecifications.checkProjectWithEmptyName()},
                {"User should not be able to create project with space name", getString(), " ", ValidationResponseSpecifications.checkProjectWithSpaceName()},
                {"User should not be able to create project with empty id and name", "", "", ValidationResponseSpecifications.checkProjectWithEmptyName()},
                {"User should not be able to create project with invalid id and empty name", "invalid#id", "", ValidationResponseSpecifications.checkProjectWithEmptyProjectName("invalid#id")}
        };
    }

    @Test(description = "User should not be able to create project with invalid name", groups = {"Negative", "CRUD"},
            dataProvider = "negativeNameProjectCreationDataProvider")
    public void userCreatesProjectWithInvalidNameTest(String description, String projectId, String projectName, ResponseSpecification spec) {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var project = generate(Project.class);
        project.setId(projectId);
        project.setName(projectName);
        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(project)
                .then()
                .spec(spec);
    }


}
