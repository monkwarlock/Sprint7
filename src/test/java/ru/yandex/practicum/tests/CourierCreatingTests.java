package ru.yandex.practicum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.Courier;
import ru.yandex.practicum.models.CourierCreatingDTO;
import ru.yandex.practicum.models.CourierLoginDTO;
import ru.yandex.practicum.steps.CourierSteps;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;

public class CourierCreatingTests extends BaseTest {
    CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    private CourierCreatingDTO courierCreatingDTO;
    private CourierLoginDTO courierLoginDTO;
    private Integer statusCode;

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
    @DisplayName("Тест: курьера можно создать, запрос возвращает правильный код ответа," +
            "возвращает ok: true")
    public void canCreateCourierTest() {
        // Создание курьера
        ValidatableResponse response = courierSteps.createCourier(courierCreatingDTO);
        response.statusCode(201).body("ok", is(true));
        statusCode = response.extract().statusCode();
    }

    @Test
    @DisplayName("Тест: нельзя создать двух одинаковых курьеров, если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void creatingTwoIdenticalCourierLoginTests() {
        // Создание курьера 1
        ValidatableResponse response = courierSteps.createCourier(courierCreatingDTO);
        response.statusCode(201);
        statusCode = response.extract().statusCode();
        Courier courier1 = new Courier();
        // Создание курьера 2 с логином курьера 1
        courier1.withLogin(courier.getLogin())
                .withPassword(RandomStringUtils.randomAlphabetic(10))
                .withFirstName(RandomStringUtils.randomAlphabetic(9));
        CourierCreatingDTO courierCreatingDTO1 = new CourierCreatingDTO(courier1.getLogin(), courier1.getPassword(), courier1.getFirstName());
        courierSteps.createCourier(courierCreatingDTO1)
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Тест: курьера нельзя создать, если не передать логин")
    public void canNotCreateCourierWithoutLoginTest() {
        // Создание курьера
        CourierCreatingDTO courierDTO = new CourierCreatingDTO("", courier.getPassword(), courier.getFirstName());
        ValidatableResponse response = courierSteps.createCourier(courierDTO);
        response.statusCode(400).body("message", containsString("Недостаточно данных для создания учетной записи"));
        statusCode = response.extract().statusCode();
    }

    @Test
    @DisplayName("Тест: курьера нельзя создать, если не передать пароль")
    public void canNotCreateCourierWithoutPasswordTest() {
        // Создание курьера
        CourierCreatingDTO courierDTO = new CourierCreatingDTO(courier.getLogin(), "", courier.getFirstName());
        ValidatableResponse response = courierSteps.createCourier(courierDTO);
        response.statusCode(400).body("message", containsString("Недостаточно данных для создания учетной записи"));
        statusCode = response.extract().statusCode();
    }

    @Test
    @DisplayName("Тест: курьера нельзя создать, если не передать Имя")
    // Курьер создаётся без имени
    public void canNotCreateCourierWithoutFirstNameTest() {
        CourierCreatingDTO courierDTO = new CourierCreatingDTO(courier.getLogin(), courier.getPassword(), "");
        ValidatableResponse response = courierSteps.createCourier(courierDTO);
        response.statusCode(400).body("message", containsString("Недостаточно данных для создания учетной записи"));
        statusCode = response.extract().statusCode();
    }

    @After
    public void tearDown() {
        // Авторизация курьера
        if (statusCode.equals(200)) {
            ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO).statusCode(200);
            courier.withCurierId(loginResponse.extract().body().path("id"));
        }
        // Удаление курьера
        if (courier.getCourierId() != null) {
            courierSteps.deleteCourier(courier).statusCode(200);
        }
    }
}