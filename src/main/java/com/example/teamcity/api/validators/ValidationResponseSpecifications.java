package com.example.teamcity.api.validators;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ValidationResponseSpecifications {

    public static ResponseSpecification checkProjectWithIdAlreadyExist(String projectId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "Project ID \"" + projectId + "\" is already used by another project"
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithNameAlreadyExist(String projectName) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "Project with this name already exists: " + projectName
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithEmptyName() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString("name cannot be empty"));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithEmptyProjectName(String id) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString("Project name cannot be empty"));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithSpaceName() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString("Given project name is empty."));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithInvalidId(String invalidId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "Project ID \"" + invalidId + "\" is invalid: contains unsupported character '#'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)."
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithEmptyId() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "Project ID must not be empty."
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithStartWithNonLetterId(String invalidId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "Project ID \"" + invalidId + "\" is invalid: starts with non-letter character '1'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)."
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithInvalidSymbolId(String invalidId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "Project ID \"" + invalidId + "\" is invalid: contains unsupported character '@'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)."
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithNonLatinId(String invalidId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "Project ID \"" + invalidId + "\" is invalid: contains non-latin letter '" + invalidId.charAt(0) + "'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)."
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWith256Id(String invalidId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "Project ID \"" + invalidId + "\" is invalid: it is 226 characters long while the maximum length is 225. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)."
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectNonLetterId(String invalidId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "Project ID \"" + invalidId + "\" is invalid: starts with non-letter character '" + invalidId.charAt(0) + "'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)."
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectNotFoundById(String projectId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_NOT_FOUND);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.anyOf(
                Matchers.containsString("Project cannot be found by external id '" + projectId + "'"),
                Matchers.containsString("No project found by name or internal/external id '" + projectId + "'.")
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkAccessDeniedForCreateProject() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_FORBIDDEN);
        responseSpecBuilder.expectContentType(ContentType.TEXT);
        responseSpecBuilder.expectBody(Matchers.containsString(
                "You do not have \"Create subproject\" permission in project with internal id: _Root"
        ));
        return responseSpecBuilder.build();
    }


}