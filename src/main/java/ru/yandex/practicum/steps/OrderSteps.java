package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.models.*;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создание заказа в сервисе")
    public ValidatableResponse createOrder(OrderCreatingDTO orderCreatingDTO) {
        return given()
                .body(orderCreatingDTO)
                .when()
                .post("/api/v1/orders")
                .then();
    }

    @Step("Отмена заказа")
    public ValidatableResponse cancellationOrder(OrderCancellationDTO orderCancellationDTO) {
        return given()
                .body(orderCancellationDTO)
                .when()
                .put("/api/v1/orders/cancel")
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse gettingListOfOrders() {
        return given()
                .when()
                .get("/api/v1/orders")
                .then();
    }

    @Step("Получение заказа по номеру")
    public ValidatableResponse gettingOrdersByItsNumber(Order order) {
        return given()
                .when()
                .queryParam("t", order.getTrack())
                .get("/api/v1/orders/track")
                .then();
    }

    @Step("Принятие заказа")
    public ValidatableResponse acceptOrder(Courier courier, Order order) {
        return given()
                .when()
                .pathParams("id", order.getId())
                .queryParam("courierId", courier.getCourierId())
                .put("/api/v1/orders/accept/{id}")
                .then();
    }

    @Step("Принятие заказа без Id курьера")
    public ValidatableResponse acceptWithoutCourierIdOrder(Order order) {
        return given()
                .when()
                .pathParams("id", order.getId())
                .put("/api/v1/orders/accept/{id}")
                .then();
    }

    @Step("Принятие заказа без Id заказа")
    public ValidatableResponse acceptWithoutIdOrder(Courier courier) {
        return given()
                .when()
                .queryParam("courierId", courier.getCourierId())
                .put("/api/v1/orders/accept/")
                .then();
    }

    @Step("Завершение заказа")
    public ValidatableResponse completeOrder(OrderCompletionDTO orderCompletionDTO) {
        return given()
                .when()
                .pathParams("id", orderCompletionDTO.getId())
                .put("/api/v1/orders/finish/{id}")
                .then();
    }

    public ValidatableResponse gettingOrdersWithoutItsNumber() {
        return given()
                .when()
                .get("/api/v1/orders/track")
                .then();
    }
}