package people;

import base.BaseTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.math.BigInteger;

import static io.qameta.allure.SeverityLevel.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("People")
public class GetPeopleTest extends BaseTest {

    private static final int PEOPLE_COUNT = 82;
    private static final int FILMS_COUNT = 2;
    private static final int SPECIES_COUNT = 0;
    private static final int VEHICLES_COUNT = 0;
    private static final int STARSHIPS_COUNT = 0;

    private static final String NAME = "R4-P17";
    private static final int HEIGHT = 96;
    private static final String MASS = "unknown";
    private static final String HAIR_COLOR = "none";
    private static final String SKIN_COLOR = "silver, red";
    private static final String EYE_COLOR = "red, blue";
    private static final String BIRTH_YEAR = "unknown";
    private static final String GENDER = "female";
    private static final String HOMEWORLD = "unknown";

    private JsonPath json;

    @Test(groups = "positive_tests")
    @Severity(BLOCKER)
    public void getAllPeople() {

        Response response = given()
                .when()
                .get(BASE_URL + PEOPLE)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        assertThat(json.getInt("count")).isEqualTo(PEOPLE_COUNT);
    }

    @Test(groups = "positive_tests")
    @Severity(BLOCKER)
    public void getOnePersonByPathParam() {

        Response response = given()
                .pathParam("id", 75)
                .when()
                .get(BASE_URL + PEOPLE + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        comparePeopleObject("");
    }

    @Test(dataProvider = "queryParamData", groups="positive_tests")
    @Severity(BLOCKER)
    public void getOnePersonByQueryParam(String name) {

        Response response = given()
                .queryParam("search", name)
                .when()
                .get(BASE_URL + PEOPLE)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        comparePeopleObject("results[0].");
    }


    @Test(groups = "negative_tests")
    @Severity(NORMAL)
    public void getOnePersonByInvalidQueryParam() {

        String invalidName = "I don't exist";

        Response response = given()
                .queryParam("search", invalidName)
                .when()
                .get(BASE_URL + PEOPLE)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        assertThat(json.getList("results").size()).isEqualTo(0);
    }

    @Test(groups = "negative_tests")
    @Severity(MINOR)
    public void getOnePersonWithNonExistingId() {

        int nonExistingId = PEOPLE_COUNT + 2;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL + PEOPLE + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Test(groups = "negative_tests")
    @Severity(MINOR)
    public void getOnePersonWithInvalidId() {

        BigInteger bigInvalidId = new BigInteger("534342320438432350");

        given()
                .pathParam("id", bigInvalidId)
                .when()
                .get(BASE_URL + PEOPLE + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private void comparePeopleObject(String objectPath) {

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(json.getString(objectPath + "name"), NAME);
        softAssert.assertEquals(json.getInt(objectPath + "height"), HEIGHT);
        softAssert.assertEquals(json.getString(objectPath + "mass"), MASS);
        softAssert.assertEquals(json.getString(objectPath + "hair_color"), HAIR_COLOR);
        softAssert.assertEquals(json.getString(objectPath + "skin_color"), SKIN_COLOR);
        softAssert.assertEquals(json.getString(objectPath + "eye_color"), EYE_COLOR);
        softAssert.assertEquals(json.getString(objectPath + "birth_year"), BIRTH_YEAR);
        softAssert.assertEquals(json.getString(objectPath + "gender"), GENDER);
        softAssert.assertEquals(getPeopleHomeworld(json.getString(objectPath + "homeworld")), HOMEWORLD);
        softAssert.assertEquals(json.getList(objectPath + "films").size(), FILMS_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "starships").size(), STARSHIPS_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "vehicles").size(), VEHICLES_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "species").size(), SPECIES_COUNT);
        softAssert.assertAll();
    }

    private String getPeopleHomeworld(String homeworldUrl) {
        return given()
                .when()
                .get(homeworldUrl)
                .jsonPath().getString("name");
    }

    @DataProvider(name = "queryParamData")
    private Object[][] queryParamDataProvider() {
        String nameCaseInsensitive = "r4-p17";
        String partOfName = "p1";

        return new Object[][]{{nameCaseInsensitive}, {partOfName}, {NAME}};
    }

}
