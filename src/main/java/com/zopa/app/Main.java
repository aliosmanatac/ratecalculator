package com.zopa.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.util.logging.config.file", "configuration/logging/logging.properties");
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(QuoteCalculationApp.class);
        ctx.refresh();
        QuoteCalculationApp quoteCalculationApp = ctx.getBean(QuoteCalculationApp.class);
        System.out.println(quoteCalculationApp.calculate(args));
    }
}
