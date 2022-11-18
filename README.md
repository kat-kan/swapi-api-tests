# swapi-api-tests

Collection of API tests for Star Wars API. This project was initiated in 2020 with JUnit. In 2022 I decided to switch to TestNG and continue the project.

![](https://img.shields.io/badge/Code-Java%2017-informational?style=flat&color=blueviolet)
![](https://img.shields.io/badge/Framework-TestNG-informational?style=flat&&color=blueviolet)
![](https://img.shields.io/badge/Library-REST%20Assured-informational?style=flat&&color=blueviolet)
![](https://img.shields.io/badge/Library-AssertJ-informational?style=flat&&color=blueviolet)
![](https://img.shields.io/badge/Library-Allure-informational?style=flat&&color=blueviolet)

App links : SWAPI [website](https://swapi.dev/) and [documentation](https://swapi.dev/documentation)

*The Star Wars API* is open-source Web API with data from the Star Wars canon universe.

## Installation

There are few ways to start using this repository:
1. Clone with Git using command line and this command: `git clone https://github.com/kat-kan/swapi-api-tests`
2. Clone with Git using graphical user interface (for example Sourcetree)
3. Download ZIP with the code (using option shown in the screenshot below)
## Features

This project contains tests for following resources:

Planets :purple_circle: Vehicles :purple_circle: Films :purple_circle: Starships

:white_check_mark: you can run tests in terminal with Maven

:white_check_mark: you can generate test execution report with Allure

### Guidelines for running tests with Maven

To run tests, use following command
`mvn clean test`

### Guidelines for generating Allure reports

[Allure](http://allure.qatools.ru/) is an open-source test tool that allows to generate HTML test execution reports.

To run tests and generate HTML report, use following command
`mvn io.qameta.allure:allure-maven:report`

If you want the report to be opened immediately in default browser, use the following command
`mvn io.qameta.allure:allure-maven:serve`

