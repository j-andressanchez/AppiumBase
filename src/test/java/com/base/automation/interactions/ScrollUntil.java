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

public class ScrollUntil implements Interaction {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public enum StopCondition {
        ELEMENT_VISIBLE, ELEMENT_CLICKABLE, ELEMENT_DISAPPEARS
    }

    private final Direction direction;
    private final Target targetElement;
    private final StopCondition condition;
    private final Target container;
    private final double scrollPercentage;
    private final Duration scrollDuration;
    private final int maxScrollAttempts;
    private final Duration waitBetweenScrolls;

    public ScrollUntil(Direction direction, Target targetElement, StopCondition condition,
                       Target container, double scrollPercentage, Duration scrollDuration,
                       int maxScrollAttempts, Duration waitBetweenScrolls) {
        this.direction = direction;
        this.targetElement = targetElement;
        this.condition = condition;
        this.container = container;
        this.scrollPercentage = scrollPercentage;
        this.scrollDuration = scrollDuration;
        this.maxScrollAttempts = maxScrollAttempts;
        this.waitBetweenScrolls = waitBetweenScrolls;
    }

    public ScrollUntil(Direction direction, Target targetElement, StopCondition condition) {
        this(direction, targetElement, condition, null, 0.8,
                Duration.ofMillis(200), 10, Duration.ofMillis(200));
    }

    public ScrollUntil(Direction direction, Target targetElement) {
        this(direction, targetElement, StopCondition.ELEMENT_VISIBLE);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver facadeDriver = BrowseTheWeb.as(actor).getDriver();
        RemoteWebDriver driver = (RemoteWebDriver) facadeDriver;

        for (int attempt = 0; attempt < maxScrollAttempts; attempt++) {
            if (isConditionMet(actor)) {
                return; // Condición cumplida, salir
            }

            try {
                performSingleScroll(driver, actor);
            } catch (Exception e) {
                System.err.println("Error en intento de scroll " + (attempt + 1) + ": " + e.getMessage());
                if (attempt == maxScrollAttempts - 1) {
                    // En el último intento, re-lanzar la excepción
                    throw new RuntimeException("Error en todos los intentos de scroll: " + e.getMessage(), e);
                }
                // Para otros intentos, continuar con el siguiente
            }

            try {
                Thread.sleep(waitBetweenScrolls.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Verificar condición final
        if (!isConditionMet(actor)) {
            throw new RuntimeException(
                    String.format("Element not found after %d scroll attempts in direction %s",
                            maxScrollAttempts, direction)
            );
        }
    }

    private boolean isConditionMet(Actor actor) {
        try {
            WebElement element = targetElement.resolveFor(actor);

            return switch (condition) {
                case ELEMENT_VISIBLE -> element.isDisplayed();
                case ELEMENT_CLICKABLE -> element.isDisplayed() && element.isEnabled();
                case ELEMENT_DISAPPEARS -> false; // Si llegamos aquí, el elemento existe
            };
        } catch (Exception e) {
            // El elemento no se encuentra
            return condition == StopCondition.ELEMENT_DISAPPEARS;
        }
    }

    private void performSingleScroll(RemoteWebDriver driver, Actor actor) {
        Point startPoint, endPoint;

        if (container != null) {
            WebElement element = container.resolveFor(actor);
            startPoint = calculateStartPoint(element);
            endPoint = calculateEndPoint(element, startPoint);
        } else {
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
        scrollSequence.addAction(finger.createPointerMove(scrollDuration, PointerInput.Origin.viewport(), endPoint.getX(), endPoint.getY()));
        scrollSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(scrollSequence));
    }

    // Factory methods básicos
    public static ScrollUntil downToFind(Target element) {
        return instrumented(ScrollUntil.class, Direction.DOWN, element, StopCondition.ELEMENT_VISIBLE);
    }

    public static ScrollUntil upToFind(Target element) {
        return instrumented(ScrollUntil.class, Direction.UP, element, StopCondition.ELEMENT_VISIBLE);
    }

    public static ScrollUntil leftToFind(Target element) {
        return instrumented(ScrollUntil.class, Direction.LEFT, element, StopCondition.ELEMENT_VISIBLE);
    }

    public static ScrollUntil rightToFind(Target element) {
        return instrumented(ScrollUntil.class, Direction.RIGHT, element, StopCondition.ELEMENT_VISIBLE);
    }

    // Factory methods con contenedor
    public static ScrollUntil downToFind(Target element, Target container) {
        return instrumented(ScrollUntil.class, Direction.DOWN, element, StopCondition.ELEMENT_VISIBLE, container, 0.5, Duration.ofMillis(600), 10, Duration.ofMillis(500));
    }

    public static ScrollUntil upToFind(Target element, Target container) {
        return instrumented(ScrollUntil.class, Direction.UP, element, StopCondition.ELEMENT_VISIBLE, container, 0.5, Duration.ofMillis(600), 10, Duration.ofMillis(500));
    }

    // Factory methods con condición específica
    public static ScrollUntil downUntilClickable(Target element) {
        return instrumented(ScrollUntil.class, Direction.DOWN, element, StopCondition.ELEMENT_CLICKABLE);
    }

    public static ScrollUntil upUntilClickable(Target element) {
        return instrumented(ScrollUntil.class, Direction.UP, element, StopCondition.ELEMENT_CLICKABLE);
    }

    public static ScrollUntil downUntilDisappears(Target element) {
        return instrumented(ScrollUntil.class, Direction.DOWN, element, StopCondition.ELEMENT_DISAPPEARS);
    }

    public static ScrollUntil upUntilDisappears(Target element) {
        return instrumented(ScrollUntil.class, Direction.UP, element, StopCondition.ELEMENT_DISAPPEARS);
    }

    // Builder pattern para configuración avanzada
    public static ScrollUntilBuilder builder() {
        return new ScrollUntilBuilder();
    }

    public static class ScrollUntilBuilder {
        private Direction direction = Direction.DOWN;
        private Target targetElement;
        private StopCondition condition = StopCondition.ELEMENT_VISIBLE;
        private Target container;
        private double scrollPercentage = 0.8;
        private Duration scrollDuration = Duration.ofMillis(200);
        private int maxScrollAttempts = 10;
        private Duration waitBetweenScrolls = Duration.ofMillis(80);

        public ScrollUntilBuilder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public ScrollUntilBuilder target(Target targetElement) {
            this.targetElement = targetElement;
            return this;
        }

        public ScrollUntilBuilder condition(StopCondition condition) {
            this.condition = condition;
            return this;
        }

        public ScrollUntilBuilder inContainer(Target container) {
            this.container = container;
            return this;
        }

        public ScrollUntilBuilder scrollPercentage(double scrollPercentage) {
            this.scrollPercentage = scrollPercentage;
            return this;
        }

        public ScrollUntilBuilder duration(Duration scrollDuration) {
            this.scrollDuration = scrollDuration;
            return this;
        }

        public ScrollUntilBuilder maxAttempts(int maxScrollAttempts) {
            this.maxScrollAttempts = maxScrollAttempts;
            return this;
        }

        public ScrollUntilBuilder waitBetween(Duration waitBetweenScrolls) {
            this.waitBetweenScrolls = waitBetweenScrolls;
            return this;
        }

        public ScrollUntil build() {
            if (targetElement == null) {
                throw new IllegalArgumentException("Target element is required");
            }
            return instrumented(ScrollUntil.class, direction, targetElement, condition, container,
                    scrollPercentage, scrollDuration, maxScrollAttempts, waitBetweenScrolls);
        }
    }
}