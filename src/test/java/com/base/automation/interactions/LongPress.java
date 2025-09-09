package com.base.automation.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.Collections;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class LongPress implements Interaction {
    private final Target target;
    private final Duration pressDuration;

    public LongPress(Target target, Duration pressDuration) {
        this.target = target;
        this.pressDuration = pressDuration;
    }

    public LongPress(Target target) {
        this(target, Duration.ofMillis(1000)); // 1 segundo por defecto
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver facadeDriver = BrowseTheWeb.as(actor).getDriver();
        RemoteWebDriver driver = (RemoteWebDriver) facadeDriver;

        WebElement element = target.resolveFor(actor);
        if (element == null) {
            throw new RuntimeException("Elemento " + target.getName() + " no encontrado.");
        }

        Point center = getElementCenter(element);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPressSequence = new Sequence(finger, 1);

        longPressSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), center.getX(), center.getY()));
        longPressSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longPressSequence.addAction(new Pause(finger, pressDuration));
        longPressSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(longPressSequence));
    }

    private Point getElementCenter(WebElement element) {
        Point location = element.getLocation();
        int centerX = location.getX() + (element.getSize().getWidth() / 2);
        int centerY = location.getY() + (element.getSize().getHeight() / 2);
        return new Point(centerX, centerY);
    }

    public static LongPress on(Target target) {
        return instrumented(LongPress.class, target);
    }

    public static LongPress on(Target target, Duration pressDuration) {
        return instrumented(LongPress.class, target, pressDuration);
    }
}