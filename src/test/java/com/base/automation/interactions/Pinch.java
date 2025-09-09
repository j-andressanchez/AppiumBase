package com.base.automation.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.Arrays;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Pinch implements Interaction {

    public enum Type {
        IN, OUT // Pinch in = zoom out, Pinch out = zoom in
    }

    private final Target target;
    private final Type pinchType;
    private final Duration duration;
    private final int distance;

    public Pinch(Target target, Type pinchType, Duration duration, int distance) {
        this.target = target;
        this.pinchType = pinchType;
        this.duration = duration;
        this.distance = distance;
    }

    public Pinch(Target target, Type pinchType) {
        this(target, pinchType, Duration.ofMillis(1000), 100);
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

        // Calcular posiciones iniciales y finales para los dos dedos
        Point finger1Start, finger1End, finger2Start, finger2End;

        if (pinchType == Type.OUT) {
            // Pinch out: dedos empiezan juntos y se separan (zoom in)
            finger1Start = new Point(center.getX() - 10, center.getY() - 10);
            finger1End = new Point(center.getX() - distance, center.getY() - distance);
            finger2Start = new Point(center.getX() + 10, center.getY() + 10);
            finger2End = new Point(center.getX() + distance, center.getY() + distance);
        } else {
            // Pinch in: dedos empiezan separados y se juntan (zoom out)
            finger1Start = new Point(center.getX() - distance, center.getY() - distance);
            finger1End = new Point(center.getX() - 10, center.getY() - 10);
            finger2Start = new Point(center.getX() + distance, center.getY() + distance);
            finger2End = new Point(center.getX() + 10, center.getY() + 10);
        }

        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

        Sequence finger1Sequence = new Sequence(finger1, 1);
        Sequence finger2Sequence = new Sequence(finger2, 1);

        // Finger 1
        finger1Sequence.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), finger1Start.getX(), finger1Start.getY()));
        finger1Sequence.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        finger1Sequence.addAction(finger1.createPointerMove(duration, PointerInput.Origin.viewport(), finger1End.getX(), finger1End.getY()));
        finger1Sequence.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // Finger 2
        finger2Sequence.addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), finger2Start.getX(), finger2Start.getY()));
        finger2Sequence.addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        finger2Sequence.addAction(finger2.createPointerMove(duration, PointerInput.Origin.viewport(), finger2End.getX(), finger2End.getY()));
        finger2Sequence.addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(finger1Sequence, finger2Sequence));
    }

    private Point getElementCenter(WebElement element) {
        Point location = element.getLocation();
        int centerX = location.getX() + (element.getSize().getWidth() / 2);
        int centerY = location.getY() + (element.getSize().getHeight() / 2);
        return new Point(centerX, centerY);
    }

    public static Pinch in(Target target) {
        return instrumented(Pinch.class, target, Type.IN);
    }

    public static Pinch out(Target target) {
        return instrumented(Pinch.class, target, Type.OUT);
    }

    public static Pinch in(Target target, Duration duration, int distance) {
        return instrumented(Pinch.class, target, Type.IN, duration, distance);
    }

    public static Pinch out(Target target, Duration duration, int distance) {
        return instrumented(Pinch.class, target, Type.OUT, duration, distance);
    }
}