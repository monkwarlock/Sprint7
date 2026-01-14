package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import ru.yandex.practicum.models.*;
import ru.yandex.practicum.steps.OrderSteps;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;

public class OrdersListTests extends BaseTest {
    OrderSteps orderSteps = new OrderSteps();

    @Test
    @DisplayName("Тест: Список заказов: в тело ответа возвращается список заказов.")
    @Description("Позитивный тест для проверки ручки /api/v1/orders на получение списка заказов")
    public void getListOfOrder() {
        // Получение списка заказов
        ValidatableResponse response = orderSteps.gettingListOfOrders();
        response.statusCode(200)
                .body("orders", instanceOf(List.class))
                .body("orders.size()", greaterThan(0))
                .body("orders", everyItem(hasKey("id")))
                .body("orders", everyItem(hasKey("courierId")))
                .body("orders", everyItem(hasKey("firstName")))
                .body("orders", everyItem(hasKey("lastName")))
                .body("orders", everyItem(hasKey("address")))
                .body("orders", everyItem(hasKey("metroStation")))
                .body("orders", everyItem(hasKey("phone")))
                .body("orders", everyItem(hasKey("rentTime")))
                .body("orders", everyItem(hasKey("deliveryDate")))
                .body("orders", everyItem(hasKey("track")))
                .body("orders", everyItem(hasKey("color")))
                .body("orders", everyItem(hasKey("comment")))
                .body("orders", everyItem(hasKey("createdAt")))
                .body("orders", everyItem(hasKey("updatedAt")))
                .body("orders", everyItem(hasKey("status")))
                .body("pageInfo", notNullValue())
                .body("pageInfo", hasKey("page"))
                .body("pageInfo", hasKey("total"))
                .body("pageInfo", hasKey("limit"))
                .body("availableStations", notNullValue())
                .body("availableStations", everyItem(hasKey("name")))
                .body("availableStations", everyItem(hasKey("number")))
                .body("availableStations", everyItem(hasKey("color")));
    }
}