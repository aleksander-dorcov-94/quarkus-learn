package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.internal.path.json.JSONAssertion;
import io.restassured.response.Response;
import org.acme.rest.dto.CreateAddressRequest;
import org.acme.rest.dto.CreateCompanyRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompanyControllerTest {

    private static Long savedCompanyId;

    @Test
    @Order(1)
    public void testCreateCompany() {
        var request = new CreateCompanyRequest("Acme Corp");

        // Extract the full Response object
        Response response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/companies")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", is("Acme Corp"))
                .extract()
                .response();

        savedCompanyId = response.jsonPath().getLong("id");

        System.out.println("Extracted ID as Long: " + savedCompanyId);
    }

    @Test
    @Order(2)
    public void testAddAddress() {
        var request = new CreateAddressRequest(savedCompanyId, "Sofia", "Vitosha Blvd");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/companies/address")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("city", is("Sofia"))
                .body("street", is("Vitosha Blvd"));
    }
}
