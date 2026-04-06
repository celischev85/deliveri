package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class DeliveryTest {

    // Настройка Selenide/Chrome для CI — добавлены важные опции
    static {
        Configuration.browser = "chrome";
        Configuration.headless = true;
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--headless=new",          // при проблемах попробуйте "--headless"
                "--window-size=1366,768",
                "--remote-allow-origins=*"
        );
        Configuration.browserCapabilities = options;
    }

    @BeforeEach
    void setup() {
        waitForSut("http://localhost:9999", 30);
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");

        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDateFormatted(daysToAddForFirstMeeting,
                DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDateFormatted(daysToAddForSecondMeeting,
                DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        $("[data-test-id=city] input").setValue(validUser.getCity());

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);

        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();

        $("[data-test-id=success-notification]")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на " + firstMeetingDate));

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(secondMeetingDate);
        $(byText("Запланировать")).click();

        $("[data-test-id=replan-notification]")
                .shouldBe(visible)
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));

        $("[data-test-id=replan-notification] button")
                .shouldBe(visible)
                .click();

        $("[data-test-id=success-notification]")
                .shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + secondMeetingDate));
    }

    // Вспомогательный метод: ждём, пока SUT начнёт отвечать (timeoutSeconds сек)
    private static void waitForSut(String url, int timeoutSeconds) {
        long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < end) {
            try {
                var conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                conn.setRequestMethod("GET");
                int code = conn.getResponseCode();
                if (code >= 200 && code < 500) return;
            } catch (Exception ignored) {
            }
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        throw new IllegalStateException("SUT is not available: " + url);
    }
}