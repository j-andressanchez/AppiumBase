package com.base.automation.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.Collections;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Swipe implements Interaction {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private final Direction direction;
    private final double swipePercentage;
    private final Duration duration;

    public Swipe(Direction direction, double swipePercentage, Duration duration) {
        this.direction = direction;
        this.swipePercentage = swipePercentage;
        this.duration = duration;
    }

    public Swipe(Direction direction) {
        this(direction, 0.8, Duration.ofMillis(500)); // Valores por defecto
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver facadeDriver = BrowseTheWeb.as(actor).getDriver();
        RemoteWebDriver driver = (RemoteWebDriver) facadeDriver;

        Dimension screenSize = driver.manage().window().getSize();
        Point startPoint = calculateStartPoint(screenSize);
        Point endPoint = calculateEndPoint(screenSize, startPoint);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeSequence = new Sequence(finger, 1);

        swipeSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startPoint.getX(), startPoint.getY()));
        swipeSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipeSequence.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), endPoint.getX(), endPoint.getY()));
        swipeSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipeSequence));
    }

    private Point calculateStartPoint(Dimension screenSize) {
        int centerX = screenSize.getWidth() / 2;
        int centerY = screenSize.getHeight() / 2;

        return switch (direction) {
            case UP -> new Point(centerX, (int) (screenSize.getHeight() * 0.8));
            case DOWN -> new Point(centerX, (int) (screenSize.getHeight() * 0.2));
            case LEFT -> new Point((int) (screenSize.getWidth() * 0.8), centerY);
            case RIGHT -> new Point((int) (screenSize.getWidth() * 0.2), centerY);
        };
    }

    private Point calculateEndPoint(Dimension screenSize, Point startPoint) {
        int swipeDistance = (int) (direction == Direction.UP || direction == Direction.DOWN ?
                screenSize.getHeight() * swipePercentage : screenSize.getWidth() * swipePercentage);

        return switch (direction) {
            case UP -> new Point(startPoint.getX(), startPoint.getY() - swipeDistance);
            case DOWN -> new Point(startPoint.getX(), startPoint.getY() + swipeDistance);
            case LEFT -> new Point(startPoint.getX() - swipeDistance, startPoint.getY());
            case RIGHT -> new Point(startPoint.getX() + swipeDistance, startPoint.getY());
        };
    }

    // Factory methods
    public static Swipe up() {
        return instrumented(Swipe.class, Direction.UP);
    }

    public static Swipe down() {
        return instrumented(Swipe.class, Direction.DOWN);
    }

    public static Swipe left() {
        return instrumented(Swipe.class, Direction.LEFT);
    }

    public static Swipe right() {
        return instrumented(Swipe.class, Direction.RIGHT);
    }

    public static Swipe up(double percentage, Duration duration) {
        return instrumented(Swipe.class, Direction.UP, percentage, duration);
    }

    public static Swipe down(double percentage, Duration duration) {
        return instrumented(Swipe.class, Direction.DOWN, percentage, duration);
    }

    public static Swipe left(double percentage, Duration duration) {
        return instrumented(Swipe.class, Direction.LEFT, percentage, duration);
    }

    public static Swipe right(double percentage, Duration duration) {
        return instrumented(Swipe.class, Direction.RIGHT, percentage, duration);
    }
}