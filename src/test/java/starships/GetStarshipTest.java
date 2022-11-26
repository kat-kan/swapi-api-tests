package starships;

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

@Feature("Starships")
public class GetStarshipTest extends BaseTest {

    private static final int STARSHIPS_COUNT = 36;
    private final static int PILOTS_COUNT = 0;
    private final static int FILMS_COUNT = 1;

    private static final String NAME = "Death Star";
    private static final String MODEL = "DS-1 Orbital Battle Station";
    private static final String MANUFACTURER = "Imperial Department of Military Research, Sienar Fleet Systems";
    private final static String COST_IN_CREDITS = "1000000000000";
    private final static String LENGTH = "120000";
    private final static String MAX_ATMOSPHERING_SPEED = "n/a";
    private final static String CREW = "342,953";
    private final static String PASSENGERS = "843,342";
    private final static String CARGO_CAPACITY = "1000000000000";
    private final static String CONSUMABLES = "3 years";
    private final static String HYPERDRIVE_RATING = "4.0";
    private final static String MGLT = "10";
    private final static String STARSHIP_CLASS = "Deep Space Mobile Battlestation";

    private JsonPath json;

    @Test(groups = "positive_tests")
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

    @Test(groups = "positive_tests")
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

    @Test(dataProvider = "queryParamData", groups = "positive_tests")
    @Severity(BLOCKER)
    public void getOneStarshipByQueryParam(String search) {

        Response response = given()
                .queryParam("search", search)
                .when()
                .get(BASE_URL + STARSHIPS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compareStarshipObject("results[0].");
    }

    @Test(groups = "negative_tests")
    @Severity(NORMAL)
    public void getOneVehicleByInvalidQueryParam() {

        String invalidName = "I don't exist";

        Response response = given()
                .queryParam("search", invalidName)
                .when()
                .get(BASE_URL + VEHICLES)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        assertThat(json.getList("results").size()).isEqualTo(0);
    }

    @Test(groups = "negative_tests")
    @Severity(MINOR)
    public void getOneStarshipWithNonExistingId() {

        int nonExistingId = STARSHIPS_COUNT + 1;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL + STARSHIPS + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Ignore("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
    @Test(groups = "negative_tests")
    @Severity(MINOR)
    public void getOneStarshipWithInvalidId() {

        BigInteger bigInvalidId = new BigInteger("214748364700000000000");

        given()
                .pathParam("id", bigInvalidId)
                .when()
                .get(BASE_URL + STARSHIPS + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private void compareStarshipObject(String objectPath) {

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(json.getString(objectPath + "name"), NAME);
        softAssert.assertEquals(json.getString(objectPath + "model"), MODEL);
        softAssert.assertEquals(json.getString(objectPath + "manufacturer"), MANUFACTURER);
        softAssert.assertEquals(json.getString(objectPath + "cost_in_credits"), COST_IN_CREDITS);
        softAssert.assertEquals(json.getString(objectPath + "length"), LENGTH);
        softAssert.assertEquals(json.getString(objectPath + "max_atmosphering_speed"), MAX_ATMOSPHERING_SPEED);
        softAssert.assertEquals(json.getString(objectPath + "crew"), CREW);
        softAssert.assertEquals(json.getString(objectPath + "passengers"), PASSENGERS);
        softAssert.assertEquals(json.getString(objectPath + "cargo_capacity"), CARGO_CAPACITY);
        softAssert.assertEquals(json.getString(objectPath + "consumables"), CONSUMABLES);
        softAssert.assertEquals(json.getString(objectPath + "hyperdrive_rating"), HYPERDRIVE_RATING);
        softAssert.assertEquals(json.getString(objectPath + "MGLT"), MGLT);
        softAssert.assertEquals(json.getString(objectPath + "starship_class"), STARSHIP_CLASS);
        softAssert.assertEquals(json.getList(objectPath + "pilots").size(), PILOTS_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "films").size(), FILMS_COUNT);
        softAssert.assertAll();
    }

    @DataProvider(name = "queryParamData")
    public Object[][] queryParamDataProvider() {
        String nameCaseInsensitive = "death";
        String partOfName = "Death";
        String modelCaseInsensitive = "BATTLE";
        String partOfModel = "ttl";

        return new Object[][]{{nameCaseInsensitive}, {partOfName}, {modelCaseInsensitive}, {partOfModel}};
    }
}
