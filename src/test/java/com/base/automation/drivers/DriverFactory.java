package com.base.automation.drivers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

public class DriverFactory {

    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723/";

    public static AndroidDriver createDriver() throws MalformedURLException {
        logger.info("Creando nuevo AndroidDriver...");

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setPlatformName("Android")
                .setApp(Paths.get("src/test/resources/apk/SauceLabs.apk").toAbsolutePath().toString())
                .setAutomationName("UiAutomator2")
                .setAppWaitActivity("*")
                .setNoReset(false)
                .setFullReset(true)
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(Duration.ofSeconds(300))
                .setAppWaitDuration(Duration.ofSeconds(30));

        AndroidDriver driver = new AndroidDriver(new URL(APPIUM_SERVER_URL), options);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        logger.info("AndroidDriver creado exitosamente para dispositivo: {}",
                driver.getCapabilities().getCapability("deviceName"));

        return driver;
    }


    public static void closeDriver(AndroidDriver driver) {
        if (driver != null) {
            try {
                logger.info("Iniciando cierre del driver y aplicación...");

                try {
                    driver.terminateApp("com.swaglabsmobileapp");
                    logger.info("Aplicación terminada exitosamente");
                } catch (Exception e) {
                    logger.warn("No se pudo terminar la app específica: {}", e.getMessage());
                }

                driver.quit();
                logger.info("Driver cerrado exitosamente");

            } catch (Exception e) {
                logger.error("Error al cerrar driver: {}. Intentando quit forzado...", e.getMessage());
                try {
                    driver.quit();
                } catch (Exception quitException) {
                    logger.error("Error en quit forzado: {}", quitException.getMessage());
                }
            }
        }
    }
}