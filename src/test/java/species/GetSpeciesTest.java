package species;

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

@Feature("Species")
public class GetSpeciesTest extends BaseTest {

    private final static int SPECIES_COUNT = 37;

    private final static int PEOPLE_OF_SPECIES_COUNT = 1;
    private final static int FILMS_COUNT = 2;

    private final static String NAME = "Quermian";
    private final static String CLASSIFICATION = "mammal";
    private final static String DESIGNATION = "sentient";
    private final static String AVG_HEIGHT = "240";
    private final static String SKIN_COLORS = "white";
    private final static String HAIR_COLORS = "none";
    private final static String EYE_COLORS = "yellow";
    private final static String AVG_LIFESPAN = "86";
    private final static String LANGUAGE = "Quermian";
    private final static String HOMEWORLD = "Quermia";

    private JsonPath json;

    @Test
    @Severity(BLOCKER)
    public void getAllSpecies() {

        Response response = given()
                .when()
                .get(BASE_URL + SPECIES)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getInt("count")).isEqualTo(SPECIES_COUNT);
    }

    @Test
    @Severity(BLOCKER)
    public void getOneSpeciesByPathParam() {

        Response response = given()
                .pathParam("id", 25)
                .when()
                .get(BASE_URL + SPECIES + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        response.prettyPeek();
        System.out.println(json.getList("people").size());
        compareSpeciesObject("");
    }

    @Test(dataProvider = "queryParamData")
    @Severity(BLOCKER)
    public void getOneSpeciesByQueryParam(String name) {

        Response response = given()
                .queryParam("search", name)
                .when()
                .get(BASE_URL + SPECIES)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compareSpeciesObject("results[0].");
    }

    @Test
    @Severity(NORMAL)
    public void getOneSpeciesByInvalidQueryParam() {

        String invalidName = "I don't exist";

        Response response = given()
                .queryParam("search", invalidName)
                .when()
                .get(BASE_URL + SPECIES)
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

        int nonExistingId = SPECIES_COUNT + 1;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL + SPECIES + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Ignore("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
    @Test
    @Severity(MINOR)
    public void getOnePlanetWithInvalidId() {

        BigInteger bigInvalidId = new BigInteger("214748364700000000000");

        given()
                .pathParam("id", bigInvalidId)
                .when()
                .get(BASE_URL + SPECIES + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private void compareSpeciesObject(String objectPath) {

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(json.getString(objectPath + "name"), NAME);
        softAssert.assertEquals(json.getString(objectPath + "classification"), CLASSIFICATION);
        softAssert.assertEquals(json.getString(objectPath + "designation"), DESIGNATION);
        softAssert.assertEquals(json.getString(objectPath + "average_height"), AVG_HEIGHT);
        softAssert.assertEquals(json.getString(objectPath + "skin_colors"), SKIN_COLORS);
        softAssert.assertEquals(json.getString(objectPath + "hair_colors"), HAIR_COLORS);
        softAssert.assertEquals(json.getString(objectPath + "eye_colors"), EYE_COLORS);
        softAssert.assertEquals(json.getString(objectPath + "average_lifespan"), AVG_LIFESPAN);
        softAssert.assertEquals(json.getString(objectPath + "language"), LANGUAGE);
        softAssert.assertEquals(json.getList(objectPath + "people").size(), PEOPLE_OF_SPECIES_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "films").size(), FILMS_COUNT);
        softAssert.assertEquals(getSpeciesHomeworld(json.getString(objectPath + "homeworld")), HOMEWORLD);
        softAssert.assertAll();
    }

    private String getSpeciesHomeworld(String homeworldUrl){
        Response response = given()
                .when()
                .get(homeworldUrl)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        JsonPath homeworldJsonPath = response.jsonPath();
        return homeworldJsonPath.getString("name");
    }

    @DataProvider(name = "queryParamData")
    public Object[][] queryParamDataProvider() {
        String nameCaseInsensitive = "quermiAN";
        String partOfName = "ermi";

        return new Object[][]{{nameCaseInsensitive}, {partOfName}, {NAME}};
    }
}
