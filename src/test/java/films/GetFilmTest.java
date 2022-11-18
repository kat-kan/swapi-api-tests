package films;

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

@Feature("Films")
public class GetFilmTest extends BaseTest {

    private final static int FILMS_COUNT = 6;
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

    @Test(dataProvider = "queryParamData")
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

    @Ignore("Temporarily disabled because of 500 response code. Test verifies if one can send really big number as path param. The expected response code would be 404")
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

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(json.getString(objectPath + "title"),TITLE);
        softAssert.assertEquals(json.getInt(objectPath + "episode_id"),EPISODE_ID);
        softAssert.assertEquals(json.getString(objectPath + "opening_crawl"),OPENING_CRAWL);
        softAssert.assertEquals(json.getString(objectPath + "director"),DIRECTOR);
        softAssert.assertEquals(json.getString(objectPath + "producer"),PRODUCER);
        softAssert.assertEquals(json.getString(objectPath + "release_date"),RELEASE_DATE);
        softAssert.assertEquals(json.getList(objectPath + "characters").size(),CHARACTERS_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "planets").size(),PLANETS_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "starships").size(),STARSHIPS_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "vehicles").size(),VEHICLES_COUNT);
        softAssert.assertEquals(json.getList(objectPath + "species").size(),SPECIES_COUNT);
        softAssert.assertAll();
    }

    @DataProvider(name = "queryParamData")
    public Object[][] queryParamDataProvider() {
        String titleCaseInsensitive = "a new";
        String partOfTitle = "A New";

        return new Object[][]{{titleCaseInsensitive}, {partOfTitle}, {TITLE}};
    }
}
