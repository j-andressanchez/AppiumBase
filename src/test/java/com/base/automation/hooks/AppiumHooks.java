package com.base.automation.hooks;

import com.base.automation.drivers.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppiumHooks {

    private static final Logger logger = LoggerFactory.getLogger(AppiumHooks.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    @Before
    public void setUp(Scenario scenario) {
        logger.info("Iniciando escenario: {}", scenario.getName());
        OnStage.setTheStage(new OnlineCast());
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        WebDriver driver = driverThreadLocal.get();

        if (driver != null) {
            try {
                if (scenario.isFailed()) {
                    logger.error("Escenario falló: {}", scenario.getName());
                }

                logger.info("Cerrando aplicación para escenario: {}", scenario.getName());

                driver.quit();
                logger.info("Aplicación cerrada exitosamente");

            } catch (Exception e) {
                logger.error("Error al cerrar la aplicación: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
}