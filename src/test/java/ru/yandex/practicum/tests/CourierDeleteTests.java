package ru.yandex.practicum.tests;

import io.qameta.allure.junit4.DisplayName;
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
    }

    @Test
    @DisplayName("Тест: успешный запрос на удаление возвращает код ответа 200 и ok: true")
    public void canDeleteCourierTest() {
        // Создаю курьера
        courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        // Авторизуюсь в сервисе
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO);
        loginResponse.statusCode(200);
        courier.withCurierId(loginResponse.extract().body().path("id"));
        // Удаление курьера из БД
        courierSteps.deleteCourier(courier)
                .statusCode(200)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Тест: если отправить запрос без id, вернётся ошибка")
    // Должен прилетать ответ 400 с сообщением "Недостаточно данных для удаления курьера",
    // а прилетает 404 с сообщением Not Found.
    public void canNotDeleteCourierWithoutIdTest() {
        // Удаление курьера из БД без id
        courierSteps.deleteCourierWithoutId()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Тест: • если отправить запрос с несуществующим id, вернётся ошибка")
    public void canNotDeleteCourierInvalidIdTest() {
        // Удаление курьера из БД c несуществующим id
        courier.withCurierId(RandomUtils.nextInt(100000000, 999999999));
        courierSteps.deleteCourier(courier)
                .statusCode(404)
                .body("message", containsString("Курьера с таким id нет"));
    }
}