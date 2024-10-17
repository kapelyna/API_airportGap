package com.example;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import io.restassured.http.ContentType;

public class AirportGapAPITest {

    final String BASE_URL = "https://airportgap.com/api";
    final String bearerToken = "WyDMDAk7KE5rxLWrwh33rJ3Y"; // Ваш Bearer токен

    // Тест для отримання аеропорту за IATA кодом
    @Test
    public void getAirportByIATACode() {
        given()
                .when()
                .get(BASE_URL + "/airports/KBP")
                .then()
                .statusCode(200) // Перевіряємо, що статус код 200 (OK)
                .body("data.attributes.iata", equalTo("KBP")) // Перевіряємо, що IATA код дорівнює "KBP"
                .body("data.attributes.name", notNullValue()); // Перевіряємо, що ім'я аеропорту не є null
    }

    // Тест для створення улюбленого аеропорту
    @Test
    public void createFavoriteAirport() {

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + bearerToken) // Додаємо Bearer токен
                .when()
                .post(BASE_URL + "/favorites?airport_id=YBR") // YBG, YBC, YBB
                .then()
                .statusCode(201) // Очікуємо статус-код 201 (Created)
                .body("data.type", equalTo("favorite")); // Перевірка типу
    }

    @Test
    public void deleteFavoriteAirport() {
        given()
                .header("Authorization", "Bearer " + bearerToken)
                .when()
                .delete(BASE_URL + "/favorites/18116") // URL для видалення
                .then()
                .statusCode(204); // Перевіряємо, що статус код 204 (No Content)
    }

    // Тест для перевірки некоректного IATA коду
    @Test
    public void validateInvalidAirportCode() {
        given()
                .when()
                .get(BASE_URL + "/airports/INVALID")
                .then()
                .statusCode(404) // Перевіряємо, що статус код 404 (Not Found)
                .body("errors[0].detail", equalTo("The page you requested could not be found")); // Перевіряємо, що
                                                                                                 // деталі помилки
                                                                                                 // відповідають
                                                                                                 // очікуваним
    }

    // Тест для перевірки відстані між аеропортами
    @Test
    public void calculateDistanceBetweenAirports() {
        String requestBody = "{\n" +
                "  \"from\": \"KBP\",\n" +
                "  \"to\": \"JFK\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL + "/airports/distance")
                .then()
                .statusCode(200) // Перевіряємо, що статус код 200 (OK)
                .body("data.attributes.kilometers", greaterThan(0F)); // Перевіряємо, що відстань в кілометрах більше
                                                                      // нуля
    }

    // Тест для перевірки неавторизованого доступу
    @Test
    public void validateUnauthorizedAccess() {
        given()
                .when()
                .get(BASE_URL + "/favorites")
                .then()
                .statusCode(401) // Перевіряємо, що статус код 401 (Unauthorized)
                .body("errors[0].detail", equalTo("You are not authorized to perform the requested action.")); // Перевіряємо,
                                                                                                               // що
                                                                                                               // деталі
                                                                                                               // помилки
                                                                                                               // відповідають
                                                                                                               // очікуваним
    }
}
