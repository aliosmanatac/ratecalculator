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
    int requestedAmount;
    BigDecimal rate;
    BigDecimal monthlyRepayment;
    BigDecimal totalRepayment;

    @Override
    public String toString() {
        String lineSeparator = System.getProperty("line.separator");

        return new StringBuilder()
                .append("Requested amount: ").append(formatCurrency(requestedAmount)).append(lineSeparator)
                .append("Rate: ").append(formatPercentage(rate)).append(lineSeparator)
                .append("Monthly repayment: ").append(formatCurrency(monthlyRepayment)).append(lineSeparator)
                .append("Total repayment: ").append(formatCurrency(totalRepayment)).append(lineSeparator)
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
