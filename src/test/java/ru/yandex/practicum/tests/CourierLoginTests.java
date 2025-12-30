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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;

public class CourierLoginTests extends BaseTest {
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
    @DisplayName("Тест: курьер может авторизоваться, успешный запрос возвращает id")
    public void canLoginCourierTest(){
        // Создаю курьера
        courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        // Авторизуюсь в сервисе
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO);
        loginResponse.statusCode(200).body("id", notNullValue());
        courier.withCurierId(loginResponse.extract().body().path("id"));
    }

    @Test
    @DisplayName("Тест: система вернёт ошибку, если неправильно указать логин")
    public void invalidLoginErrorTest(){
        // Создаю курьера
        courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        // Авторизуюсь с неправильным логином
        CourierLoginDTO courierLoginDTO1 = new CourierLoginDTO("Qwerty1234", courier.getPassword());
        courierSteps.loginCourier(courierLoginDTO1)
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
        // Авторизуюсь для получения id и удаления курьера из БД
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO);
        loginResponse.statusCode(200).body("id", notNullValue());
        courier.withCurierId(loginResponse.extract().body().path("id"));
    }

    @Test
    @DisplayName("Тест: система вернёт ошибку, если неправильно указать пароль")
    public void invalidPasswordErrorTest(){
        // Создаю курьера
        courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        // Авторизуюсь с неправильным паролем
        CourierLoginDTO courierLoginDTO1 = new CourierLoginDTO(courier.getLogin(), "Qwerty1234");
        courierSteps.loginCourier(courierLoginDTO1)
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
        // Авторизуюсь для получения id и удаления курьера из БД
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO);
        loginResponse.statusCode(200).body("id", notNullValue());
        courier.withCurierId(loginResponse.extract().body().path("id"));
    }

    @Test
    @DisplayName("Тест: система вернёт ошибку, если не указать логин")
    public void notValueLoginTest(){
        // Создаю курьера
        courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        // Авторизуюсь с неправильным паролем
        CourierLoginDTO courierLoginDTO1 = new CourierLoginDTO("", courier.getPassword());
        courierSteps.loginCourier(courierLoginDTO1)
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
        // Авторизуюсь для получения id и удаления курьера из БД
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO);
        loginResponse.statusCode(200).body("id", notNullValue());
        courier.withCurierId(loginResponse.extract().body().path("id"));
    }

    @Test
    @DisplayName("Тест: система вернёт ошибку, если не указать пароль")
    public void notValuePasswordTest(){
        // Создаю курьера
        courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        // Авторизуюсь с неправильным паролем
        CourierLoginDTO courierLoginDTO1 = new CourierLoginDTO(courier.getLogin(), "");
        courierSteps.loginCourier(courierLoginDTO1)
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
        // Авторизуюсь для получения id и удаления курьера из БД
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO);
        loginResponse.statusCode(200).body("id", notNullValue());
        courier.withCurierId(loginResponse.extract().body().path("id"));
    }

    @Test
    @DisplayName("Тест: система вернёт ошибку, если не указать логин и пароль")
    public void notValueLoginAndPasswordTest(){
        // Создаю курьера
        courierSteps.createCourier(courierCreatingDTO).statusCode(201);
        // Авторизуюсь с неправильным паролем
        CourierLoginDTO courierLoginDTO1 = new CourierLoginDTO("", "");
        courierSteps.loginCourier(courierLoginDTO1)
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
        // Авторизуюсь для получения id и удаления курьера из БД
        ValidatableResponse loginResponse = courierSteps.loginCourier(courierLoginDTO);
        loginResponse.statusCode(200).body("id", notNullValue());
        courier.withCurierId(loginResponse.extract().body().path("id"));
    }

    @Test
    @DisplayName("Тест: авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void invalidLoginAndPasswordErrorTest(){
        // Авторизуюсь под несуществующими логином и паролем
        courierSteps.loginCourier(courierLoginDTO)
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @After
    // Удаляю курьера
    public void tearDown() {
        if (courier.getCourierId() != null) {
            courierSteps.deleteCourier(courier).statusCode(200);
        }
    }
}