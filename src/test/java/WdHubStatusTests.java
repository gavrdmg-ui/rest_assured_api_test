import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;


public class WdHubStatusTests extends TestBase {
    @Test
    public void authorizationWithValidCredentialsRequestTest() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .get("/wd/hub/status")
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/wd_hub_response_schema.json"));
    }

    @Test
    public void unauthorizedRequestTest() {
        given()
                .log().all()
                .when()
                .get("/wd/hub/status")
                .then()
                .log().all()
                .statusCode(401)
                .body(containsString("Authorization Required"));
    }

    @CsvSource(value = {
            "user1,12345",
            "user2,1234"
    })
    @ParameterizedTest(name = "Авторизация с username: {0} password: {1}")
    public void authorizationWithInValidCredentialsTest(String userName, String password) {
        given()
                .log().all()
                .auth().basic(userName, password)
                .when()
                .get("/wd/hub/status")
                .then()
                .log().all()
                .statusCode(401)
                .body(containsString("Authorization Required"));
    }

    @Test
    public void checkMessageFieldValueTest() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .get("/wd/hub/status")
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/wd_hub_response_schema.json"))
                .body("value.message", startsWith("Selenoid v3.0.13 built at"));
    }

    @Test
    public void checkReadyFieldValueTest() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .get("/wd/hub/status")
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/wd_hub_response_schema.json"))
                .body("value.ready", is("true"));
    }

    @Test
    public void methodNotAllowTest() {
        given()
                .log().all()
                .auth().basic("user1", "1234")
                .when()
                .put("/wd/hub/session")
                .then()
                .log().all()
                .statusCode(405)
                .body(containsString("Method not allowed"));
    }
}
