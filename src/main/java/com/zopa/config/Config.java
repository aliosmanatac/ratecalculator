package com.zopa.config;

import java.util.Currency;
import java.util.Locale;

public class Config {
    public static final Currency CURRENCY = Currency.getInstance("GBP");
    public static final Locale LOCALE = Locale.UK;
    public static final int LOAN_LENGTH_IN_MONTHS = 36;
    public static final int MAX_LOAN_AMOUNT = 15000;
    public static final int MIN_LOAN_AMOUNT = 1000;
    public static final int LOAN_AMOUNT_MULTIPLIER = 100;

}
