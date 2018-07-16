package com.zopa.calculator;

import com.zopa.model.Offer;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.SortedSet;

public interface QuoteCalculatorHelper {
    /**
     * Calculates rate based on the current offers available
     * @param offerSet Set of available offers
     * @param amount Total amount to loan
     * @return calculated rate as optional if offers satisfy amount.
     * otherwise return empty
     */
    Optional<BigDecimal> calculateRate(SortedSet<Offer> offerSet, int amount);

    /**
     * Calculates discount factor
     * @param rate yearly rate
     * @param numberOfMonths Total number of months of repayment
     * @return Discount factor
     */
    BigDecimal calculateDiscountFactor(BigDecimal rate, int numberOfMonths);

    /**
     * Calculates amount to be repayed each month
     * @param discountFactor Discount factor for the loan
     * @param amount Total amount to loan
     * @return Amount to be repayed each month
     */
    BigDecimal calculateMonthlyPayments(BigDecimal discountFactor, int amount);

    /**
     * Calculates total repayment for the loan
     * @param monthlyPayments Amount repayed each month
     * @param numberOfMonths Total number of months to repay
     * @return Total repayment for the loan
     */
    BigDecimal calculateTotalPayments(BigDecimal monthlyPayments, int numberOfMonths);
}
