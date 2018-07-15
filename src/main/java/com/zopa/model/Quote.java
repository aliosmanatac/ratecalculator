package com.zopa.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.NumberFormat;

import static com.zopa.config.Config.CURRENCY;
import static com.zopa.config.Config.LOCALE;

@Builder
@Getter
public class Quote {
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");
    private int requestedAmount;
    private BigDecimal rate;
    private BigDecimal monthlyRepayment;
    private BigDecimal totalRepayment;

    @Override
    public String toString() {

        return new StringBuilder()
                .append("Requested amount: ").append(formatCurrency(requestedAmount)).append(LINE_SEPARATOR)
                .append("Rate: ").append(formatPercentage(rate)).append(LINE_SEPARATOR)
                .append("Monthly repayment: ").append(formatCurrency(monthlyRepayment)).append(LINE_SEPARATOR)
                .append("Total repayment: ").append(formatCurrency(totalRepayment)).append(LINE_SEPARATOR)
                .toString();
    }

    private String formatCurrency(final int amount) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(LOCALE);
        numberFormat.setCurrency(CURRENCY);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(amount);
    }

    private String formatCurrency(final BigDecimal amount) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(LOCALE);
        numberFormat.setCurrency(CURRENCY);
        numberFormat.setGroupingUsed(false);
        return numberFormat.format(amount);
    }

    private String formatPercentage(final BigDecimal percent) {
        NumberFormat numberFormat = NumberFormat.getPercentInstance(LOCALE);
        numberFormat.setMinimumFractionDigits(1);
        return numberFormat.format(percent);
    }
}
