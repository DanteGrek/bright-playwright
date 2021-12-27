package io.github.dantegrek;

import com.microsoft.playwright.options.ViewportSize;
import io.github.dantegrek.enums.BrowserName;
import io.github.dantegrek.interfaces.Device;
import io.github.dantegrek.actions.TestAction;
import io.github.dantegrek.enums.Devices;
import io.github.dantegrek.screenplay.Actor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import static org.junit.jupiter.api.Assertions.*;

public class StartBrowserTest {

    @AfterEach
    public void clearActor() {
        Actor.actor()
                .closeBrowser()
                .cleanConfig();
    }

    public static Object[][] browsers() {
        return new Object[][]{
                {BrowserName.CHROMIUM, "HeadlessChrome"},
                {BrowserName.CHROME, "Chrome"},
                {BrowserName.MSEDGE, "Edg/"},
                {BrowserName.WEBKIT, "Safari"},
                {BrowserName.FIREFOX, "Firefox"}
        };
    }

    public static Object[][] devices() {
        return new Object[][]{
                {BrowserName.CHROMIUM, Devices.IPHONE_12},
                {BrowserName.CHROME, Devices.IPHONE_12},
                {BrowserName.MSEDGE, Devices.IPHONE_12},
                {BrowserName.WEBKIT, Devices.IPHONE_12},
                {BrowserName.FIREFOX, Devices.IPHONE_12}
        };
    }

    @ParameterizedTest
    @MethodSource("browsers")
    public void startBrowserTest(BrowserName browserName, String expectedUserAgent) {
        String userAgent = Actor.actor()
                .config()
                .withBrowser(browserName)
                .configIsFinished()
                .createBrowser()
                .does(TestAction.testAction())
                .getUserAgent();
        assertTrue(userAgent.contains(expectedUserAgent),
                "User agent of " + browserName.name + " does not contain " + expectedUserAgent);
    }

    @ParameterizedTest
    @MethodSource("devices")
    public void startDeviceTest(BrowserName browserName, Device device) {
        String userAgent = Actor.actor()
                .config()
                .withBrowser(browserName)
                .withDevice(device)
                .configIsFinished()
                .createBrowser()
                .does(TestAction.testAction())
                .getUserAgent();
        ViewportSize viewportSize = Actor.actor()
                .does(TestAction.testAction())
                .getViewportSize();
        int deviceScaleFactor = Actor.actor()
                .does(TestAction.testAction())
                .getDeviceScaleFactor();
        assertAll(String.format("Device '%s'", device.getDeviceName()),
                () -> assertEquals(device.getUserAgent(), userAgent, "user agent"),
                () -> assertEquals(device.getViewportWidth(), viewportSize.width, "viewport width is different."),
                () -> assertEquals(device.getViewportHeight(), viewportSize.height, "viewport height is different."),
                () -> assertEquals(device.getDeviceScaleFactor(), deviceScaleFactor, "device scale factor is different."));
    }
}