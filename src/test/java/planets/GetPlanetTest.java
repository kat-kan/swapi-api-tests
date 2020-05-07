package planets;

import base.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

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
    @Severity(SeverityLevel.BLOCKER)
    public void getAllPlanets(){

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
    @Severity(SeverityLevel.BLOCKER)
    public void getOnePlanetByPathParam(){

        Response response = given()
                .pathParam("id", 8)
                .when()
                .get(BASE_URL + PLANETS + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("");
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    public void getOnePlanetByQueryParam(){

        Response response = given()
                .queryParam("search", NAME)
                .when()
                .get(BASE_URL + PLANETS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("results[0].");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void getOnePlanetByPartOfQueryParam(){

        String partOfName = "Na";

        Response response = given()
                .queryParam("search", partOfName)
                .when()
                .get(BASE_URL + PLANETS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("results[0].");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void getOnePlanetByQueryParamCaseInsensitive(){

        String nameCaseInsensitive = "naboo";

        Response response = given()
                .queryParam("search", nameCaseInsensitive)
                .when()
                .get(BASE_URL + PLANETS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("results[0].");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void getOnePlanetByInvalidQueryParam(){

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
    @Severity(SeverityLevel.MINOR)
    public void getOnePlanetWithNonExistingId(){

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
    @Severity(SeverityLevel.MINOR)
    public void getOnePlanetWithInvalidId(){

        BigInteger bigInvalidId = new BigInteger("214748364700000000000");

        given()
                .pathParam("id", bigInvalidId)
                .when()
                .get(BASE_URL + PLANETS + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private void compare(String path){

        assertThat(json.getString(path + "name")).isEqualTo(NAME);
        assertThat(json.getString(path + "rotation_period")).isEqualTo(ROTATION_PERIOD);
        assertThat(json.getString(path + "orbital_period")).isEqualTo(ORBITAL_PERIOD);
        assertThat(json.getString(path + "diameter")).isEqualTo(DIAMETER);
        assertThat(json.getString(path + "climate")).isEqualTo(CLIMATE);
        assertThat(json.getString(path + "gravity")).isEqualTo(GRAVITY);
        assertThat(json.getString(path + "terrain")).isEqualTo(TERRAIN);
        assertThat(json.getString(path + "surface_water")).isEqualTo(SURFACE_WATER);
        assertThat(json.getString(path + "population")).isEqualTo(POPULATION);
        assertThat(json.getList(path + "residents").size()).isEqualTo(RESIDENTS_COUNT);
        assertThat(json.getList(path + "films").size()).isEqualTo(FILMS_COUNT);
    }
}
