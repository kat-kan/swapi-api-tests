package vehicles;

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

    @ParameterizedTest(name = "name/model : {0}")
    @MethodSource("createQueryParamData")
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
    public void getOnePlanetWithNonExistingId() {

        int nonExistingId = VEHICLES_COUNT + 1;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL + VEHICLES + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Disabled("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
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

        assertThat(json.getString(objectPath + "name")).isEqualTo(NAME);
        assertThat(json.getString(objectPath + "model")).isEqualTo(MODEL);
        assertThat(json.getString(objectPath + "manufacturer")).isEqualTo(MANUFACTURER);
        assertThat(json.getString(objectPath + "cost_in_credits")).isEqualTo(COST_IN_CREDITS);
        assertThat(json.getString(objectPath + "length")).isEqualTo(LENGTH);
        assertThat(json.getString(objectPath + "max_atmosphering_speed")).isEqualTo(MAX_ATMOSPHERING_SPEED);
        assertThat(json.getString(objectPath + "crew")).isEqualTo(CREW);
        assertThat(json.getString(objectPath + "passengers")).isEqualTo(PASSENGERS);
        assertThat(json.getString(objectPath + "cargo_capacity")).isEqualTo(CARGO_CAPACITY);
        assertThat(json.getString(objectPath + "consumables")).isEqualTo(CONSUMABLES);
        assertThat(json.getString(objectPath + "vehicle_class")).isEqualTo(VEHICLE_CLASS);
        assertThat(json.getList(objectPath + "pilots").size()).isEqualTo(PILOTS_COUNT);
        assertThat(json.getList(objectPath + "films").size()).isEqualTo(FILMS_COUNT);
    }

    private static Stream<Arguments> createQueryParamData() {

        String nameCaseInsensitive = "imperial";
        String partOfName = "Imperial";
        String modelCaseInsensitive = "BIKE";
        String partOfModel = "ike";

        return Stream.of(
                Arguments.of(NAME),
                Arguments.of(nameCaseInsensitive),
                Arguments.of(partOfName),
                Arguments.of(MODEL),
                Arguments.of(modelCaseInsensitive),
                Arguments.of(partOfModel)
        );
    }
}
