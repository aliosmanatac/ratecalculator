package com.zopa.calculator;

import com.zopa.model.Quote;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonthlyCompoundInterestLoanCalculatorTest {

    @Mock
    QuoteCalculatorHelperImpl quoteCalculatorHelperMock;

    @InjectMocks
    MonthlyCompoundInterestLoanCalculator quoteCalculator;

    @Before
    public void setup() {
        when(quoteCalculatorHelperMock.calculateDiscountFactor(any(BigDecimal.class), anyInt())).thenReturn(BigDecimal.valueOf(1.2345));
        when(quoteCalculatorHelperMock.calculateMonthlyPayments(any(BigDecimal.class), anyInt())).thenReturn(BigDecimal.valueOf(456.7));
        when(quoteCalculatorHelperMock.calculateTotalPayments(any(BigDecimal.class), anyInt())).thenReturn(BigDecimal.valueOf(6789));
    }

    @Test
    public void calculateQuote_helperReturnsEmptyRate_returnEmptyQuote() {
        when(quoteCalculatorHelperMock.calculateRate(any(SortedSet.class), anyInt())).thenReturn(Optional.empty());
        Optional<Quote> quoteOptional = quoteCalculator.calculateQuote(new TreeSet<>(), 500, 36);
        assertFalse(quoteOptional.isPresent());
    }

    @Test
    public void calculateQuote_helperReturnsValidRate_returnsQuote() {
        when(quoteCalculatorHelperMock.calculateRate(any(SortedSet.class), anyInt())).thenReturn(Optional.of(BigDecimal.valueOf(32.1)));
        Optional<Quote> quoteOptional = quoteCalculator.calculateQuote(new TreeSet<>(), 500, 36);
        assertTrue(quoteOptional.isPresent());
        assertEquals(500, quoteOptional.get().getRequestedAmount());
        assertEquals(BigDecimal.valueOf(32.1), quoteOptional.get().getRate());
        assertEquals(BigDecimal.valueOf(456.7), quoteOptional.get().getMonthlyRepayment());
        assertEquals(BigDecimal.valueOf(6789), quoteOptional.get().getTotalRepayment());
    }
}