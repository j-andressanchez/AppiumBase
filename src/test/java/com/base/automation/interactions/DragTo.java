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
import java.util.Collections;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class DragTo implements Interaction {
    private final Target sourceElement;
    private final Target destinationElement;
    private final Duration duration;

    public DragTo(Target sourceElement, Target destinationElement, Duration duration) {
        this.sourceElement = sourceElement;
        this.destinationElement = destinationElement;
        this.duration = duration;
    }

    public DragTo(Target sourceElement, Target destinationElement) {
        this(sourceElement, destinationElement, Duration.ofMillis(1000));
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver facadeDriver = BrowseTheWeb.as(actor).getDriver();
        RemoteWebDriver driver = (RemoteWebDriver) facadeDriver;

        WebElement source = sourceElement.resolveFor(actor);
        WebElement destination = destinationElement.resolveFor(actor);

        if (source == null) {
            throw new RuntimeException("Elemento origen " + sourceElement.getName() + " no encontrado.");
        }
        if (destination == null) {
            throw new RuntimeException("Elemento destino " + destinationElement.getName() + " no encontrado.");
        }

        Point sourceCenter = getElementCenter(source);
        Point destinationCenter = getElementCenter(destination);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence dragSequence = new Sequence(finger, 1);

        // Mover al elemento origen y presionar
        dragSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), sourceCenter.getX(), sourceCenter.getY()));
        dragSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // Arrastrar al destino
        dragSequence.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), destinationCenter.getX(), destinationCenter.getY()));

        // Soltar
        dragSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(dragSequence));
    }

    private Point getElementCenter(WebElement element) {
        Point location = element.getLocation();
        int centerX = location.getX() + (element.getSize().getWidth() / 2);
        int centerY = location.getY() + (element.getSize().getHeight() / 2);
        return new Point(centerX, centerY);
    }

    public static DragTo drag(Target source, Target destination) {
        return instrumented(DragTo.class, source, destination);
    }

    public static DragTo drag(Target source, Target destination, Duration duration) {
        return instrumented(DragTo.class, source, destination, duration);
    }
}