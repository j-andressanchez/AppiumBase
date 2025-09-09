package com.base.automation.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.Collections;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class TapOn implements Interaction {
    private final Target target;

    public TapOn(Target target) {
        this.target = target;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver facadeDriver = BrowseTheWeb.as(actor).getDriver();
        RemoteWebDriver driver = (RemoteWebDriver) facadeDriver;

        WebElement element = target.resolveFor(actor);

        if (element == null) {
            throw new RuntimeException("Elemento " + target.getName() + " no encontrado.");
        }

        // Calcula coordenadas del centro del elemento
        Point location = element.getLocation();
        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();
        int centerX = location.getX() + (width / 2);
        int centerY = location.getY() + (height / 2);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapSequence = new Sequence(finger, 1);
        tapSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
        tapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tapSequence.addAction(new Pause(finger, Duration.ofMillis(100)));  // Pausa corta para simular toque
        tapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(tapSequence));
    }

    public static TapOn the(Target target) {
        return instrumented(TapOn.class, target);
    }
}