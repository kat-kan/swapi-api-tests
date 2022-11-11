package starships;

import base.BaseTest;
import io.qameta.allure.Severity;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.SeverityLevel.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class GetStarshipTest extends BaseTest {

    private static final int STARSHIPS_COUNT = 36;

    private static final String NAME = "Death Star";
    private static final String MODEL = "DS-1 Orbital Battle Station";
    private static final String MANUFACTURER = "Imperial Department of Military Research, Sienar Fleet Systems";

    private JsonPath json;

    @Test
    @Severity(BLOCKER)
    public void getAllStarships() {

        Response response = given()
                .when()
                .get(BASE_URL + STARSHIPS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        assertThat(json.getInt("count")).isEqualTo(STARSHIPS_COUNT);
    }

    @Test
    @Severity(BLOCKER)
    public void getOneStarshipByPathParam() {

        Response response = given()
                .pathParam("id", 9)
                .when()
                .get(BASE_URL + STARSHIPS + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        response.prettyPeek();
        compareStarshipObject("");
    }

    private void compareStarshipObject(String objectPath) {

        assertThat(json.getString(objectPath + "name")).isEqualTo(NAME);
        assertThat(json.getString(objectPath + "model")).isEqualTo(MODEL);
        assertThat(json.getString(objectPath + "manufacturer")).isEqualTo(MANUFACTURER);
    }

}
