package io.github.dantegrek;

import io.github.dantegrek.enums.BrowserName;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.github.dantegrek.enums.Key.*;
import static io.github.dantegrek.jplay.Jplay.*;

public class KeyboardActionsTest {

    private final String keyboardActionsUrl = "https://dantegrek.github.io/testautomation-playground/keyboard_events.html";

    @AfterEach
    public void afterEach() {
        then()
                .closeBrowser()
                .clearConfig();
    }

    public static Object[][] browsers() {
        return new Object[][]{
                {BrowserName.CHROMIUM},
                {BrowserName.WEBKIT},
                {BrowserName.FIREFOX}
        };
    }

    @ParameterizedTest
    @MethodSource("browsers")
    public void keyTest(BrowserName browserName) {
        given()
                .browserConfig()
                .withBrowser(browserName);
        and()
                .startBrowser()
                .navigateTo(keyboardActionsUrl);
        when()
                .click("#area")
                .key(DIGIT_0);
        then()
                .expectThat()
                .selector("#key")
                .hasText("0");
    }

    @ParameterizedTest
    @MethodSource("browsers")
    public void keyDownKeyUpTest(BrowserName browserName) {
        given()
                .browserConfig()
                .withBrowser(browserName);
        and()
                .startBrowser()
                .navigateTo(keyboardActionsUrl);
        when()
                .click("#area")
                .keyDown(SHIFT);
        user()
                .expectThat()
                .selector("#key")
                .hasText("Shift");
        and()
                .key(KEY_A)
                .keyUp(SHIFT)
                .expectThat()
                .selector("#key")
                .hasText("A");
        then()
                .key(KEY_A)
                .expectThat()
                .selector("#key")
                .hasText("a");
    }

    @ParameterizedTest
    @MethodSource("browsers")
    public void insertTextTest(BrowserName browserName) {
        given()
                .browserConfig()
                .withBrowser(browserName);
        and()
                .startBrowser()
                .navigateTo(keyboardActionsUrl);
        when()
                .click("#area")
                .insertText("嗨");
        then()
                .expectThat()
                .selector("#area")
                .hasValue("嗨");
    }

}
