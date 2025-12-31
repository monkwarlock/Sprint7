package ru.yandex.practicum.util;

import java.util.Arrays;
import java.util.List;

public class RestConfig {
    public static final String HOST = "https://qa-scooter.praktikum-services.ru/";

    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final int rentTime;
    private final String deliveryDate;
    private final String comment;
    private final List<String> color;

    public RestConfig(String firstName, String lastName, String address, String metroStation, String phone,
                      int rentTime, String deliveryDate, String comment, List<String> color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    private static final List<RestConfig> testData = Arrays.asList(
            new RestConfig("Ян", "Ли", "ул. Ленина, д. 15, кв. 3",
                    "1", "+79991234567", 4, "10.01.2026",
                    "Оставить у двери", List.of("BLACK")),
            new RestConfig("Абдурахмангаджи", "Семипополовигероверсалофедираковский", "ул Ленина, д 15, кв 3",
                    "2", "84951234567", 3, "10-01-2026",
                    "", List.of("GREY")),
            new RestConfig("Ян", "Ли", "ул. Ленина, д. 15, кв. 3",
                    "3", "+79991234567", 4, "2026.01.10",
                    "Оставить у двери", List.of("BLACK", "GREY")),
            new RestConfig("Абдурахмангаджи", "Семипополовигероверсалофедираковский", "ул Ленина, д 15, кв 3",
                    "4", "84951234567", 3, "2026-10-01",
                    "", List.of())
    );

    public static List<RestConfig> getTestData() {
        return testData;
    }

    public static RestConfig getData() {
        return new RestConfig("Ян", "Ли", "ул. Ленина, д. 15, кв. 3",
                "3", "+79991234567", 4, "2026.01.10",
                "Оставить у двери", List.of("BLACK", "GREY"));
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getMetroStation() {
        return metroStation;
    }

    public String getPhone() {
        return phone;
    }

    public int getRentTime() {
        return rentTime;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getComment() {
        return comment;
    }

    public List<String> getColor() {
        return color;
    }
}