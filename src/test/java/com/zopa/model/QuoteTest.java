package com.zopa.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class QuoteTest {

    @Test
    public void toString_happyCase() {
        String lineSeparator = System.getProperty("line.separator");

        Quote quote = Quote.builder()
                .requestedAmount(2468)
                .rate(BigDecimal.valueOf(0.0032))
                .monthlyRepayment(BigDecimal.valueOf(123.45678))
                .totalRepayment(BigDecimal.valueOf(9876.5432))
                .build();
        assertEquals("Requested amount: £2468" + lineSeparator +
                        "Rate: 0.3%" + lineSeparator +
                        "Monthly repayment: £123.46" + lineSeparator +
                        "Total repayment: £9876.54" + lineSeparator,
                quote.toString());
    }
}