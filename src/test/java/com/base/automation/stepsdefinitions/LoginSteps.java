package com.base.automation.stepsdefinitions;

import com.base.automation.drivers.DriverFactory;
import com.base.automation.tasks.Login;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.WebDriver;

public class LoginSteps {

    private Actor tester;
    WebDriver hisMobileDevice;

    @Given("el usuario abre la aplicación")
    public void abrirApp() throws Exception {
        hisMobileDevice = DriverFactory.createDriver();
        tester = Actor.named("Tester");
        tester.can(BrowseTheWeb.with(hisMobileDevice));
    }

    @When("ingresa sus credenciales {string} y {string}")
    public void login(String username, String password) {
        tester.attemptsTo(Login.withCredentials(username, password));
    }

    @Then("debería ver la pantalla principal")
    public void validarPantallaPrincipal() {
        // Aquí podrías verificar que algún elemento de la pantalla principal esté visible
        // Ejemplo:
        // andres.should(seeThat(Text.of(HomePage.WELCOME_LABEL), equalTo("PRODUCTS")));
    }
}
