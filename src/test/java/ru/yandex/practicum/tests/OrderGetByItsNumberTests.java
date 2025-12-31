package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.util.RestConfig;
import ru.yandex.practicum.models.Order;
import ru.yandex.practicum.models.OrderCancellationDTO;
import ru.yandex.practicum.models.OrderCreatingDTO;
import ru.yandex.practicum.steps.OrderSteps;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;

public class OrderGetByItsNumberTests extends BaseTest {
    OrderSteps orderSteps = new OrderSteps();
    private Order order;
    public RestConfig data;
    private OrderCreatingDTO orderCreatingDTO;
    private Integer statusCode;

    @Before
    public void setUp() {
        data = RestConfig.getData();
        order = new Order();
        order.withFirstName(data.getFirstName()).withLastName(data.getLastName()).withAddress(data.getAddress())
                .withMetroStation(data.getMetroStation()).withPhone(data.getPhone()).withRentTime(data.getRentTime())
                .withDeliveryDate(data.getDeliveryDate()).withComment(data.getComment())
                .withColor(data.getColor());
        orderCreatingDTO = new OrderCreatingDTO(order.getFirstName(), order.getLastName(), order.getAddress(),
                order.getMetroStation(), order.getPhone(), order.getRentTime(), order.getDeliveryDate(), order.getComment(),
                order.getColor());
        // Создание заказа
        ValidatableResponse responseTrack = orderSteps.createOrder(orderCreatingDTO).statusCode(201);
        order.withTrack(responseTrack.extract().body().path("track"));
    }

    @Test
    @DisplayName("Тест: Получить заказ по его номеру: успешный запрос возвращает объект с заказом")
    @Description("Позитивный тест для проверки ручки /api/v1/orders/track на получение заказа по его номеру")
    public void getOrderByItsNumberTest() {
        // Получение заказа по его номеру
        ValidatableResponse responseOrder = orderSteps.gettingOrdersByItsNumber(order.getTrack());
        responseOrder.statusCode(200)
                .body("order", notNullValue());
        statusCode = responseOrder.extract().statusCode();
    }

    @Test
    @DisplayName("Тест: запрос без номера заказа возвращает ошибку")
    @Description("Негативный тест для проверки ручки /api/v1/orders/track на получение заказа без номера заказа")
    public void getOrderWithoutItsNumberTest() {
        // Получение заказа по его номеру
        ValidatableResponse responseOrder = orderSteps.gettingOrdersWithoutItsNumber();
        responseOrder.statusCode(400).body("message", containsString("Недостаточно данных для поиска"));
        statusCode = responseOrder.extract().statusCode();
    }

    @Test
    @DisplayName("Тест: запрос с несуществующим заказом возвращает ошибку")
    @Description("Негативный тест для проверки ручки /api/v1/orders/track на получение несуществующего заказа")
    public void getOrderByInvalidNumberTest() {
        // Получение заказа по несуществующему в БД номеру
        ValidatableResponse responseOrder = orderSteps.gettingOrdersByItsNumber(2147483647);
        responseOrder.statusCode(404).body("message", containsString("Заказ не найден"));
        statusCode = responseOrder.extract().statusCode();
    }

    @After
    public void tearDown() {
        if (statusCode.equals(200)) {
            // Отмена заказа (Баг: при отмене приходит 200 Ок вместо 400 Недостаточно данных для поиска)
            OrderCancellationDTO orderCancellationDTO = new OrderCancellationDTO(order.getTrack());
            orderSteps.cancellationOrder(orderCancellationDTO).statusCode(200);
        }
    }
}