package com.base.automation.drivers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class DriverFactory {

    public static AndroidDriver createDriver() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setPlatformName("Android")
                .setApp(Paths.get("src/test/resources/apk/SauceLabs.apk").toAbsolutePath().toString())
                .setAutomationName("UiAutomator2")
                .setAppWaitActivity("com.swaglabsmobileapp.MainActivity")
                .setNoReset(true);

        return new AndroidDriver(
                new URL("http://127.0.0.1:4723/"),
                options
        );
    }
}

