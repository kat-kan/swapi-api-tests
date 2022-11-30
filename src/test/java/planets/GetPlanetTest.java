package planets;

import base.BaseTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.math.BigInteger;

import static io.qameta.allure.SeverityLevel.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Planets")
public class GetPlanetTest extends BaseTest {

    private final static int PLANETS_COUNT = 60;
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

    @Test(groups = "positive_tests")
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

    @Test(groups = "positive_tests")
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

    @Test(dataProvider = "queryParamData", groups = "positive_tests")
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

    @Test(groups = "negative_tests")
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

    @Test(groups = "negative_tests")
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

    @Ignore("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
    @Test(groups = "negative_tests")
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

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(json.getString(objectPath + "name"), NAME);
        softAssert.assertEquals(json.getString(objectPath + "rotation_period"), ROTATION_PERIOD);
        softAssert.assertEquals(json.getString(objectPath + "orbital_period"), ORBITAL_PERIOD);
        softAssert.assertEquals(json.getString(objectPath + "diameter"), DIAMETER);
        softAssert.assertEquals(json.getString(objectPath + "climate"), CLIMATE);
        softAssert.assertEquals(json.getString(objectPath + "gravity"), GRAVITY);
        softAssert.assertEquals(json.getString(objectPath + "terrain"), TERRAIN);
        softAssert.assertEquals(json.getString(objectPath + "surface_water"), SURFACE_WATER);
        softAssert.assertEquals(json.getString(objectPath + "population"), POPULATION);
        softAssert.assertEquals(json.getList(objectPath + "residents").size(), RESIDENTS_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "films").size(), FILMS_COUNT);
    }

    @DataProvider(name = "queryParamData")
    public Object[][] queryParamDataProvider() {
        String nameCaseInsensitive = "naboo";
        String partOfName = "Na";

        return new Object[][]{{nameCaseInsensitive}, {partOfName}, {NAME}};
    }
}
