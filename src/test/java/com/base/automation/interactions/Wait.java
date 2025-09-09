package com.base.automation.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Wait implements Interaction {

    public enum Condition {
        ELEMENT_VISIBLE,
        ELEMENT_CLICKABLE,
        ELEMENT_PRESENT,
        ELEMENT_INVISIBLE,
        ELEMENT_SELECTED,
        TEXT_TO_BE_PRESENT,
        FIXED_TIME
    }

    private final Condition condition;
    private final Target target;
    private final Duration timeout;
    private final String expectedText;

    public Wait(Condition condition, Target target, Duration timeout, String expectedText) {
        this.condition = condition;
        this.target = target;
        this.timeout = timeout;
        this.expectedText = expectedText;
    }

    public Wait(Condition condition, Target target, Duration timeout) {
        this(condition, target, timeout, null);
    }

    public Wait(Condition condition, Duration timeout) {
        this(condition, null, timeout, null);
    }

    public Wait(Duration timeout) {
        this(Condition.FIXED_TIME, null, timeout, null);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver facadeDriver = BrowseTheWeb.as(actor).getDriver();
        RemoteWebDriver driver = (RemoteWebDriver) facadeDriver;

        switch (condition) {
            case ELEMENT_VISIBLE -> waitForElementVisible(driver, actor);
            case ELEMENT_CLICKABLE -> waitForElementClickable(driver, actor);
            case ELEMENT_PRESENT -> waitForElementPresent(driver, actor);
            case ELEMENT_INVISIBLE -> waitForElementInvisible(driver, actor);
            case ELEMENT_SELECTED -> waitForElementSelected(driver, actor);
            case TEXT_TO_BE_PRESENT -> waitForTextToBePresent(driver, actor);
            case FIXED_TIME -> waitFixedTime();
        }
    }

    private void waitForElementVisible(RemoteWebDriver driver, Actor actor) {
        if (target == null) {
            throw new IllegalArgumentException("Target is required for ELEMENT_VISIBLE condition");
        }

        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.visibilityOf(target.resolveFor(actor)));
    }

    private void waitForElementClickable(RemoteWebDriver driver, Actor actor) {
        if (target == null) {
            throw new IllegalArgumentException("Target is required for ELEMENT_CLICKABLE condition");
        }

        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.elementToBeClickable(target.resolveFor(actor)));
    }

    private void waitForElementPresent(RemoteWebDriver driver, Actor actor) {
        if (target == null) {
            throw new IllegalArgumentException("Target is required for ELEMENT_PRESENT condition");
        }

        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(target.getCssOrXPathSelector())));
    }

    private void waitForElementInvisible(RemoteWebDriver driver, Actor actor) {
        if (target == null) {
            throw new IllegalArgumentException("Target is required for ELEMENT_INVISIBLE condition");
        }

        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.invisibilityOf(target.resolveFor(actor)));
    }

    private void waitForElementSelected(RemoteWebDriver driver, Actor actor) {
        if (target == null) {
            throw new IllegalArgumentException("Target is required for ELEMENT_SELECTED condition");
        }

        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.elementToBeSelected(target.resolveFor(actor)));
    }

    private void waitForTextToBePresent(RemoteWebDriver driver, Actor actor) {
        if (target == null || expectedText == null) {
            throw new IllegalArgumentException("Target and expectedText are required for TEXT_TO_BE_PRESENT condition");
        }

        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.textToBePresentInElement(target.resolveFor(actor), expectedText));
    }

    private void waitFixedTime() {
        try {
            Thread.sleep(timeout.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Wait interrupted", e);
        }
    }

    // Factory methods básicos
    public static Wait forSeconds(int seconds) {
        return instrumented(Wait.class, Duration.ofSeconds(seconds));
    }

    public static Wait forMillis(int milliseconds) {
        return instrumented(Wait.class, Duration.ofMillis(milliseconds));
    }

    public static Wait forMinutes(int minutes) {
        return instrumented(Wait.class, Duration.ofMinutes(minutes));
    }

    // Factory methods para elementos
    public static Wait untilVisible(Target element) {
        return instrumented(Wait.class, Condition.ELEMENT_VISIBLE, element, Duration.ofSeconds(10));
    }

    public static Wait untilVisible(Target element, Duration timeout) {
        return instrumented(Wait.class, Condition.ELEMENT_VISIBLE, element, timeout);
    }

    public static Wait untilClickable(Target element) {
        return instrumented(Wait.class, Condition.ELEMENT_CLICKABLE, element, Duration.ofSeconds(10));
    }

    public static Wait untilClickable(Target element, Duration timeout) {
        return instrumented(Wait.class, Condition.ELEMENT_CLICKABLE, element, timeout);
    }

    public static Wait untilPresent(Target element) {
        return instrumented(Wait.class, Condition.ELEMENT_PRESENT, element, Duration.ofSeconds(10));
    }

    public static Wait untilPresent(Target element, Duration timeout) {
        return instrumented(Wait.class, Condition.ELEMENT_PRESENT, element, timeout);
    }

    public static Wait untilInvisible(Target element) {
        return instrumented(Wait.class, Condition.ELEMENT_INVISIBLE, element, Duration.ofSeconds(10));
    }

    public static Wait untilInvisible(Target element, Duration timeout) {
        return instrumented(Wait.class, Condition.ELEMENT_INVISIBLE, element, timeout);
    }

    public static Wait untilSelected(Target element) {
        return instrumented(Wait.class, Condition.ELEMENT_SELECTED, element, Duration.ofSeconds(10));
    }

    public static Wait untilSelected(Target element, Duration timeout) {
        return instrumented(Wait.class, Condition.ELEMENT_SELECTED, element, timeout);
    }

    public static Wait untilTextAppears(Target element, String text) {
        return instrumented(Wait.class, Condition.TEXT_TO_BE_PRESENT, element, Duration.ofSeconds(10), text);
    }

    public static Wait untilTextAppears(Target element, String text, Duration timeout) {
        return instrumented(Wait.class, Condition.TEXT_TO_BE_PRESENT, element, timeout, text);
    }

    // Builder pattern para configuración avanzada
    public static WaitBuilder builder() {
        return new WaitBuilder();
    }

    public static class WaitBuilder {
        private Condition condition = Condition.FIXED_TIME;
        private Target target;
        private Duration timeout = Duration.ofSeconds(10);
        private String expectedText;

        public WaitBuilder condition(Condition condition) {
            this.condition = condition;
            return this;
        }

        public WaitBuilder target(Target target) {
            this.target = target;
            return this;
        }

        public WaitBuilder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public WaitBuilder timeoutSeconds(int seconds) {
            this.timeout = Duration.ofSeconds(seconds);
            return this;
        }

        public WaitBuilder timeoutMillis(int milliseconds) {
            this.timeout = Duration.ofMillis(milliseconds);
            return this;
        }

        public WaitBuilder text(String expectedText) {
            this.expectedText = expectedText;
            return this;
        }

        public Wait build() {
            return instrumented(Wait.class, condition, target, timeout, expectedText);
        }
    }
}