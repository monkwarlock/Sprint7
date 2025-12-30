package ru.yandex.practicum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.RestConfig;
import ru.yandex.practicum.models.*;
import ru.yandex.practicum.steps.CourierSteps;
import ru.yandex.practicum.steps.OrderSteps;


import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class OrderAcceptTests extends BaseTest {
    OrderSteps orderSteps = new OrderSteps();
    private Order order;
    public RestConfig data;
    private OrderCreatingDTO orderCreatingDTO;
    CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    private CourierCreatingDTO courierCreatingDTO;
    private CourierLoginDTO courierLoginDTO;
    private Integer statusCode;
    private Integer statusCodeCreateCourier;

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
        courier = new Courier();
        courier.withLogin(RandomStringUtils.randomAlphabetic(12))
                .withPassword(RandomStringUtils.randomAlphabetic(10))
                .withFirstName(RandomStringUtils.randomAlphabetic(9));
        courierCreatingDTO = new CourierCreatingDTO(courier.getLogin(), courier.getPassword(), courier.getFirstName());
        courierLoginDTO = new CourierLoginDTO(courier.getLogin(), courier.getPassword());
    }

    @Test
    @DisplayName("Тест: Принять заказ: успешный запрос возвращает ok: true")
    public void acceptTheOrderTest() {
        // Создание заказа
        ValidatableResponse responseTrack = orderSteps.createOrder(orderCreatingDTO).statusCode(201);
        order.withTrack(responseTrack.extract().body().path("track"));
        // Создание курьера
        ValidatableResponse responseCourier = courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        statusCodeCreateCourier = responseCourier.extract().statusCode();
        // Авторизация курьера
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO).statusCode(200);
        courier.withCurierId(loginResponse.extract().body().path("id"));
        // Получение заказа по его номеру
        ValidatableResponse responseOrder = orderSteps.gettingOrdersByItsNumber(order).statusCode(200);
        order.withId(responseOrder.extract().body().path("order.id"));
        // Принятие заказа
        ValidatableResponse acceptOrderResponse = orderSteps.acceptOrder(courier, order);
        acceptOrderResponse.statusCode(200).body("ok", is(true));
        statusCode = acceptOrderResponse.extract().statusCode();
    }

    @Test
    @DisplayName("Тест: если не передать id курьера, запрос вернёт ошибку")
    public void acceptTheOrderWithoutCourierIdTest() {
        // Создание заказа
        ValidatableResponse responseTrack = orderSteps.createOrder(orderCreatingDTO).statusCode(201);
        order.withTrack(responseTrack.extract().body().path("track"));
        // Получение заказа по его номеру
        ValidatableResponse responseOrder = orderSteps.gettingOrdersByItsNumber(order).statusCode(200);
        order.withId(responseOrder.extract().body().path("order.id"));
        // Принятие заказа без courierId
        ValidatableResponse acceptOrderResponse = orderSteps.acceptWithoutCourierIdOrder(order);
        acceptOrderResponse.statusCode(400).body("message", containsString("Недостаточно данных для поиска"));
        statusCode = acceptOrderResponse.extract().statusCode();
    }

    @Test
    @DisplayName("Тест: если передать неверный id курьера, запрос вернёт ошибку")
    public void acceptTheOrderWithInvalidCourierIdTest() {
        // Создание заказа
        ValidatableResponse responseTrack = orderSteps.createOrder(orderCreatingDTO).statusCode(201);
        order.withTrack(responseTrack.extract().body().path("track"));
        // Получение заказа по его номеру
        courier.withCurierId(2147483647);
        ValidatableResponse responseOrder = orderSteps.gettingOrdersByItsNumber(order).statusCode(200);
        order.withId(responseOrder.extract().body().path("order.id"));
        // Принятие заказа
        ValidatableResponse acceptOrderResponse = orderSteps.acceptOrder(courier, order);
        acceptOrderResponse.statusCode(404).body("message", containsString("Курьера с таким id не существует"));
        statusCode = acceptOrderResponse.extract().statusCode();
        System.out.println(statusCode);
    }

    @Test
    @DisplayName("Тест: если не передать номер заказа, запрос вернёт ошибку")
    public void acceptTheOrderWithoutIdTest() {
        // Создание курьера
        ValidatableResponse responseCourier = courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        statusCodeCreateCourier = responseCourier.extract().statusCode();
        // Авторизация курьера
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO).statusCode(200);
        courier.withCurierId(loginResponse.extract().body().path("id"));
        // Принятие заказа без Id заказа
        ValidatableResponse acceptOrderResponse = orderSteps.acceptWithoutIdOrder(courier);
        acceptOrderResponse.statusCode(400).body("message", containsString("Недостаточно данных для поиска"));
        statusCode = acceptOrderResponse.extract().statusCode();
        // Баг: Вместо 400 Недостаточно данных для поиска, приходит 404 Not found
    }

    @Test
    @DisplayName("Тест: если передать неверный номер заказа, запрос вернёт ошибку")
    public void acceptTheOrderWithInvalidIdTest() {
        // Создание курьера
        ValidatableResponse responseCourier = courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        statusCodeCreateCourier = responseCourier.extract().statusCode();
        // Авторизация курьера
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO).statusCode(200);
        courier.withCurierId(loginResponse.extract().body().path("id"));
        // Принятие заказа
        order.withId(2147483647);
        ValidatableResponse acceptOrderResponse = orderSteps.acceptOrder(courier, order);
        acceptOrderResponse.statusCode(404).body("message", containsString("Заказа с таким id не существует"));
        statusCode = acceptOrderResponse.extract().statusCode();
    }

    @After
    public void tearDown() {
        try {
            if (statusCode.equals(200)) {
                // Завершение заказа, если курье сможет принять заказ
                OrderCompletionDTO orderCompletionDTO = new OrderCompletionDTO(order.getId());
                orderSteps.completeOrder(orderCompletionDTO).statusCode(200);
            } else {
                // Отмена заказа, если курьер не сможет принять заказ (Баг: при отмене приходит вместо 200 Ок 400 Недостаточно данных для поиска)
                OrderCancellationDTO orderCancellationDTO = new OrderCancellationDTO(order.getTrack());
                orderSteps.cancellationOrder(orderCancellationDTO).statusCode(200);
            }
        }
        finally {
            // Удаление курьера
            if (courier.getCourierId() != null && statusCodeCreateCourier.equals(201)) {
                courierSteps.deleteCourier(courier).statusCode(200);
            }
        }
    }
}