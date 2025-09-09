package com.base.automation.stepsdefinitions;

import com.base.automation.drivers.DriverFactory;
import com.base.automation.hooks.AppiumHooks;
import com.base.automation.tasks.Login;
import com.base.automation.ui.ProductsPage;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.questions.Text;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class LoginSteps {

    private Actor tester;

    @Given("el usuario abre la aplicación")
    public void abrirApp() throws Exception {
        AndroidDriver hisMobileDevice = DriverFactory.createDriver();
        AppiumHooks.setDriver(hisMobileDevice);

        tester = Actor.named("Tester");
        tester.can(BrowseTheWeb.with(hisMobileDevice));
    }

    @When("ingresa sus credenciales {string} y {string}")
    public void login(String username, String password) {
        tester.attemptsTo(Login.withCredentials(username, password));
    }

    @Then("Se debe observar la pantalla de Productos")
    public void seDebeObservarLaPantallaDeProductos() {
        tester.should(seeThat(Text.of(ProductsPage.PRODUCTS_TITLE), equalTo("PRODUCTS")));
    }
}
