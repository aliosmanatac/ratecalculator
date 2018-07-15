package com.zopa.calculator;

import com.zopa.model.Offer;
import com.zopa.model.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.TreeSet;

import static org.springframework.util.Assert.notNull;

@Component
public class MonthlyCompoundInterestLoanCalculator implements QuoteCalculator {

    private QuoteCalculatorHelper quoteCalculatorHelper;

    @Autowired
    public MonthlyCompoundInterestLoanCalculator(final QuoteCalculatorHelper quoteCalculatorHelper) {
        notNull(quoteCalculatorHelper, "quoteCalculatorHelper cannot be null");
        this.quoteCalculatorHelper = quoteCalculatorHelper;
    }

    /**
     * Calculate quote based on monthly compound interest.
     * The formula in https://www.thebalance.com/loan-payment-calculations-315564 is used to calculate the loan
     * Loan Payment = Amount / Discount Factor
     * @param offerSet Available set off offers
     * @param amount requested amount as loan
     * @param numberOfMonths number of months to repay the loan
     * @return Quote if requested amount is available, empty otherwise
     */
    public Optional<Quote> calculateQuote(final TreeSet<Offer> offerSet, final int amount, final int numberOfMonths) {
        Optional<BigDecimal> rateOptional = quoteCalculatorHelper.calculateRate(offerSet, amount);
        if (!rateOptional.isPresent())
            return Optional.empty();

        BigDecimal discountFactor = quoteCalculatorHelper.calculateDiscountFactor(rateOptional.get(), numberOfMonths);
        BigDecimal monthlyPayments = quoteCalculatorHelper.calculateMonthlyPayments(discountFactor, amount);
        BigDecimal totalPayments = quoteCalculatorHelper.calculateTotalPayments(monthlyPayments, numberOfMonths);

        return Optional.of(Quote.builder()
                .rate(rateOptional.get())
                .requestedAmount(amount)
                .monthlyRepayment(monthlyPayments)
                .totalRepayment(totalPayments)
                .build());
    }

}
