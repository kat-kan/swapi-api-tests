package starships;

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

    @ParameterizedTest(name = "name/model : {0}")
    @MethodSource("createQueryParamData")
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
    public void getOneStarshipWithNonExistingId() {

        int nonExistingId = STARSHIPS_COUNT + 1;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL + STARSHIPS + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Disabled("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
    @Test
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
        assertThat(json.getString(objectPath + "hyperdrive_rating")).isEqualTo(HYPERDRIVE_RATING);
        assertThat(json.getString(objectPath + "MGLT")).isEqualTo(MGLT);
        assertThat(json.getString(objectPath + "starship_class")).isEqualTo(STARSHIP_CLASS);
        assertThat(json.getList(objectPath + "pilots").size()).isEqualTo(PILOTS_COUNT);
        assertThat(json.getList(objectPath + "films").size()).isEqualTo(FILMS_COUNT);
    }

    private static Stream<Arguments> createQueryParamData() {

        String nameCaseInsensitive = "death";
        String partOfName = "Death";
        String modelCaseInsensitive = "BATTLE";
        String partOfModel = "ttl";

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
