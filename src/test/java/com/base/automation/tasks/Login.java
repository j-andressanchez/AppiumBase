package com.base.automation.tasks;

import com.base.automation.interactions.TapOn;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.Actor;

import static net.serenitybdd.screenplay.Tasks.instrumented;

import com.base.automation.ui.LoginPage;

public class Login implements Task {

    private final String username;
    private final String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Login withCredentials(String username, String password) {
        return instrumented(Login.class, username, password);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Enter.theValue(username).into(LoginPage.USERNAME_FIELD),
                Enter.theValue(password).into(LoginPage.PASSWORD_FIELD),
                TapOn.the(LoginPage.LOGIN_BUTTON)
        );
    }
}

