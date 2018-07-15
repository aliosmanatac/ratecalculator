package com.zopa.calculator;

import com.zopa.model.Offer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class QuoteCalculatorHelperImplTest {

    @InjectMocks
    QuoteCalculatorHelperImpl quoteCalculatorHelper;

    @Test(expected = IllegalArgumentException.class)
    public void calculateRate_nullSet_throwsException() {
        quoteCalculatorHelper.calculateRate(null, 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateRate_negativeAmount_throwsException() {
        quoteCalculatorHelper.calculateRate(new TreeSet<>(), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateRate_zeroAmount_throwsException() {
        quoteCalculatorHelper.calculateRate(new TreeSet<>(), 0);
    }

    @Test
    public void calculateRate_requestMoreThanAvailable_returnsEmptyOptional() {
        SortedSet<Offer> offerSet = new TreeSet<>(Arrays.asList(
                Offer.builder().rate(BigDecimal.valueOf(0.69)).amount(99).lender("L1").build(),
                Offer.builder().rate(BigDecimal.valueOf(0.69)).amount(200).lender("L2").build(),
                Offer.builder().rate(BigDecimal.valueOf(0.69)).amount(300).lender("L3").build()));
        Optional<BigDecimal> result = quoteCalculatorHelper.calculateRate(offerSet, 600);
        assertFalse(result.isPresent());
    }

    @Test
    public void calculateRate_loansOnlyFromOne_returnsEmptyOptional() {
        SortedSet<Offer> offerSet = new TreeSet<>(Arrays.asList(
                Offer.builder().rate(BigDecimal.valueOf(0.5)).amount(200).lender("L1").build(),
                Offer.builder().rate(BigDecimal.valueOf(0.15)).amount(300).lender("L2").build(),
                Offer.builder().rate(BigDecimal.valueOf(0.0069)).amount(101).lender("L3").build()));
        Optional<BigDecimal> result = quoteCalculatorHelper.calculateRate(offerSet, 100);
        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(0.0069), result.get().setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    public void calculateRate_loanFromAll_returnsWeightedAverage() {
        SortedSet<Offer> offerSet = new TreeSet<>(Arrays.asList(
                Offer.builder().rate(BigDecimal.valueOf(0.15)).amount(300).lender("L1").build(),
                Offer.builder().rate(BigDecimal.valueOf(0.5)).amount(200).lender("L2").build(),
                Offer.builder().rate(BigDecimal.valueOf(0.69)).amount(100).lender("L3").build()));
        Optional<BigDecimal> result = quoteCalculatorHelper.calculateRate(offerSet, 600);
        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(0.3567), result.get().setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    public void calculateRate_loansPartial_returnsWeightedAverage() {
        SortedSet<Offer> offerSet = new TreeSet<>(Arrays.asList(
                Offer.builder().rate(BigDecimal.valueOf(0.0015)).amount(10000).lender("L1").build(),
                Offer.builder().rate(BigDecimal.valueOf(0.5)).amount(200).lender("L2").build(),
                Offer.builder().rate(BigDecimal.valueOf(0.69)).amount(100).lender("L3").build()));
        Optional<BigDecimal> result = quoteCalculatorHelper.calculateRate(offerSet, 10100);
        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(0.0064), result.get().setScale(4, RoundingMode.HALF_UP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateDiscountFactor_nullRate_throwsException() {
        quoteCalculatorHelper.calculateDiscountFactor(null, 36);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateDiscountFactor_zeroNumberOfMonths_throwsException() {
        quoteCalculatorHelper.calculateDiscountFactor(BigDecimal.valueOf(0.074), 0);
    }

    @Test
    public void calculateDiscountFactor_validValues_calculatesDiscountFactor() {
        BigDecimal discountFactor = quoteCalculatorHelper.calculateDiscountFactor(BigDecimal.valueOf(0.074), 36);
        assertEquals(BigDecimal.valueOf(32.1954), discountFactor.setScale(4, RoundingMode.HALF_UP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateMonthlyPayments_zeroAmount_throwsExcept() {
        quoteCalculatorHelper.calculateMonthlyPayments(BigDecimal.valueOf(166.654), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateMonthlyPayments_nullDiscountFactor_throwsExcept() {
        quoteCalculatorHelper.calculateMonthlyPayments(null, 1000);
    }

    @Test
    public void calculateMonthlyPayments_validValues_returnMonthlyPayment() {
        BigDecimal monthlyPayment = quoteCalculatorHelper.calculateMonthlyPayments(BigDecimal.valueOf(166.654), 100000);
        assertEquals(BigDecimal.valueOf(600.0456), monthlyPayment.setScale(4, RoundingMode.HALF_UP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateTotalPayments_nullMonthlyPayments_throwsException() {
        quoteCalculatorHelper.calculateTotalPayments(null, 36);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateTotalPayments_zeroNumberOfPayments_throwsException() {
        quoteCalculatorHelper.calculateTotalPayments(BigDecimal.valueOf(10.0), 0);
    }
}