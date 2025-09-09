package com.base.automation.ui;

import io.appium.java_client.AppiumBy;
import net.serenitybdd.screenplay.targets.Target;

public class ProductsPage {

    public static final Target PRODUCTS_TITLE = Target.the("Titulo Productos")
            .located(AppiumBy.xpath("//android.widget.TextView[@text=\"PRODUCTS\"]"));

}
