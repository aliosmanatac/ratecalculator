package com.zopa.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    static {
        System.setProperty("java.util.logging.config.file", "configuration/logging/logging.properties");
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(QuoteCalculationApp.class);
        ctx.refresh();
        QuoteCalculationApp quoteCalculationApp = ctx.getBean(QuoteCalculationApp.class);
        System.out.println(quoteCalculationApp.start(args));
    }
}
