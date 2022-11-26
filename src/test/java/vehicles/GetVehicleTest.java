package vehicles;

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

@Feature("Vehicles")
public class GetVehicleTest extends BaseTest {

    private final static int VEHICLES_COUNT = 39;
    private final static int PILOTS_COUNT = 2;
    private final static int FILMS_COUNT = 1;

    private final static String NAME = "Imperial Speeder Bike";
    private final static String MODEL = "74-Z speeder bike";
    private final static String MANUFACTURER = "Aratech Repulsor Company";
    private final static String COST_IN_CREDITS = "8000";
    private final static String LENGTH = "3";
    private final static String MAX_ATMOSPHERING_SPEED = "360";
    private final static String CREW = "1";
    private final static String PASSENGERS = "1";
    private final static String CARGO_CAPACITY = "4";
    private final static String CONSUMABLES = "1 day";
    private final static String VEHICLE_CLASS = "speeder";

    private JsonPath json;

    @Test
    @Severity(BLOCKER)
    public void getAllVehicles() {

        Response response = given()
                .when()
                .get(BASE_URL + VEHICLES)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getInt("count")).isEqualTo(VEHICLES_COUNT);
    }

    @Test
    @Severity(BLOCKER)
    public void getOneVehicleByPathParam() {

        Response response = given()
                .pathParam("id", 30)
                .when()
                .get(BASE_URL + VEHICLES + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compareVehicleObject("");
    }

    @Test(dataProvider = "queryParamData")
    @Severity(BLOCKER)
    public void getOneVehicleByQueryParam(String search) {

        Response response = given()
                .queryParam("search", search)
                .when()
                .get(BASE_URL + VEHICLES)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compareVehicleObject("results[0].");
    }

    @Test
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

    @Test
    @Severity(MINOR)
    public void getOneVehicleWithNonExistingId() {

        int nonExistingId = VEHICLES_COUNT + 1;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL + VEHICLES + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Ignore("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
    @Test
    @Severity(MINOR)
    public void getOneVehicleWithInvalidId() {

        BigInteger bigInvalidId = new BigInteger("214748364700000000000");

        given()
                .pathParam("id", bigInvalidId)
                .when()
                .get(BASE_URL + VEHICLES + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private void compareVehicleObject(String objectPath) {

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
        softAssert.assertEquals(json.getString(objectPath + "vehicle_class"), VEHICLE_CLASS);
        softAssert.assertEquals(json.getList(objectPath + "pilots").size(), PILOTS_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "films").size(), FILMS_COUNT);
        softAssert.assertAll();
    }

    @DataProvider(name = "queryParamData")
    public Object[][] queryParamDataProvider() {
        String nameCaseInsensitive = "imperial";
        String partOfName = "Imperial";
        String modelCaseInsensitive = "BIKE";
        String partOfModel = "ike";

        return new Object[][]{{nameCaseInsensitive}, {partOfName}, {NAME}, {modelCaseInsensitive}, {partOfModel}, {MODEL}};
    }
}
