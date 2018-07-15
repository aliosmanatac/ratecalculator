package com.zopa.calculator;

import com.zopa.model.Offer;
import com.zopa.model.Quote;

import java.util.Optional;
import java.util.TreeSet;

public interface QuoteCalculator {
    /**
     * Calculate quote for a qiven set of offers, requested amount and number of months for payment
     * @param offerSet Available set of offers
     * @param amount requested amount as loan
     * @param numberOfMonths number of months to repay the loan
     * @return Quote if available, otherwise empty
     */
    Optional<Quote> calculateQuote(TreeSet<Offer> offerSet, int amount, int numberOfMonths);
}
