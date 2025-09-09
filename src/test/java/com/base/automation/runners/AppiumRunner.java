package com.base.automation.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.base.automation.stepsdefinitions", "com.base.automation.hooks"},
        plugin = {"pretty"}
)
public class AppiumRunner {
}