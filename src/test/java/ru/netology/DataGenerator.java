package ru.netology;

import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;




public final class DataGenerator {

    private DataGenerator() {
    }

    public static LocalDate generateDate(int shift) {
        return LocalDate.now().plusDays(shift);

    }

    public static String generateDateFormatted(int shift, DateTimeFormatter formatter) {
        return generateDate(shift).format(formatter);
    }

    public static String generateCity() {
        var cities = new String[]{"Москва", "Казань", "Новосибирск", "Нижний Новгород", "Хабаровск"};
        return cities[(int) (Math.random() * cities.length)];

    }

    public static String generateName(String locale) {
        var faker = new Faker(new Locale(locale));
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public static String generatePhone(String locale) {
        var faker = new Faker(new Locale(locale));
        return faker.phoneNumber().phoneNumber();
    }

    public static class Registration {
        private Registration() {
        }

        public static UserInfo generateUser(String locale) {
            return new UserInfo(generateCity(), generateName(locale), generatePhone(locale));

        }
    }
}




