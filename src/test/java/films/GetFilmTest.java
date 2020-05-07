package films;

import base.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

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
    @Severity(SeverityLevel.BLOCKER)
    public void getAllFilms(){

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
    @Severity(SeverityLevel.BLOCKER)
    public void getOneFilmByPathParam(){

        Response response = given()
                .pathParam("id", 1)
                .when()
                .get(BASE_URL + FILMS + "/{id}")
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("");
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    public void getOneFilmByQueryParam(){

        Response response = given()
                .queryParam("search", TITLE)
                .when()
                .get(BASE_URL + FILMS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("results[0].");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void getOneFilmByPartOfQueryParam(){

        String partOfTitle = "A New";

        Response response = given()
                .queryParam("search", partOfTitle)
                .when()
                .get(BASE_URL + FILMS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("results[0].");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void getOneFilmByQueryParamCaseInsensitive(){

        String titleCaseInsensitive = "a new";

        Response response = given()
                .queryParam("search", titleCaseInsensitive)
                .when()
                .get(BASE_URL + FILMS)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        compare("results[0].");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    public void getOneFilmByInvalidQueryParam(){

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
    @Severity(SeverityLevel.MINOR)
    public void getOneFilmWithNonExistingId(){

        int nonExistingId = FILMS_COUNT + 1;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get(BASE_URL+FILMS+"/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Disabled("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
    @Test
    @Severity(SeverityLevel.MINOR)
    public void getOneFilmWithInvalidId(){

        BigInteger bigInvalidId = new BigInteger("214748364700000000000");

        given()
                .pathParam("id", bigInvalidId)
                .when()
                .get(BASE_URL+FILMS+"/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private void compare(String path){

        assertThat(json.getString(path + "title")).isEqualTo(TITLE);
        assertThat(json.getInt(path + "episode_id")).isEqualTo(EPISODE_ID);
        assertThat(json.getString(path + "opening_crawl")).isEqualTo(OPENING_CRAWL);
        assertThat(json.getString(path + "director")).isEqualTo(DIRECTOR);
        assertThat(json.getString(path + "producer")).isEqualTo(PRODUCER);
        assertThat(json.getString(path + "release_date")).isEqualTo(RELEASE_DATE);
        assertThat(json.getList(path + "characters").size()).isEqualTo(CHARACTERS_COUNT);
        assertThat(json.getList(path + "planets").size()).isEqualTo(PLANETS_COUNT);
        assertThat(json.getList(path + "starships").size()).isEqualTo(STARSHIPS_COUNT);
        assertThat(json.getList(path + "vehicles").size()).isEqualTo(VEHICLES_COUNT);
        assertThat(json.getList(path + "species").size()).isEqualTo(SPECIES_COUNT);
    }
}
