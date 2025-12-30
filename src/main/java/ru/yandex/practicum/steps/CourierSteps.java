package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.models.Courier;
import ru.yandex.practicum.models.CourierCreatingDTO;
import ru.yandex.practicum.models.CourierLoginDTO;

import static io.restassured.RestAssured.given;

public class CourierSteps {

    @Step("Создание курьера в сервисе")
    public ValidatableResponse createCourier(CourierCreatingDTO courierCreatingDTO) {
        return given()
                .body(courierCreatingDTO)
                .when()
                .post("/api/v1/courier")
                .then();
    }

    @Step("Авторизация курьера в сервисе")
    public ValidatableResponse loginCourier(CourierLoginDTO courierLoginDTO) {
        return given()
                .body(courierLoginDTO)
                .when()
                .post("/api/v1/courier/login")
                .then();
    }

    @Step("Удаление курьера из сервиса")
    public ValidatableResponse deleteCourier(Courier courier) {
        return given()
                .when()
                .pathParams("id", courier.getCourierId())
                .delete("/api/v1/courier/{id}")
                .then();
    }

    @Step("Удаление курьера из сервиса без id")
    public ValidatableResponse deleteCourierWithoutId() {
        return given()
                .when()
                .delete("/api/v1/courier/")
                .then();
    }
}