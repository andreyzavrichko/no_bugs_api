package com.example.teamcity.api.validators;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ValidationResponseSpecifications {

    public static ResponseSpecification checkProjectWithIdAlreadyExist(String projectId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectBody("errors[0].message", Matchers.equalTo(
                "Project ID \"" + projectId + "\" is already used by another project"
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithNameAlreadyExist(String projectName) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectBody("errors[0].message", Matchers.equalTo(
                "Project with this name already exists: " + projectName
        ));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithEmptyName() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectBody(Matchers.containsString("name cannot be empty"));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithSpaceName() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectBody(Matchers.containsString("Given project name is empty."));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithInvalidId(String invalidId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectBody("errors[0].message", Matchers.equalTo(
                "Project ID \"" + invalidId + "\" is invalid: contains unsupported character '#'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)."
        ));
        return responseSpecBuilder.build();
    }
}