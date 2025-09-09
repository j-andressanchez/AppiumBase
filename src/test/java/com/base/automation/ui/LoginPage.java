package com.base.automation.ui;

import io.appium.java_client.AppiumBy;
import net.serenitybdd.screenplay.targets.Target;

public class LoginPage {

    public static final Target USERNAME_FIELD = Target.the("campo de usuario")
            .located(AppiumBy.accessibilityId("test-Username"));

    public static final Target PASSWORD_FIELD = Target.the("campo de contraseña")
            .located(AppiumBy.accessibilityId("test-Password"));

    public static final Target LOGIN_BUTTON = Target.the("botón de login")
            .located(AppiumBy.accessibilityId("test-LOGIN"));

}
