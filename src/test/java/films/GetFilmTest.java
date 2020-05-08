package films;

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
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

public class GetFilmTest extends BaseTest {

    private final static int FILMS_COUNT = 7;
    private final static int EPISODE_ID = 4;
    private final static int CHARACTERS_COUNT = 18;
    private final static int PLANETS_COUNT = 3;
    private final static int STARSHIPS_COUNT = 8;
    private final static int VEHICLES_COUNT = 4;
    private final static int SPECIES_COUNT = 5;

    private final static String TITLE = "A New Hope";
    private final static String OPENING_CRAWL = "It is a period of civil war.\r\nRebel spaceships, striking\r\nfrom a hidden base, have won\r\ntheir first victory against\r\nthe evil Galactic Empire.\r\n\r\nDuring the battle, Rebel\r\nspies managed to steal secret\r\nplans to the Empire's\r\nultimate weapon, the DEATH\r\nSTAR, an armored space\r\nstation with enough power\r\nto destroy an entire planet.\r\n\r\nPursued by the Empire's\r\nsinister agents, Princess\r\nLeia races home aboard her\r\nstarship, custodian of the\r\nstolen plans that can save her\r\npeople and restore\r\nfreedom to the galaxy....";
    private final static String DIRECTOR = "George Lucas";
    private final static String PRODUCER = "Gary Kurtz, Rick McCallum";
    private final static String RELEASE_DATE = "1977-05-25";

    private JsonPath json;

    @Test
    @Severity(BLOCKER)
    public void getAllFilms() {

        Response response = given()
                .when()
                .get(BASE_URL + FILMS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        assertThat(json.getInt("count")).isEqualTo(FILMS_COUNT);
    }

    @Test
    @Severity(BLOCKER)
    public void getOneFilmByPathParam() {

        Response response = given()
                .pathParam("id", 1)
                .when()
                .get(BASE_URL + FILMS + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compareFilmObject("");
    }

    @ParameterizedTest(name = "title : {0}")
    @MethodSource("createQueryParamData")
    @Severity(BLOCKER)
    public void getOneFilmByQueryParam(String title) {

        Response response = given()
                .queryParam("search", title)
                .when()
                .get(BASE_URL + FILMS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compareFilmObject("results[0].");
    }


    @Test
    @Severity(NORMAL)
    public void getOneFilmByInvalidQueryParam() {

        String invalidTitle = "I don't exist";

        Response response = given()
                .queryParam("search", invalidTitle)
                .when()
                .get(BASE_URL + FILMS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        assertThat(json.getList("results").size()).isEqualTo(0);
    }

    @Test
    @Severity(MINOR)
    public void getOneFilmWithNonExistingId() {

        int nonExistingId = FILMS_COUNT + 1;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL + FILMS + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Disabled("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
    @Test
    @Severity(MINOR)
    public void getOneFilmWithInvalidId() {

        BigInteger bigInvalidId = new BigInteger("214748364700000000000");

        given()
                .pathParam("id", bigInvalidId)
                .when()
                .get(BASE_URL + FILMS + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private void compareFilmObject(String objectPath) {

        assertThat(json.getString(objectPath + "title")).isEqualTo(TITLE);
        assertThat(json.getInt(objectPath + "episode_id")).isEqualTo(EPISODE_ID);
        assertThat(json.getString(objectPath + "opening_crawl")).isEqualTo(OPENING_CRAWL);
        assertThat(json.getString(objectPath + "director")).isEqualTo(DIRECTOR);
        assertThat(json.getString(objectPath + "producer")).isEqualTo(PRODUCER);
        assertThat(json.getString(objectPath + "release_date")).isEqualTo(RELEASE_DATE);
        assertThat(json.getList(objectPath + "characters").size()).isEqualTo(CHARACTERS_COUNT);
        assertThat(json.getList(objectPath + "planets").size()).isEqualTo(PLANETS_COUNT);
        assertThat(json.getList(objectPath + "starships").size()).isEqualTo(STARSHIPS_COUNT);
        assertThat(json.getList(objectPath + "vehicles").size()).isEqualTo(VEHICLES_COUNT);
        assertThat(json.getList(objectPath + "species").size()).isEqualTo(SPECIES_COUNT);
    }

    private static Stream<Arguments> createQueryParamData() {

        String titleCaseInsensitive = "a new";
        String partOfTitle = "A New";

        return Stream.of(
                Arguments.of(titleCaseInsensitive),
                Arguments.of(partOfTitle),
                Arguments.of(TITLE)
        );
    }
}
