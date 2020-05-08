package planets;

import base.BaseTest;
import io.qameta.allure.Severity;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static io.qameta.allure.SeverityLevel.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class GetPlanetTest extends BaseTest {

    private final static int PLANETS_COUNT = 61;
    private final static int RESIDENTS_COUNT = 11;
    private final static int FILMS_COUNT = 4;

    private final static String NAME = "Naboo";
    private final static String ROTATION_PERIOD = "26";
    private final static String ORBITAL_PERIOD = "312";
    private final static String DIAMETER = "12120";
    private final static String CLIMATE = "temperate";
    private final static String GRAVITY = "1 standard";
    private final static String TERRAIN = "grassy hills, swamps, forests, mountains";
    private final static String SURFACE_WATER = "12";
    private final static String POPULATION = "4500000000";

    private JsonPath json;

    @Test
    @Severity(BLOCKER)
    public void getAllPlanets() {

        Response response = given()
                .when()
                .get(BASE_URL + PLANETS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getInt("count")).isEqualTo(PLANETS_COUNT);
    }

    @Test
    @Severity(BLOCKER)
    public void getOnePlanetByPathParam() {

        Response response = given()
                .pathParam("id", 8)
                .when()
                .get(BASE_URL + PLANETS + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        comparePlanetObject("");
    }

    @ParameterizedTest(name = "name : {0}")
    @MethodSource("createQueryParamData")
    @Severity(BLOCKER)
    public void getOnePlanetByQueryParam(String name) {

        Response response = given()
                .queryParam("search", name)
                .when()
                .get(BASE_URL + PLANETS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        comparePlanetObject("results[0].");
    }

    @Test
    @Severity(NORMAL)
    public void getOnePlanetByInvalidQueryParam() {

        String invalidName = "I don't exist";

        Response response = given()
                .queryParam("search", invalidName)
                .when()
                .get(BASE_URL + PLANETS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        assertThat(json.getList("results").size()).isEqualTo(0);
    }

    @Test
    @Severity(MINOR)
    public void getOnePlanetWithNonExistingId() {

        int nonExistingId = PLANETS_COUNT + 1;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL + PLANETS + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Disabled("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
    @Test
    @Severity(MINOR)
    public void getOnePlanetWithInvalidId() {

        BigInteger bigInvalidId = new BigInteger("214748364700000000000");

        given()
                .pathParam("id", bigInvalidId)
                .when()
                .get(BASE_URL + PLANETS + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private void comparePlanetObject(String objectPath) {

        assertThat(json.getString(objectPath + "name")).isEqualTo(NAME);
        assertThat(json.getString(objectPath + "rotation_period")).isEqualTo(ROTATION_PERIOD);
        assertThat(json.getString(objectPath + "orbital_period")).isEqualTo(ORBITAL_PERIOD);
        assertThat(json.getString(objectPath + "diameter")).isEqualTo(DIAMETER);
        assertThat(json.getString(objectPath + "climate")).isEqualTo(CLIMATE);
        assertThat(json.getString(objectPath + "gravity")).isEqualTo(GRAVITY);
        assertThat(json.getString(objectPath + "terrain")).isEqualTo(TERRAIN);
        assertThat(json.getString(objectPath + "surface_water")).isEqualTo(SURFACE_WATER);
        assertThat(json.getString(objectPath + "population")).isEqualTo(POPULATION);
        assertThat(json.getList(objectPath + "residents").size()).isEqualTo(RESIDENTS_COUNT);
        assertThat(json.getList(objectPath + "films").size()).isEqualTo(FILMS_COUNT);
    }

    private static Stream<Arguments> createQueryParamData() {

        String nameCaseInsensitive = "naboo";
        String partOfName = "Na";

        return Stream.of(
                Arguments.of(NAME),
                Arguments.of(nameCaseInsensitive),
                Arguments.of(partOfName)
        );
    }
}
