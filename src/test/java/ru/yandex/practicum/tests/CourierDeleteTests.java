package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.Courier;
import ru.yandex.practicum.models.CourierCreatingDTO;
import ru.yandex.practicum.models.CourierLoginDTO;
import ru.yandex.practicum.steps.CourierSteps;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class CourierDeleteTests extends BaseTest {
    CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    private CourierCreatingDTO courierCreatingDTO;
    private CourierLoginDTO courierLoginDTO;

    @Before
    public void setUp() {
        courier = new Courier();
        courier.withLogin(RandomStringUtils.randomAlphabetic(12))
                .withPassword(RandomStringUtils.randomAlphabetic(10))
                .withFirstName(RandomStringUtils.randomAlphabetic(9));
        courierCreatingDTO = new CourierCreatingDTO(courier.getLogin(), courier.getPassword(), courier.getFirstName());
        courierLoginDTO = new CourierLoginDTO(courier.getLogin(), courier.getPassword());
        // Создаю курьера
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        // Авторизуюсь в сервисе
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO);
        loginResponse.statusCode(200);
        courier.withCurierId(loginResponse.extract().body().path("id"));
    }

    @Test
    @DisplayName("Тест: успешный запрос на удаление возвращает код ответа 200 и ok: true")
    @Description("Позитивный тест для проверки ручки /api/v1/courier/:id на удаление курьера")
    public void canDeleteCourierTest() {
        // Удаляю курьера из БД
        courierSteps.deleteCourier(courier.getCourierId())
                .statusCode(200)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Тест: если отправить запрос без id, вернётся ошибка")
    @Description("Негативный тест для проверки ручки /api/v1/courier/:id на удаление курьера без указания Id")
    // Должен прилетать ответ 400 с сообщением "Недостаточно данных для удаления курьера",
    // а прилетает 404 с сообщением Not Found.
    public void canNotDeleteCourierWithoutIdTest() {
        // Удаление курьера из БД без id
        courierSteps.deleteCourierWithoutId()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Тест: если отправить запрос с несуществующим id, вернётся ошибка")
    @Description("Негативный тест для проверки ручки /api/v1/courier/:id на удаление курьера с несуществующим Id")
    public void canNotDeleteCourierInvalidIdTest() {
        // Удаление курьера из БД c несуществующим id
        courierSteps.deleteCourier(RandomUtils.nextInt(100000000, 999999999))
                .statusCode(404)
                .body("message", containsString("Курьера с таким id нет"));
    }
}