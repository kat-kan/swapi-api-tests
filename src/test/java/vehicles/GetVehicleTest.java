package vehicles;

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
    @Severity(SeverityLevel.BLOCKER)
    public void getAllVehicles(){

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
    @Severity(SeverityLevel.BLOCKER)
    public void getOneVehicleByPathParam(){

        Response response = given()
                .pathParam("id", 30)
                .when()
                .get(BASE_URL + VEHICLES + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("");
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    public void getOneVehicleByQueryParam(){

        Response response = given()
                .queryParam("search", NAME)
                .when()
                .get(BASE_URL + VEHICLES)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("results[0].");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void getOneVehicleByPartOfQueryParam(){

        String partOfName = "Imperial";

        Response response = given()
                .queryParam("search", partOfName)
                .when()
                .get(BASE_URL + VEHICLES)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("results[0].");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void getOneVehicleByQueryParamCaseInsensitive(){

        String nameCaseInsensitive = "imperial";

        Response response = given()
                .queryParam("search", nameCaseInsensitive)
                .when()
                .get(BASE_URL + VEHICLES)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("results[0].");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void getOneVehicleByInvalidQueryParam(){

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
    @Severity(SeverityLevel.MINOR)
    public void getOnePlanetWithNonExistingId(){

        int nonExistingId = VEHICLES_COUNT + 1;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL + VEHICLES+ "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Disabled("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
    @Test
    @Severity(SeverityLevel.MINOR)
    public void getOneVehicleWithInvalidId(){

        BigInteger bigInvalidId = new BigInteger("214748364700000000000");

        given()
                .pathParam("id", bigInvalidId)
                .when()
                .get(BASE_URL + VEHICLES + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private void compare(String path){

        assertThat(json.getString(path + "name")).isEqualTo(NAME);
        assertThat(json.getString(path + "model")).isEqualTo(MODEL);
        assertThat(json.getString(path + "manufacturer")).isEqualTo(MANUFACTURER);
        assertThat(json.getString(path + "cost_in_credits")).isEqualTo(COST_IN_CREDITS);
        assertThat(json.getString(path + "length")).isEqualTo(LENGTH);
        assertThat(json.getString(path + "max_atmosphering_speed")).isEqualTo(MAX_ATMOSPHERING_SPEED);
        assertThat(json.getString(path + "crew")).isEqualTo(CREW);
        assertThat(json.getString(path + "passengers")).isEqualTo(PASSENGERS);
        assertThat(json.getString(path + "cargo_capacity")).isEqualTo(CARGO_CAPACITY);
        assertThat(json.getString(path + "consumables")).isEqualTo(CONSUMABLES);
        assertThat(json.getString(path + "vehicle_class")).isEqualTo(VEHICLE_CLASS);
        assertThat(json.getList(path + "pilots").size()).isEqualTo(PILOTS_COUNT);
        assertThat(json.getList(path + "films").size()).isEqualTo(FILMS_COUNT);
    }
}
