package com.base.automation.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.Collections;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Scroll implements Interaction {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private final Direction direction;
    private final Target container;
    private final double scrollPercentage;
    private final Duration duration;

    public Scroll(Direction direction, Target container, double scrollPercentage, Duration duration) {
        this.direction = direction;
        this.container = container;
        this.scrollPercentage = scrollPercentage;
        this.duration = duration;
    }

    public Scroll(Direction direction, Target container) {
        this(direction, container, 0.7, Duration.ofMillis(800)); // Valores por defecto
    }

    public Scroll(Direction direction) {
        this(direction, null, 0.7, Duration.ofMillis(800)); // Sin contenedor específico
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver facadeDriver = BrowseTheWeb.as(actor).getDriver();
        RemoteWebDriver driver = (RemoteWebDriver) facadeDriver;

        Point startPoint, endPoint;

        if (container != null) {
            // Scroll dentro de un contenedor específico
            WebElement element = container.resolveFor(actor);
            startPoint = calculateStartPoint(element);
            endPoint = calculateEndPoint(element, startPoint);
        } else {
            // Scroll en toda la pantalla
            Dimension screenSize = driver.manage().window().getSize();
            startPoint = calculateScreenStartPoint(screenSize);
            endPoint = calculateScreenEndPoint(screenSize, startPoint);
        }

        performScroll(driver, startPoint, endPoint);
    }

    private Point calculateStartPoint(WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();

        int centerX = location.getX() + (size.getWidth() / 2);
        int centerY = location.getY() + (size.getHeight() / 2);

        return switch (direction) {
            case UP -> new Point(centerX, location.getY() + (int) (size.getHeight() * 0.8));
            case DOWN -> new Point(centerX, location.getY() + (int) (size.getHeight() * 0.2));
            case LEFT -> new Point(location.getX() + (int) (size.getWidth() * 0.8), centerY);
            case RIGHT -> new Point(location.getX() + (int) (size.getWidth() * 0.2), centerY);
        };
    }

    private Point calculateEndPoint(WebElement element, Point startPoint) {
        Point location = element.getLocation();
        Dimension size = element.getSize();

        int scrollDistance = (int) (direction == Direction.UP || direction == Direction.DOWN ?
                size.getHeight() * scrollPercentage : size.getWidth() * scrollPercentage);

        return switch (direction) {
            case UP -> new Point(startPoint.getX(), startPoint.getY() - scrollDistance);
            case DOWN -> new Point(startPoint.getX(), startPoint.getY() + scrollDistance);
            case LEFT -> new Point(startPoint.getX() - scrollDistance, startPoint.getY());
            case RIGHT -> new Point(startPoint.getX() + scrollDistance, startPoint.getY());
        };
    }

    private Point calculateScreenStartPoint(Dimension screenSize) {
        int centerX = screenSize.getWidth() / 2;
        int centerY = screenSize.getHeight() / 2;

        return switch (direction) {
            case UP -> new Point(centerX, (int) (screenSize.getHeight() * 0.8));
            case DOWN -> new Point(centerX, (int) (screenSize.getHeight() * 0.2));
            case LEFT -> new Point((int) (screenSize.getWidth() * 0.8), centerY);
            case RIGHT -> new Point((int) (screenSize.getWidth() * 0.2), centerY);
        };
    }

    private Point calculateScreenEndPoint(Dimension screenSize, Point startPoint) {
        int scrollDistance = (int) (direction == Direction.UP || direction == Direction.DOWN ?
                screenSize.getHeight() * scrollPercentage : screenSize.getWidth() * scrollPercentage);

        return switch (direction) {
            case UP -> new Point(startPoint.getX(), startPoint.getY() - scrollDistance);
            case DOWN -> new Point(startPoint.getX(), startPoint.getY() + scrollDistance);
            case LEFT -> new Point(startPoint.getX() - scrollDistance, startPoint.getY());
            case RIGHT -> new Point(startPoint.getX() + scrollDistance, startPoint.getY());
        };
    }

    private void performScroll(RemoteWebDriver driver, Point startPoint, Point endPoint) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scrollSequence = new Sequence(finger, 1);

        scrollSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startPoint.getX(), startPoint.getY()));
        scrollSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scrollSequence.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), endPoint.getX(), endPoint.getY()));
        scrollSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(scrollSequence));
    }

    // Factory methods
    public static Scroll up() {
        return instrumented(Scroll.class, Direction.UP);
    }

    public static Scroll down() {
        return instrumented(Scroll.class, Direction.DOWN);
    }

    public static Scroll left() {
        return instrumented(Scroll.class, Direction.LEFT);
    }

    public static Scroll right() {
        return instrumented(Scroll.class, Direction.RIGHT);
    }

    public static Scroll up(Target container) {
        return instrumented(Scroll.class, Direction.UP, container);
    }

    public static Scroll down(Target container) {
        return instrumented(Scroll.class, Direction.DOWN, container);
    }

    public static Scroll left(Target container) {
        return instrumented(Scroll.class, Direction.LEFT, container);
    }

    public static Scroll right(Target container) {
        return instrumented(Scroll.class, Direction.RIGHT, container);
    }
}