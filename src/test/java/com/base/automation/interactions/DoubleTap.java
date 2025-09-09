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

public class DoubleTap implements Interaction {
    private final Target target;
    private final Duration pauseBetweenTaps;

    public DoubleTap(Target target, Duration pauseBetweenTaps) {
        this.target = target;
        this.pauseBetweenTaps = pauseBetweenTaps;
    }

    public DoubleTap(Target target) {
        this(target, Duration.ofMillis(200)); // Pausa por defecto
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
        Sequence doubleTapSequence = new Sequence(finger, 1);

        // Primer tap
        doubleTapSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), center.getX(), center.getY()));
        doubleTapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTapSequence.addAction(new Pause(finger, Duration.ofMillis(50)));
        doubleTapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // Pausa entre taps
        doubleTapSequence.addAction(new Pause(finger, pauseBetweenTaps));

        // Segundo tap
        doubleTapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTapSequence.addAction(new Pause(finger, Duration.ofMillis(50)));
        doubleTapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(doubleTapSequence));
    }

    private Point getElementCenter(WebElement element) {
        Point location = element.getLocation();
        int centerX = location.getX() + (element.getSize().getWidth() / 2);
        int centerY = location.getY() + (element.getSize().getHeight() / 2);
        return new Point(centerX, centerY);
    }

    public static DoubleTap on(Target target) {
        return instrumented(DoubleTap.class, target);
    }

    public static DoubleTap on(Target target, Duration pauseBetweenTaps) {
        return instrumented(DoubleTap.class, target, pauseBetweenTaps);
    }
}