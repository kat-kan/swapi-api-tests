# swapi_api_tests

Tests for Star Wars API ([SWAPI documentation](https://swapi.dev/documentation)):
- planets
- vehicles
- films. 

## Guidelines for running tests with Maven

To run tests, use following command
`mvn clean test`

## Guidelines for generating Allure reports
 
Allure is an open-source framework that allows to generate HTML test execution reports.

([Allure webpage](http://allure.qatools.ru/))

To run tests and generate HTML report, use following command
`mvn clean test allure:report`

If you want the report to be opened immediately in default browser, use the following command
`mvn clean test allure:serve`

