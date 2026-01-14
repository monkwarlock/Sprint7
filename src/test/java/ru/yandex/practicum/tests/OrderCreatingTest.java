package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.practicum.util.RestConfig;
import ru.yandex.practicum.models.OrderCreatingDTO;
import ru.yandex.practicum.models.Order;
import ru.yandex.practicum.models.OrderCancellationDTO;
import ru.yandex.practicum.steps.OrderSteps;

import java.util.Collection;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreatingTest extends BaseTest {
    OrderSteps orderSteps = new OrderSteps();
    private Order order;
    public RestConfig data;
    private OrderCreatingDTO orderCreatingDTO;
    private String statusCode;

    public OrderCreatingTest(RestConfig data) {
        this.data = data;
    }

    @Parameterized.Parameters
    public static Collection<RestConfig> provideData() {
        return RestConfig.getTestData();
    }

    @Before
    public void setUp() {
        order = new Order();
        order.withFirstName(data.getFirstName()).withLastName(data.getLastName()).withAddress(data.getAddress())
                .withMetroStation(data.getMetroStation()).withPhone(data.getPhone()).withRentTime(data.getRentTime())
                .withDeliveryDate(data.getDeliveryDate()).withComment(data.getComment())
                .withColor(data.getColor());
        orderCreatingDTO = new OrderCreatingDTO(order.getFirstName(), order.getLastName(), order.getAddress(),
                order.getMetroStation(), order.getPhone(), order.getRentTime(), order.getDeliveryDate(), order.getComment(),
                order.getColor());
    }

    @Test
    @DisplayName("Тест: Проверь, что когда создаёшь заказ: 1. Можно указать один из цветов — BLACK или GREY; " +
            "2. Можно указать оба цвета; 3. Можно совсем не указывать цвет; 4. Тело ответа содержит track")
    @Description("Позитивный тест для проверки ручки /api/v1/orders на создание заказа с различными вариантами допустимых цветов скутера")
    public void creatingOrderWithDifferentScooterColorsTest() {
        ValidatableResponse response = orderSteps.createOrder(orderCreatingDTO);
        response.statusCode(201).body("track", notNullValue());
        order.withTrack(response.extract().body().path("track"));
        statusCode = Integer.toString(response.extract().statusCode());
    }


    @After
    // Заказ не отменяется, должно приходить 200 ок, а приходит 400 с сообщением недостаточно данных для поиска
    public void tearDown() {
        // Отмена созданного заказа
        if (statusCode.equals("201") || statusCode.equals("200")) {
            OrderCancellationDTO orderCancellationDTO = new OrderCancellationDTO(order.getTrack());
            orderSteps.cancellationOrder(orderCancellationDTO).statusCode(200);
        }
    }
}