package base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    protected static final String BASE_URL = "https://swapi.py4e.com/api/";
    protected static final String FILMS = "films";
    protected static final String PLANETS = "planets";
    protected static final String VEHICLES = "vehicles";

    protected static RequestSpecification requestSpecification;

    @BeforeAll
    public static void beforeAll(){

        requestSpecification = new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .build();
    }
}
